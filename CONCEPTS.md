# EventSwarm Concepts

To get the most out of EventSwarm you should understand the concepts
that underpin the implementation. We’ll keep it high level here: for the
nitty gritty detail, you should read and understand the source or ask
questions: for now, raise github issues with your questions.

## Events

An `Event` is the basic unit of streaming data in EventSwarm. An event
records information relating to some occurrence out in the real or
virtual world. To qualify as an event, the data must have (or be
assigned):

1.  An identifyable source (typically domain name or URL)

2.  A timestamp (we prefer source timestamps, but will allocate if
    required)

3.  A unique id (we will assign a UUID if there is no natural id)

Raw events are considered atomic, immutable and point-in-time. We never
modify them.

There are a few related concepts that are worth understanding:

-   A `SubEvent` is a component of an event that you want to process
    independently but is fundamentally part of its parent. A line item
    on an invoice, for example. In most cases, a SubEvent will share the
    timestamp and source of its parent.

-   An `Activity` is a set of events that capture some behaviour of
    interest, for example, a tweet and all of its retweets. Activities
    have duration defined by the time span from the oldest to newest
    events in the set. Once created, activitites are also immutable.

The Java class for an event is usually determined by the underlying data
format. So we have `CSVEvent`, `JsonEvent`, `XmlEvent` etc. Attributes
of an event that we want to use in our processing graph are extracted
from these raw events using a `ValueRetriever`. You can think of a
`ValueRetriever` as a view over an event. We prefer not to write new
classes for each new content source (although this is a more recent
principle, so you will find contrary examples in our source).

Thus when adding a new source of data, you should only have to define a
new `Event` class if we don’t have an existing class for the wire format
produced by the source. You will, however, need to define retrievers for
each of the data elements that need to be accessed in expressions,
filters etc. This approach allows us to program incrementally, only
defining retrievers for the data you need to use. In many cases, the
out-of-the-box retrievers defined by the event class will be sufficient:
you just need to identify the field name/path and type.

## Triggers and Actions

Event processing components are connected using *triggers* and
*actions*. Upstream components offer triggers, downstream components
have actions. Actions are registered against one or more upstream
triggers. The connection of actions to triggers defines the edges of
your processing graph. Triggers and actions are paired by type, and the
most important pairing is the `AddEventTrigger` and `AddEventAction`.
This pair supports the flow of events through your processing graph.

For CEP, it is also necessary to explicitly remove events. For example,
if you use a time window and an event ‘falls out’ of the window, the
window needs to tell the downstream expressions and components. This is
achieved through the `RemoveEventTrigger` and `RemoveEventAction` pair.

When defining matching rules, you need a mechanism to signal that a
logical expression has matched. This is done using the
`EventMatchTrigger` and `EventMatchAction` pair for single event
matches, and a `ComplexExpressionMatchTrigger` and
`ComplexExpressionMatchAction` for expressions that match multiple
events (e.g. sequence, and). [^1]

There are other triggers and actions but the above are the most common
and important.

## EventSets

### EventSet

The core aggregation construct in EventSwarm is the `EventSet`. It is
largely equivalent to a tuple set in an SQL database, and collects an
arbitrary group of events in an order consistent with time. An
`EventSet` is agnostic to the underlying event types: if you only want
tweets, use a filter to make sure the `EventSet` only contains tweets.
It ignores duplicates based the event ids, so make sure your ids are
truly unique.

The `EventSet` class is used throughout EventSwarm, and the semantics of
an EventSwarm processing graph depends intrinsically on the correctness
of the implementation. So we have only one implementation. If you need
to define a new kind of `EventSet`, create a subclass or talk to us
about addressing your needs by modifying the existing implementation. If
you replace `EventSet`, you’re in dangerous territory.

### Windows

Windows are bounded EventSets and are used to limit the scope of your
matches and control memory usage. Typically, bounds are based on time
(e.g. a sliding time window) or number of events. In almost all cases,
the correct behaviour of these windows requires them to ignore upstream
removes.

EventSwarm provides a variety of window types that you can use to build
your processing logic, but some key implementations are:

-   `DiscreteTimeWindow`, which is a sliding time window that keeps the
    last N seconds (or minutes or hours) of events. This time window is
    driven by event timestamps: an event will remain in the window until
    a new event is added whose timestamp is more than N seconds ahead.
    The data-driven nature of this EventSet has many advantages:

    -   it avoids proliferation of threads and timers

    -   it keeps the semantics clean, correct and simple

    -   the semantics is the same for both real time delivery and for
        replay (think about this one, it’s a biggie)

    But for windows that are updated infrequently, this is sometimes not
    enough (i.e. events will sit around forever), so ...

-   A `ClockedTimeWindow` is a sliding time window that keeps the last N
    seconds of events, but is driven by an external tick source. The
    tick source can be the system clock, or alternatively, can be driven
    by timestamps from events in a more frequently updated `EventSet`
    via the `EventClock` implementation. The latter is preferred,
    keeping us fully data driven and retaining the advantages of a
    `DiscreteTimeWindow`.

-   A `BoundedDiscreteTimeWindow` is a sliding time window with an upper
    limit on the number of events that will be held. So for example, you
    want to maintain a one-hour window of `#omg` tweets, but are willing
    to discard older events once there are more than 10,000 tweets in
    the window. This compromise allows you to control memory usage.

-   A `LastNWindow` is a sliding window that keeps the last N events
    received. This window is typically used to constrain memory usage.

-   An `AtMostNWindow` is a sliding window that keeps at most N events,
    but accepts and executes upstream removes. We typically use this
    window for capturing expression matches and maintaining counts.

## Expressions

Expressions describe things that you want to match in a stream of
events. There are lots provided in the EventSwarm library and you’ll
almost certainly think of others that we don’t have. That said, there
are some basic components that you will often use:

-   A `Matcher` is a class with a single function that accepts an event
    as a parameter and returns true or false. For example, a keyword
    matcher would return true if a text field of an event contains a
    specified keyword. Typically, a `Matcher` uses a `ValueRetriever` to
    extract data from an event and compare it with a constant value or
    compute a logical expression on that value.

-   An `Expression` is a class that implements the `AddEventAction` and
    passes on any events that satisfy the expression via an
    `ExpressionMatchTrigger` or `AddEventTrigger`. Most of the time, you
    will define a `Matcher` and use the `EventMatcherExpression`
    provided by EventSwarm. EventSwarm also provides a bunch of useful
    wrapper expressions, for example `EventLogicalAND`,
    `EventLogicalOR`, `EventLogicalNOT` etc.

-   A `ComplexExpression` is an expression that matches multiple events.
    For example, a tweet followed by a retweet. There are two key
    ComplexExpression classes provided by EventSwarm:

    -   The `ANDExpression`, which is satisfied when it collects a set
        of events that match the component expressions.

    -   The `SequenceExpression`, which is satisfied when it collects a
        set of events that match the component expressions and are
        strictly ordered in time (i.e. timestamp(a) \< timestamp(b) \<
        timestamp(c) …)

    You might be thinking ‘where is the OR expression’. So here’s the
    scoop: a logical OR is true whenever you have a single match, so you
    don’t need a complex expression. You can just use the
    EventLogicalOR, and if you want to keep track of all matches, give
    your EventLogicalOR instance an EventSet to hold the matches.

    There is a missing link in this picture, and that’s negation (i.e.
    logical NOT across a set of events). Negation over multiple events
    is complex in data stream processing, and most of the time there are
    better ways to skin your particular cat. You can simulate a logical
    NOT by collecting matches for an expression in an EventSet and
    testing that it is empty at suitable points. The best way I can
    explain the problem is as follows:

    When dealing with time series data from many sources, your data set
    is invariably incomplete and data will arrive out of order. If you
    want to detect that a pattern has *not* matched, you have to wait a
    while to be sure you have the data, and even then you can’t be sure.
    Thus there is little certainty in a logical NOT expresssion.

-   We provide two wrapper expressions, *TrueTransition* and
    *FalseTransition*, that allow you to react on the transition of your
    expression from true to false or false to true respectively.
    Transitions are often important, for example, I want to know when
    the stock price is above \$2.50 but I only need to be told when the
    transition occurs, not every trade above \$2.50.

## Abstractions

Abstractions perform calculations or compute derived values over an
`EventSet`. For example, EventSwarm provides a `StatisticsAbstraction`
that maintains statistics on numeric values extracted from individual
events by a `ValueRetriever`. Most of our abstractions, including the
`StatisticsAbstraction`, are incremental, that is, they are updated
incrementally as data flows. 

EventSwarm also supports static
abstractions that have to be calculated on a complete EventSet but these
are discouraged for real-time data streams. You should only use static
abstractions for queries or calculations that are executed infrequently
on snapshots (i.e. you don’t want to generate alerts based on these
calculations).

## Powersets

A *powerset* is a set of subsets. In EventSwarm, you can think of a
powerset as the equivalent of an SQL `GROUP BY` clause, except that a
single event can belong to many subsets. We provide two implementations:

-   A `HashPowerset` implements a powerset with non-overlapping subsets.
    You provide an `EventKey` implementation (similar to a
    `ValueRetriever`: in future versions `EventKey` will probably be
    deprecated) to extract the hash key from an event, and the
    `HashPowerset` will group your events into distinc `EventSet`
    instances using that key. So for example, you might want to group
    tweets by author.

-   A `MultiHashPowerset` implements a powerset with overlapping
    subsets. You provide an `EventKeys` implementation that extracts an
    array of keys from an event, and the `MultiHashPowerset` will put
    the event into the `EventSet` corresponding to each key returned by
    the `EventKeys`. For example, you might want to group tweets by
    hashtag.

Powersets provide hooks for you to add expressions to each subset and
provide a factory to create the `EventSet` instances.

There is also a special class of expression called a
`PowersetExpression`. One of the biggest performance hits in EventSwarm
is Java object instance construction. For powersets where there are
many, short-lived subsets, creation of an expression for each subset can
have high overheads. For example, monitoring call records in a telco and
grouping by calling party, then by called party to detect if a caller
has made more than one call to the same number in a 10-minute window.
Even a small telco has millions of customers, so the implications of
this are significant. In these cases, it is more efficient to maintain a
set of pre-configured, empty expressions in a factory and reevaluate
against the entire subset when a new event is added. This behaviour is
implemented by `PowersetExpression`. A `PowersetExpression` also
provides some other conv.

Powersets *prune* subsets by default, that is, when a subset becomes
empty, it is removed. Constant pruning has high overheads, so disable it
as required.

## Channels

Channels provide mechanisms to get events into and out of EventSwarm.

### Input

Input channels read data from an input source and create events.
Typically, the channel will accept an event factory object to construct
event instances from data items in the stream. Input channels are
specific to a wire format (e.g. CSV, JSON), but we try to abstract over
the communication medium. Most input channels can read from a Java
`InputStream` instance but we do have specific HTTP channels for
receiving XML and JSON.

It’s probably worth mentioning here that using factories to recycle
events and avoid constructor overheads is not generally feasible for
EventSwarm. To make it feasible, the interface contracts would need to
require each component to ‘register’ event references so we can garbage
collect. The added complexity and overheads are quite likely to exceed
any performance advantages gained from recycling. We might revisit this
in future though.

### Output

The focus of EventSwarm is on processing and generating alerts or taking
actions. As such, we do not offer many event output channels. This is
changing as we increasingly look to push events outwards from
EventSwarm. At present, however, only an HTTPSender channel is offered
in Java. Note that our ruby gem implements email and sms alerts arising
from events.

Our `kafka-processor` application provides a way to pull JSON events off 
a topic and write matches to your expressions on another topic. At present,
this is the simplest way to integrate EventSwarm data stream processing with
other applications. 

## Threading and parallelism

By default, EventSwarm traverses the processing graph synchronously
without threading. Until you outstrip your IO capability and the
bandwidth of a single CPU core, this has the lowest latency. It’s not
hard to get 20,000 events/sec through a non-trivial processing graph on
a single core. On older magnetic disk drives we're typically IO bound, but 
SSDs have significantly improved IO speeds so if you have a slower processor
you could end up CPU bound.

Threading and the associated synchronisation can have non-trivial
overheads, so test your app as a synchronous graph first. We typically
reserve threading for the following circumstances:

1.  For downstream processing behind a powerset (i.e. after the map step
    in map/reduce)

2.  For segregating high-latency IO operations

3.  For handling distinct input streams

The rule of thumb is to split the data first, then use threads to
process the segments. A map/reduce pattern often works well, provided
the reduce step is operating on a much smaller data set. 

When you are ready to go multi-threaded, EventSwarm provides two
mechanisms: the `AddEventQueue` class queues events for execution of
downstream processing in a thread. This queue implements the
`AddEventTrigger`/`AddEventAction` alone and provides a simple and
relatively efficient threading mechanism. If you need to pass other
Trigger/Action pairs, we provide a slightly more complex implementation
in the `ThreadedActionExecutor` that can queue any action and is
oriented towards powersets in particular. This is an area of active work
for us, so expect some new classes here.

If you're using our `kafka-processor`, use a separate topic and a distinct 
expression for different things you want to match. Each expression will 
operate in a separate thread so it should scale quite well and you have 
the option to run multiple containers. We will be adding
explicit support for kafka partitions in a future version. 

[^1]: *Note that in practice, the behaviour of these expression
    trigger/action pairs can usually be implemented using
    *`AddEventTrigger`*/*`AddEventAction`* pairs and it is likely that
    we will deprecate expression match trigger/action pairs.*


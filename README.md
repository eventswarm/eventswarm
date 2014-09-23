# EventSwarm

Eventswarm is a lightweight, in-memory, complex event processing (CEP)
library for Java with minimal dependencies. It focuses on data-driven,
low-latency processing and is designed for embedding in applications,
even mobile applications: the jar file is around 350K without any
compression applied.

## Why EventSwarm?

There are some good, open source, complex event processing tools like
[Esper][] and nowadays [[Drools](http://drools.org).][] These tools
provide a domain specific language (DSL), runtime environment and many
capabilities out-of-the-box.

So why might you want to use EventSwarm?

-   **Control**

    EventSwarm is a Java class library using a small set of interfaces
    to connect components into a data-driven processing graph. Through
    component selection and graph structuring, you control latency,
    memory usage, distribution and threading.

-   **Minimal dependencies**

    EventSwarm depends on log4j, opencsv, org.json and
    java-uuid-generator. If you need XML, then add your favourite
    implementation. That’s all.

-   **Extensibility**

    If you need a component that doesn’t exist or doesn’t suit your
    requirements, you can add it. The interface contracts are simple.
    You can even modify our implementations if you dare, and we’d love
    it if you contribute your changes back to the project.

-   **Scale**

    While EventSwarm does not provide pre-built scaling configurations
    out-of-the-box, the data-driven nature of an EventSwarm processing
    graph allows you to distribute graph segments in a fairly arbitrary
    manner using whichever distribution infrastructure you choose (e.g.
    Storm, Kafka). The key to processing scalability is partitioning,
    and EventSwarm has natural graph abstractions for partitioning. With
    a few exceptions, changing the graph segmentation does not change
    the processing logic. Given it’s lightweight nature, EventSwarm
    opens up the possibility of pushing your processing all the way out
    to the data sources. This means scale is only really limited by your
    ability to partition your application.

-   **Familiar language and tools**

    We’re not big fans of DSLs for heavy-duty programming tasks. CEP
    DSLs are complex beasts and require a fully-fledged development
    environment and robust build/test/deploy processes. DSLs require
    that your developers learn a new language, perhaps learn a different
    development toolchain and understand complex and sometimes arcane
    runtime semantics. CEP is also an order of magnitude more complex
    than SQL because you add a time dimension and distribution
    semantics.

    With EventSwarm, simple things are simple. You can build incredibly
    complex processing graphs and rules, but the basic concepts are
    easily understood and programmed in a familiar language with
    familiar tools. EventSwarm itself is Java but you can also use our
    Ruby [gem][] or integrate with your favourite JVM-based scripting
    language.

-   **Stability**

    We’ve been building applications with EventSwarm for about 7 years.
    It’s changed a lot in that time and has a few warts to prove it, but
    its proven to be a very stable platform. Our most mature application
    [Note8](https://note8.com.au) is mostly only restarted for upgrades.
    Sometimes when we’re tardy with our enhancements, this period is
    several months.

## So I’m sold, what next?

For a quick start, read our [getting started](./GETTING\_STARTED.md)
document.

To better understand the EventSwarm concepts, read our [key
concepts](./CONCEPTS.md) document. This is a must-read if you get
serious about it.

If you want to know more about the history of EventSwarm, read our
[history](./HISTORY.md) document.

To get down and dirty, "use the source Luke".

To contribute, fork the repo and send us pull requests when you have
something to share. We’ll ask you to sign a copy of our [contribution
agreement](./CONTRIBUTORS\_AGREEMENT.txt) so that everyone can use your
work freely (it’s the Apache one with the names changed).

  [Esper]: http://esper.org
  [[Drools](http://drools.org).]: #
  [gem]: http://github.com/eventswarm/revs
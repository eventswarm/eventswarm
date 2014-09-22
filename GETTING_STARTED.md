# Getting Started

EventSwarm is built using maven, so the best way to get it is through
adding a dependency to your `pom.xml`. Otherwise, download the jar or
source and add it to your project dependencies.

The remainder of this doc shows iterations of a simple app for
monitoring stock market trades. We’re incrementally modifying the app to
do more complex analysis, and hopefully giving you an idea of how
EventSwarm can be used.

Now let’s try a simple app:

    import com.eventswarm.channels.CSVChannel
    import com.eventswarm.abstractions.StatisticsAbstraction

    public static void main(String args[]) {

    }

This app processes the contents of a CSV file (simulated stock market
data) and calculates the average trade price. Pretty dumb though,
because we really want the average price on a per-stock basis. So let’s
make it better:

    import com.eventswarm.channels.CSVChannel
    import com.eventswarm.abstractions.StatisticsAbstraction
    import com.eventswarm.powersets.HashPowerset;

    public static void main(String args[]) {

    }

As you can see, a powerset splits your data stream based on a key. In
this case the key is simple (stock code), but keys can be as complex as
you want them to be.

So what if we wanted to detect when a trade occurs at a price that’s a
bit high? We can compare the price of an individual trade with the
average and alert when it’s more than 1 standard deviation above the
mean. The code:

    import com.eventswarm.channels.CSVChannel
    import com.eventswarm.abstractions.StatisticsAbstraction
    import com.eventswarm.powersets.HashPowerset;

    public static void main(String args[]) {

    }

For stock market trading this is still pretty simplistic, but hopefully
you’re starting to get the idea. What if you wanted to detect when the
price was high *and* the volume (number of shares traded) was high? The
code:

    import com.eventswarm.channels.CSVChannel
    import com.eventswarm.abstractions.StatisticsAbstraction
    import com.eventswarm.powersets.HashPowerset;

    public static void main(String args[]) {

    }

In these examples we’ve demonstrated basic from concepts in EventSwarm
and built a simple app.


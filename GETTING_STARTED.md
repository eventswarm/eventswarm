# Getting Started

The easiest way to get started with EventSwarm is to use our 
[kafka-processor](https://github.com/eventswarm/kafka-processor) in 
a docker container. This processor is a JRuby application that reads
JSON from a kafka topic and writes matches to a second kafka topic. 

There are a couple of sample expressions built using Ruby in the `./rules` directory, 
and you can add your own rules by mounting your own rules over this directory. 

For something more complicated, you can either use the core Java package directly
or our [revs](https://github.com/eventswarm/revs) JRuby gem. 

## EventSwarm core 

The EventSwarm core is built using maven, so the best way to get it is through
adding a dependency to your `pom.xml`. Otherwise, download the jar or
source and add it to your project dependencies.

## EventSwarm social

[eventswarm-social](https://github.com/eventswarm/eventswarm-social) provides a set
of Java classes to assist in pulling data from Twitter or pubsubhubbub feeds 
(e.g. https://superfeedr.com). This is a bit of a work-in-progress and there's not a
lot there right now. Use it as you see fit.

## Revs gem

[revs](https://github.com/eventswarm/revs) is a JRuby gem that provides utilities and 
wrappers for using EventSwarm in a JRuby application. JRuby is somewhat less verbose
than Java and can be evaluated dynamically (hence its usage in the `kafka-processor`). 

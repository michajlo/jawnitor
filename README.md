# jawnitor

Because DSLs are fun and MBeans aren't

[![Build Status](https://travis-ci.org/michajlo/jawnitor.png)](https://travis-ci.org/michajlo/jawnitor)

## Theory of operation

The general idea here is you define some MBeans and their attributes
to retrieve using a DSL, and jawnitor does just that.  It then does
the good dead of dumping them into a file somewhere (via slf4j
perhaps?) so that your favorite monitoring tool, spread sheet editor,
or scientific calculation library can pull them in and have their way
with them.

But wait, aren't there like 100,000 tools out there that can read
MBeans off of my server via remote JMX anyway? Yes, there are, but
they need access to your machine, and need to be separately
configured with each iteration of your app. By pluggin jawnitor into
your app you don't need all that extra coordination and open ports
and stuff.

## Vision

Further down the road you can also manipulate these values, or add
new ones before outputting them. This way you can do cool stuff like
combine your metrics in some cool way into one super metric, or track
the differential or integral term of some metric over time and base
output on that.

## TODOs

Clearly this isn't done yet, but for a minimum viable product, not
too much else is required. I'm sure more stuff will pop up, but
generally, only the following are probably necessary:

  - define and implement main entry point
  - fill in output stuff


# Cooperari

*[Latin meaning: "to cooperate"](https://en.wiktionary.org/wiki/cooperari)*

Bugs in multithreaded applications are elusive
 to detected, trace and replicate, given the usual non-determinism and
non-reproducibility of preemptive, coarse-grained scheduling decisions.
Simple bugs can easily be undetected under preemptive semantics,
and in particular [**heinsenbugs**](https://en.wikipedia.org/wiki/Heisenbug) are  common in multithreaded applications.

Cooperari is a tool for deterministic testing of multithreaded Java applications. It is based on **cooperative semantics**: the code is instrumented on-the-fly such that threads voluntarily suspend (yield) at interference points, called **yield points**, and code  between two consecutive yield points of each thread always executes serially as a
transaction.  A cooperative scheduler takes over control at
yield points and deterministically selects the next thread to run.

Cooperari integrates with [**JUnit 4**](http://junit.org/junit4) such that each test case in a JUnit test suite runs multiple times, until it either fails or the state-space of schedules is deemed as covered by a configurable policy that is responsible for the scheduling decisions.  Beyond failed assertions in software tests, deadlocks and races are also detected as soon as they are exposed in the cooperative execution.


# History and evolution 

The first implementation of Cooperari (archived [here](https://bitbucket.org/edrdo/cooperari/wiki/Home)) dates back to 2014 and is described by the following paper:

* Eduardo R.B. Marques, F. Martins, and M. Simões, [**Cooperari: a tool for cooperative testing of multithreaded Java applications**](papers/pppj14.pdf), Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ'14), **DOI**: [10.1145/2647508.2647523](https://doi.org/10.1145/2647508.2647523)


This repository contains a completely revamped and refactored version of the original Cooperari implementation, even if the general design principles are the same.  After from refactoring and general improvements, 
the main differences to the original versions are as follows:

- Yield point support improved and extended:
  - Monitor operations (acquisition and release, `wait`, `notify`, `nofityAll`, ...).
  - `java.lang.Thread` life-cycle methods.
  - Access to object data fields (and race detection). 
  - Calls to methods in `java.util.concurrent.AtomicXXX` classes. 
  - Calls to methods in the `sun.misc.Unsafe` class.
- Other features:
  - Improved race condition and deadlock detection mechanisms.
  - Tests can probe for the reachability of code "hotspots", asserting
that certain "hotspots" are always, never, or sometimes reached.
  - Custom command-line JUnit test suite runner.
  - Yield point coverage reports are now generated.
- Aspect-oriented code instrumentation:
  - The standard [AspectJ](https://www.eclipse.org/aspectj/) runtime is used    instead of the [AspectBench](http://www.sable.mcgill.ca/abc/) compiler.
  - Code does not need to weaved (instrumented) ahead-of-time any longer, 
[load-time weaving](https://www.eclipse.org/aspectj/doc/released/devguide/ltw.html) is used instead. 
- Cooperari now works with Java 8 rather than Java 7. Future support for Java &gt; 8 code will depend on the availability and backward compability of internal JRE features in classes like `com.sun.misc.Unsafe` (this issue has not been analyzed yet, use Oracle's JDK 8 in conjunction with Cooperari).

# License

Cooperari is open-source software under the terms of the [MIT License](LICENSE.md). 

Cooperari is developed and maintained by [Eduardo R. B. Marques](http://www.dcc.fc.up.pt/~edrdo).  The original version and design incorporated contributions from Miguel Simões and [Francisco Martins](http://www.di.fc.ul.pt/~fmartins).

# Getting started

## Requirements

Cooperari requires Oracle's [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
It may also work with OpenJDK 8, but it has not been fully tested in that
environment.

## Binary distribution 
Download one of the latest binary distribution packages here at [GitHub](link).

Extract the distribution package file and follow the instructions
in the provided `README.txt`.

## Compiling from scratch

You may clone this repository to compile Cooperari from scratch. 
In addition to Oracle's JDK 8, [Maven](https://maven.apache.org) 3.0 or higher will be required to compile Cooperari.

After cloning, execute `mvn package` in the `cooperari` directory.
This will compile all files, execute tests, and generate the Coooperari JAR and distribution files.

## Maven Central

Cooperari artifacts will be available at Maven Central in future releases.

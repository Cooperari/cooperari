
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub release](https://img.shields.io/github/release/Cooperari/cooperari.svg)](https://github.com/Cooperari/cooperari/releases)
[![Travis build status](https://api.travis-ci.org/Cooperari/cooperari.png?branch=master)](https://travis-ci.org/Cooperari/cooperari)

# Cooperari

*[Latin meaning: "to cooperate"](https://en.wiktionary.org/wiki/cooperari)*

Bugs in multithreaded applications are elusive
 to detect, trace, and replicate, given the usual non-determinism and
non-reproducibility of preemptive, coarse-grained scheduling decisions.
Simple bugs can easily be undetected under preemptive semantics,
and in particular [**heinsenbugs**](https://en.wikipedia.org/wiki/Heisenbug) are  common in multithreaded applications.

Cooperari is a tool for deterministic testing of multithreaded Java applications. It is based on **cooperative semantics**: the code is instrumented on-the-fly (using [**AspectJ**](https://www.eclipse.org/aspectj/) load-time weaving) such that threads voluntarily suspend (yield) at interference points, called **yield points**, and code  between two consecutive yield points of each thread always executes serially as a transaction.  A cooperative scheduler takes over control at
yield points and deterministically selects the next thread to run.

Cooperari integrates with [**JUnit 4**](http://junit.org/junit4) such that each test case in a JUnit test suite runs multiple times, until it either fails or the state-space of schedules is deemed as covered by a configurable policy that is responsible for the scheduling decisions. Fine-grained cooperative execution traces can then be inspected to understand test failures. Beyond failed assertions in software tests, deadlocks and field access data races are also detected as soon as they are exposed in the cooperative execution. 


## Installing and running Cooperari

Read the [Getting Started guide](GettingStarted.md).
	
## Compiling from scratch

You may clone this repository to compile Cooperari from scratch. 
In addition to a Java 8 JDK, [Maven](https://maven.apache.org) 3.0 or higher will be required to compile Cooperari. After cloning, execute `mvn package` in the `cooperari` directory. This will compile all files, execute tests, and generate the Coooperari JAR and distribution files.

	git clone git@github.com:Cooperari/cooperari.git
	cd cooperari
	mvn package
	
## Inception and evolution 

The first implementation of Cooperari (archived [here](https://bitbucket.org/edrdo/cooperari/wiki/Home)) dates back to 2014 and is described by the following paper:

* E. R. B. Marques, F. Martins, and M. Simões, [**Cooperari: a tool for cooperative testing of multithreaded Java applications**](papers/pppj14.pdf), Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ'14), **DOI**: [10.1145/2647508.2647523](https://doi.org/10.1145/2647508.2647523)

Cooperari is currently developed and maintained by [Eduardo R. B. Marques](http://www.dcc.fc.up.pt/~edrdo).  The original version and design incorporated contributions from Miguel Simões and [Francisco Martins](http://www.di.fc.ul.pt/~fmartins).  This repository contains an almost complete re-implementation and evolution of the original version, even if the general design principles are the same, along with important extensions and generally quite 
more robust operation.  The main technical improvements to the original version are as follows:

- Yield point support improved and extended:
  - Monitor operations (acquisition and release, `wait`, `notify`, `nofityAll`, ...).
  - `java.lang.Thread` life-cycle methods (`start`, `join`, `interrupt`, `stop`, ...).
  - Access to object data fields (and race detection). 
  - Calls to methods in `java.util.concurrent.AtomicXXX` classes. 
  - Calls to methods in the `sun.misc.Unsafe` class.
- Other features:
  - Improved race condition and deadlock detection mechanisms.
  - Tests can probe for the reachability of code "hotspots", asserting
that certain "hotspots" are always, never, or only sometimes reached.
  - Custom command-line JUnit test suite runner.
  - Yield point coverage reports are now generated.
- Aspect-oriented code instrumentation:
  - The standard [AspectJ](https://www.eclipse.org/aspectj/) distribution is used, rather than the [AspectBench](http://www.sable.mcgill.ca/abc/) compiler.
  - Code does not need to weaved (instrumented) ahead-of-time any longer, 
[load-time weaving](https://www.eclipse.org/aspectj/doc/released/devguide/ltw.html) is used instead. 
- Cooperari now works with Java 8 rather than Java 7. Future support for Java &gt; 8 code will depend on the availability and backward compatibility of internal JRE features, e.g., classes like `sun.misc.Unsafe` (this issue has not been analyzed yet).

## License

Copyright 2014-2021 Eduardo R. B. Marques

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.


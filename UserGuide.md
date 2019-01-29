# Getting started

This short guide describes how to install and make use of Cooperari.

## Installation

### Pre-requisites

Cooperari requires [Oracle's Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). It may also work with OpenJDK 8, but it has not been thoroughly tested in that environment.

Configure your `PATH` as:

	export PATH=<oracle jdk 8 installation path>/bin:$PATH

### Download and install Cooperari

Start by downloading the latest binary release archive at [GitHub](https://github.com/Cooperari/cooperari/releases) in TGZ or ZIP form and then unpack it, i.e.:

	tar xzf cooperari-VERSION-release.tgz
	cd cooperari-VERSION
	ls

or 

	unzip cooperari-VERSION-release.zip
	cd cooperari-VERSION
	ls

You should obtain the following listing

	LICENSE.txt	bin		doc		examples	lib

Add Cooperari's `bin` directory to your `PATH`.

	export PATH=<cooperari install dir>/bin:$PATH

### Release contents

* `LICENSE.txt`: the Cooperari license;
* `bin`: executable scripts;
* `lib`: JAR files;
* `doc`: documentation including the Cooperari API documentation in `doc/javadoc`;
* `examples`: a few examples to help you get started.
	
## Running the examples

### Provided examples

The source code of the examples can be found in `examples/src`. The current release includes the following `org.cooperari.examples.X` items

* `DiningPhilosophers`: the classical dining philosophers scenario;
* `RaceDetection`: illustrates race detection features;
* `AtomicPrimitives`: simple example employing `java.util.concurrent.AtomicXXX` API;
* `SunMiscUnsafe`: a variant of `AtomicPrimitives` that uses `sun.misc.Unsafe` insteaad;
* `BuggySemaphore`: additional example comprising a buggy semaphore implementation; 
* `All`: a JUnit test suite that groups all tests, if you wish to execute them all at once.

## (Re-)compiling the examples

The examples are already initially compiled in the `classes` directory. If you change their source code and wish to recompile, then execute 

	cjavac

in the top-level `examples` directory.
 

### Running an example

Let's say you wish to run the `DiningPhilosophers` example. Then in the top-level `examples` directory you should execute: 

	cjunit org.cooperari.examples.DiningPhilosophers

You will obtain as output something as follows:


```text
Configuring load-time weaving ...
JAR file for 'org.cooperari.examples.DiningPhilosophers' saved to './cdata/org.cooperari.examples.DiningPhilosophers-cooperari.jar'
== Cooperari 0.2-SNAPSHOT - JUnit test execution - mode: cooperative ==
org.cooperari.examples.DiningPhilosophers
  test_4_Philosophers_V1                                  [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 3 time: 138 ms coverage: 76.5 % (13 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V1.3.trace.log'
  test_4_Philosophers_V2                                  [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 1 time: 27 ms coverage: 52.9 % (9 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V2.1.trace.log'
  test_8_Philosophers                                     [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 69 time: 5411 ms coverage: 70.6 % (12 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/org.cooperari.examples.DiningPhilosophers/test_8_Philosophers.69.trace.log'
== Summary ==
Executed: 3; Skipped: 0;  Failed: 3; Execution time: 5671 ms
== Yield point coverage ==
Coverage rate: 94.1 % (16 / 17 yp)
Global coverage report: '/Users/edrdo/development/cooperari/target/ cooperari-0.2-SNAPSHOT/examples/all_yield_points.coverage.log'
```





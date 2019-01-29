# Getting started

This short guide describes how to install and start making use of Cooperari.

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

Let's say you wish to run the `DiningPhilosophers` example. Then, in the top-level `examples` directory, you should execute: 

	cjunit org.cooperari.examples.DiningPhilosophers

You will obtain as output something as follows:


```text
Configuring load-time weaving ...
JAR file for 'org.cooperari.examples.DiningPhilosophers' saved to './cdata/org.cooperari.examples.DiningPhilosophers-cooperari.jar'
== Cooperari 0.2-SNAPSHOT - JUnit test execution - mode: cooperative ==
org.cooperari.examples.DiningPhilosophers
  test_4_Philosophers_V1                                  [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 3 time: 128 ms coverage: 76.5 % (13 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/cdata/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V1.3.trace.log'
  test_4_Philosophers_V2                                  [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 1 time: 24 ms coverage: 52.9 % (9 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/cdata/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V2.1.trace.log'
  test_8_Philosophers                                     [failed: org.cooperari.errors.CMultipleExceptionsError]
    > trials: 69 time: 6034 ms coverage: 70.6 % (12 / 17 yp)
    > failure trace: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/cdata/org.cooperari.examples.DiningPhilosophers/test_8_Philosophers.69.trace.log'
== Summary ==
Executed: 3; Skipped: 0;  Failed: 3; Execution time: 6277 ms
== Yield point coverage ==
Coverage rate: 94.1 % (16 / 17 yp)
Global coverage report: '/Users/edrdo/development/cooperari/target/cooperari-0.2-SNAPSHOT/examples/cdata/all_yield_points.coverage.log'
```

You may run the example again and obtain similar results, given
that the cooperative execution will be **deterministic and repeatable**. 

In any case, what happened? Every test in the example failed after a certain number of trials. 

* For each failed test, the output indicates the location of a log file containing the cooperative execution trace for the trial that failed (the last one). These are text files located in the `cdata` directory with a tab-separated-values format, detailing the sequence of steps that lead to the test failure.

* You may inspect the `cdata` directory to find coverage logs also, indicating
which yield points have been reached in the execution of each test. The top level `all_yield_points.coverage.log` is an aggregate coverage log of all tests.

### Running an example preemptively

Using the `cjunitp` script (instead of `cjunit`), you may also run an example with normal preemptive semantics and without any instrumentation. For instance:

```
$ cjunitp org.cooperari.examples.DiningPhilosophers
== Cooperari 0.2-SNAPSHOT - JUnit test execution - mode: preemptive ==
org.cooperari.examples.DiningPhilosophers
  test_4_Philosophers_V1                                  [passed]
    > trials: 20 time: 16 ms
  test_4_Philosophers_V2                                  [passed]
    > trials: 20 time: 50 ms
  test_8_Philosophers                                     [passed]
    > trials: 100 time: 234 ms
== Summary ==
Executed: 3; Skipped: 0;  Failed: 0; Execution time: 364 ms
```

The possible deadlock in the dining philosophers fails to "show up". 
It could happen that it eventually "materializes", but very rarely so. It is 
more frequent to observe failures in the other provided examples though,
but in any case non-deterministically (sometimes bugs are revealed, sometimes not!).

### Trace files for cooperative execution

Fragment of an example you may find in `cdata/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V2.1.trace.log`

```
# THREADS
TID     NAME    CLASS
0       test_4_Philosophers_V2  org.cooperari.junit.CJUnitRunner$MethodRunner
1       Philosopher-0   org.cooperari.examples.DiningPhilosophers$Philosopher
2       Philosopher-1   org.cooperari.examples.DiningPhilosophers$Philosopher
3       Philosopher-2   org.cooperari.examples.DiningPhilosophers$Philosopher
4       Philosopher-3   org.cooperari.examples.DiningPhilosophers$Philosopher
# EXECUTION TRACE
#       TID     STEP    EVENT   SOURCE FILE     LINE    YIELD POINT     STAGE
0       0       0       -       <system>        0       <started>       0
...
15      1       0       -       <system>        0       <started>       0
16      2       0       -       <system>        0       <started>       0
17      3       0       -       <system>        0       <started>       0
18      4       0       -       <system>        0       <started>       0
...
22      4       2       -       DiningPhilosophers.java 60      <monitorenter>  0
23      1       3       -       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)      0
24      3       1       -       DiningPhilosophers.java 60      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.leftFork)       0
25      4       3       -       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)      0
26      3       2       -       DiningPhilosophers.java 60      <monitorenter>  0
27      2       1       -       DiningPhilosophers.java 60      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.leftFork)       0
28      2       2       -       DiningPhilosophers.java 60      <monitorenter>  0
29      2       3       -       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)      0
30      3       3       -       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)      0
31      3       4       -       DiningPhilosophers.java 61      <monitorenter>  0
32      2       4       -       DiningPhilosophers.java 61      <monitorenter>  0
33      4       4       -       DiningPhilosophers.java 61      <monitorenter>  0
34      2       4       D       DiningPhilosophers.java 61      <monitorenter>  0
35      3       4       D       DiningPhilosophers.java 61      <monitorenter>  0
36      4       4       D       DiningPhilosophers.java 61      <monitorenter>  0
37      1       4       D       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)      0
...
# STACK TRACE FOR FAILURE
org.cooperari.errors.CMultipleExceptionsError: 4 exceptions:
 org.cooperari.feature.monitor.CResourceDeadlockError: L3/Philosopher-0/DiningPhilosophers.java:61 > L2/Philosopher-2/DiningPhilosophers.java:61 > L1/Philosopher-3/DiningPhilosophers.java:61 > L0/Philosopher-0/DiningPhilosophers.java:61 > L3/Philosopher-1/DiningPhilosophers.java:61
 org.cooperari.feature.monitor.CResourceDeadlockError: L3/Philosopher-0/DiningPhilosophers.java:61 > L2/Philosopher-2/DiningPhilosophers.java:61 > L1/Philosopher-3/DiningPhilosophers.java:61 > L0/Philosopher-0/DiningPhilosophers.java:61 > L3/Philosopher-1/DiningPhilosophers.java:61
 org.cooperari.feature.monitor.CResourceDeadlockError: L3/Philosopher-0/DiningPhilosophers.java:61 > L2/Philosopher-2/DiningPhilosophers.java:61 > L1/Philosopher-3/DiningPhilosophers.java:61 > L0/Philosopher-0/DiningPhilosophers.java:61 > L3/Philosopher-1/DiningPhilosophers.java:61
 org.cooperari.feature.monitor.CResourceDeadlockError: L3/Philosopher-0/DiningPhilosophers.java:61 > L2/Philosopher-2/DiningPhilosophers.java:61 > L1/Philosopher-3/DiningPhilosophers.java:61 > L0/Philosopher-0/DiningPhilosophers.java:61 > L3/Philosopher-1/DiningPhilosophers.java:61
```

### Coverage log files

Example that you may find in `cdata/org.cooperari.examples.DiningPhilosophers/test_4_Philosophers_V2.coverage.log`:

```
# YIELD POINT COVERAGE
TOTAL   COVERED %
17      9       52.94117647058823
# YIELD POINTS
COVERED SOURCE FILE     LINE    SIGNATURE
Y       DiningPhilosophers.java 53      field-set(org.cooperari.examples.DiningPhilosophers.Philosopher.leftFork)
Y       DiningPhilosophers.java 54      field-set(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)
Y       DiningPhilosophers.java 55      field-set(org.cooperari.examples.DiningPhilosophers.Philosopher.hasEaten)
Y       DiningPhilosophers.java 60      <monitorenter>
Y       DiningPhilosophers.java 60      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.leftFork)
Y       DiningPhilosophers.java 61      <monitorenter>
Y       DiningPhilosophers.java 61      field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.rightFork)
N       DiningPhilosophers.java 62      field-set(org.cooperari.examples.DiningPhilosophers.Philosopher.hasEaten)
N       DiningPhilosophers.java 63      <monitorexit>
Y       DiningPhilosophers.java 64      <monitorexit>
N       DiningPhilosophers.java 105     org.cooperari.examples.DiningPhilosophers.Philosopher.start()
N       DiningPhilosophers.java 109     org.cooperari.examples.DiningPhilosophers.Philosopher.join()
N       DiningPhilosophers.java 113     field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.hasEaten)
Y       DiningPhilosophers.java 128     org.cooperari.CSystem.forkAndJoin(Runnable[])
N       DiningPhilosophers.java 131     field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.hasEaten)
N       DiningPhilosophers.java 148     org.cooperari.CSystem.forkAndJoin(Runnable[])
N       DiningPhilosophers.java 151     field-get(org.cooperari.examples.DiningPhilosophers.Philosopher.hasEaten)
```
 





# Cooperari

## What? Why? How?

Cooperari is a tool for cooperative testing of multithreaded Java applications,

For further details refer 

This repository contains a completely revamped version of the original Cooperari implementation, even if the same general design principles are maintained. 
The major differences to the original versions are as follows:

* Yield point support improved and extended.
* The standard AspectJ runtime is used instead of the abc compiler.
Code is instrumented using load-time aspect weaving rather than
ahead-of-time weaving. 
* Code works with Java 8 rather than Java 7.

## Getting started

### Requirements

Cooperari requires Oracle's [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
It may also work with OpenJDK 8, but it has not been fully tested in that
environment.

### Binary distribution 
Download the binary distribution packages here at [GitHub](link).

Extract the distribution package file and follow the instructions
in the provided `README.txt`.

### Maven Central

Cooperari artifacts will be available in Maven Central in future releases.

### Compiling from scratch

You may clone this repository to compile Cooperari from scratch. 
Maven 3.0 or higher will be required. 

After cloning, execute `mvn package` in the `cooperari` directory.
This will compile all files, execute associated testes, and generate the Coooperari JAR and distribution files.



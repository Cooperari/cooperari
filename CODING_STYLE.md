# Coding style

The source code of Cooperari closely follows the [Google Java Style](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html) with the following variations:

* Instance fields are named in camel-case fashion, but are prefixed with an underscore (`'_'`).  The aim is to prevent prevent shadowing declarations and to distinguish without ambiguity instance fields from other definitions (e.g., local method variables) in any IDE/text editor.
 
* Definitions exposed through the `org.cooperari` package (the top-level main package used by client code) are prefixed with `C` (classes, interfaces, enumerations, annotations, constants) or `c` (e.g., static class methods in the `CSystem` class) to prevent namespace pollution/clashing.  The same scheme is used in definitions of sub-packages of `org.cooperari` if name clashes are likely to arise (e.g., `CThread`).





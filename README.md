Fuzzer
======

Simple Fuzzy Logic Tool for Java 8.

## Documentation
* [Syntax and Semantics](https://github.com/umeding/fuzzer/blob/master/doc/sections/syntax.md)
* [Implementation Details](https://github.com/umeding/fuzzer/blob/master/doc/sections/algorithm.md)
* [To do](https://github.com/umeding/fuzzer/blob/master/TODO.md)
* [Licensing](https://github.com/umeding/fuzzer/blob/master/LICENSE)

### Getting started, the simple way

Fuzzer and a Fuzzer Maven plugin is available through the Central Repository <http://search.maven.org>

Group Id | Artifact Id | Version
--- | --- | ---
[`com.uwemeding`](http://search.maven.org/#browse%7C629099076) | [`fuzzer`](http://search.maven.org/#browse%7C247213999) | [1.0](http://search.maven.org/#artifactdetails%7Ccom.uwemeding%7Cfuzzer%7C1.0%7Cjar)
[`com.uwemeding`](http://search.maven.org/#browse%7C629099076) | [`fuzzer-maven-plugin`](http://search.maven.org/#browse%7C1942614669) | [1.0](http://search.maven.org/#artifactdetails%7Ccom.uwemeding%7Cfuzzer-maven-plugin%7C1.0%7Cmaven-plugin)


### Getting started, for development, debugging, poking around

Fuzzer is fully written in Java 8+ and its project is fully managed by
[Maven](http://maven.apache.org "Maven") (v. 3).

### How to install it

In order to install and use Fuzzer, download the source code by cloning it via your Git client:

```bash
git clone git@github.com:umeding/fuzzer.git
```

Fuzzer uses [Maven](http://maven.apache.org "Maven") to manage the
project, therefore your need to compile the Java source file and
generate the Fuzzer `.jar` archive by typing the following commands:

```bash
cd $FUZZER_DIR
mvn clean package -Pstandalone
```

### How to use it

You simply need to create a fuzzy rules file and start of the program:

```bash
java -jar $FUZZER_DIR/target/fuzzer-XXXXX-all.jar SomeRules.fpl
```

### Generated files

At the end of the execution of the Java application, Fuzzer will
generate Java source file(s) implementing the fuzzy rules.


### Development/debugging
You should be able to use a modern IDE to compile and debug Fuzzer.
The latest versions of Netbeans, and IntelliJ work without issues.
Eclipse should work as well, but I have not tested it, YMMV.


### Future works/TODO

There are a number of TODOs and additional features that need to be
implemented to round out the current (simple) functionality. My intent
is to keep Fuzzer small and simple.

### License

Copyright (c) 2014, Uwe Meding

Fuzzer is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Fuzzer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Fuzzer. If not, see <http://www.gnu.org/licenses/>.

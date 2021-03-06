>#Fuzzer

Simple Fuzzy Logic Tool for Java 8.

## Documentation
* [Syntax and Semantics](https://github.com/umeding/fuzzer/blob/master/doc/sections/syntax.md)
* [Implementation Details](https://github.com/umeding/fuzzer/blob/master/doc/sections/algorithm.md)
* [To Do](https://github.com/umeding/fuzzer/blob/master/TODO.md)
* [Licensing](https://github.com/umeding/fuzzer/blob/master/LICENSE)

### Getting started, the simple way

Fuzzer and a Fuzzer Maven plugin is available through the Central Repository <http://search.maven.org>

Group Id | Artifact Id | Version
--- | --- | ---
`com.uwemeding` | `fuzzer` | <a href="http://search.maven.org/#artifactdetails%7Ccom.uwemeding%7Cfuzzer%7C1.0%7Cjar" target="_blank">1.0</a>
`com.uwemeding` | `fuzzer-maven-plugin` | <a href="http://search.maven.org/#artifactdetails%7Ccom.uwemeding%7Cfuzzer-maven-plugin%7C1.0%7Cmaven-plugin" target="_blank">1,0</a>


### Getting started, for development, debugging, poking around

Fuzzer is fully written in Java 8+ and its project is fully managed by
[Maven](http://maven.apache.org "Maven") (v. 3).

### How to install it

In order to install and use Fuzzer, download the source code by cloning it via your Git client:

```bash
$ git clone git@github.com:umeding/fuzzer.git
```

Fuzzer uses [Maven](http://maven.apache.org "Maven") to manage the
project, therefore your need to compile the Java source file and
generate the Fuzzer `.jar` archive by typing the following commands:

```bash
$ cd $FUZZER_DIR
$ mvn clean package -Pstandalone
```

### Development/debugging
You should be able to use a modern IDE to compile and debug Fuzzer.
The latest versions of Netbeans, and IntelliJ work without issues.
Eclipse should work as well, but I have not tested it, YMMV.


### How to use it

You simply need to create a fuzzy rules file and start of the program:

```bash
$ java -jar $FUZZER_DIR/target/fuzzer-XXXXX-all.jar SomeRules.fpl
```

### Generated files

At the end of the execution of the Java application, Fuzzer will
generate Java source file(s) implementing the fuzzy rules.


### Future works/TODO

There are a number of TODOs and additional features that need to be
implemented to round out the current (simple) functionality. My intent
is to keep Fuzzer small and simple.

### Other Resources/Background
* <a href="https://www.calvin.edu/~pribeiro/othrlnks/Fuzzy/home.htm" target="_blank">Getting started with Fuzzy Logic</a>
* <a href="http://www.amazon.com/Fuzzy-Logic-Engineering-Applications-Third/dp/047074376X" target="_blank">Fuzzy Logic with Engineering Applications, 3<sup>rd</sup> Ed. (Amazon)</a>
* <a href="https://www.youtube.com/watch?v=H9SikB7HbSU" target="_blank" title="Fuzzy Sets">Fuzzy sets - A Primer (YouTube)</a>
* <a href="https://www.youtube.com/watch?v=R4TPFpYXvS0" target="_blank" title="Fuzzy Logic Control Example">Fuzzy Logic Control Example (YouTube)</a>


### License

Copyright (c) 2014 Meding Software Technik

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

Syntax
======

<a name="top"></a>Content
-------
* [Back to Overview](https://github.com/umeding/fuzzer/blob/master/README.md)
* [Lexical Conventions](#lexical)
* [Functions](#functions)
* [Inputs/Outputs](#ios)
* [Reasoning](#reasoning)
* [Rules](#rules)

## <a name="lexical"></a>Lexical Conventions ##
The basic lexical conventions used by Fuzzer are similar to those in
the Java or C programming language. Fuzzer is a case-sensitive
language. All keywords are in lowercase.

### Whitespace
White space can contain the characters for blanks, tabs, newlines, and
form feeds. These characters are ignored except when they serve to
separate other tokens. However, blanks and tabs are significant in
strings.

White space characters are :

* Blank spaces
* Tabs
* Carriage returns
* New-line
* Form-feeds

### Comments
There are two forms to introduce comments.

* Single line comments begin with the token `//` and end with a carriage
return 
* Multi line comments begin with the token `/*` and end with the
token `*/`

### <a name="identifiers"></a>Identifiers (<small>[^Top](#top)</small>)
Identifiers are names used to give an object, such as a hedge or a
function or a rule, a name so that it can be referenced from other
places in a description.

* Identifiers must begin with an alphabetic character or the
underscore character (`a-z A-Z _ `)
* Identifiers may contain alphabetic characters, numeric characters,
the underscore, and the dollar sign (`a-z A-Z 0-9 _ $`)

### Case sensitivity
Fuzzer programs are case-sensitve.

* All lower case letters are unique from upper case letters
* All Fuzzer keywords are lower case

### Numbers in Fuzzer
You can specify constant numbers in integer or real formats. Both
signed and unsigned numbers are supported. In general, number handling
is fairly relaxed and are mostly used in their real representation
internally. 

__Example__

```
/*
 * Simple multiline comment.
 */
program Simple {

    // Hedges
    hedge very(x) -> x^2;

    // Functions
    function S(x) piecewise A,B,C {
          .. A -> 0;
        A .. B -> 2*((x-A)/(C-A))^2;
        B .. C -> 1-2*((x-A)/(C-A))^2;
        C ..   -> 1;
    }
}
```

## <a name="program"></a>Program (<small>[^Top](#top)</small>)
A `program` is the main building block in a Fuzzer description.
Programs wrap all the the fuzzy logic descriptions for the desired
functionalities. One program per file is expected.

### Hedges
Linguistic hedges are operators that can be applied to the primary
input variables of a Fuzzer program. They allow to control the degree
to which a variable belongs to a member set. The expression syntax can
be found here: 
[Apache JEXL reference](http://commons.apache.org/proper/commons-jexl/reference/syntax.html)

__Example__
```
    hedge very(x) -> x^2;
    hedge slightly(x) -> (x^1.2) && (1.0-(x^2.0));
```
![Degree of membership](https://github.com/umeding/fuzzer/raw/master/doc/sections/hedges.png "Degree of membership")

In the above example, there are two linguistic hedges: `very` which
concentrates, whereas `slightly` dialates the degree of membership.

### <a name="functions"></a>Functions (<small>[^Top](#top)</small>)
#### Piecewise Functions
Piecewise functions can be defined to describe custom membership shapes for
the input/output variables. 

__Example__
```
function S(x) piecewise A,B,C {
      .. A -> 0;
    A .. B -> 2*((x-A)/(C-A))^2;
    B .. C -> 1-2*((x-A)/(C-A))^2;
    C ..   -> 1;
}
```
The above example defines an "S" style transition. The parameters
`A, B, C` are used to describe the shape of the transition. The
expression syntax can be found here: 
[Apache JEXL reference](http://commons.apache.org/proper/commons-jexl/reference/syntax.html)

#### External Functions
External functions refer to (single value) methods in Java. 

__Example__
```
function triangle(x) external com.example.FuzzyShapes.triangle(x);
```
The above example defines a reference to a triangular shape
transition. The corresponding Java code would look like this:
```java
package com.example;
public class FuzzyShapes {
    /**
     * Triangular shape transition.
     * @param x  input value
     * @return   value with respect to a triangle
     */
    public static double triangle(double x) {
        // implementation
    }
}
```


### <a name="ios"></a>Inputs/Outputs (<small>[^Top](#top)</small>)
* Range, Step

__Example__
```
output veloc(-5 .. 5 step 0.1) {
    nb = {-5,1} {-2,1} {-1,0};
    z = {-2,1} {0,1} {1,0};
    pb = S[1,3,5](x);
}
```

### <a name="reasoning"></a>Reasoning (<small>[^Top](#top)</small>)
Fuzzer supports the following reasoning methods:
#### Max-min inference method
takes the minimum value of the antecedents: &#181;A AND &#181;B = min { &#181;A, &#181;B }

#### Max-Dot (also max-product) inference
takes the product of the antecedents: &#181;A AND &#181;B = &#181;A * &#181;B.
      
__Note:__ This is also the default if nothing is specified.

__Example__
```
reasoning max-dot;
```

### <a name="rules"></a>Rules (<small>[^Top](#top)</small>)
* Input references
* Conjunction (`and`)
* Disjunction (`or`)

__Examples__
```
rule r1(theta is very nb) {
    veloc = nb;
}
```


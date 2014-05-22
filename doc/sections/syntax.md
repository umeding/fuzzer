Syntax
======

<a name="top"></a>Content
-------
* [Back to Overview](https://github.com/umeding/fuzzer/blob/master/README.md)
* [Lexical Conventions](#lexical)
* [Program](#program)

## <a name="lexical"></a>Lexical Conventions ##
The basic lexical conventions used by Fuzzer are similar to those in
the Java or C programming language. Fuzzer is a case-sensitive
language. All keywords are in lowercase.

### Whitespace ###
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

### Comments ###
There are two forms to introduce comments.

* Single line comments begin with the token `//` and end with a carriage
return 
* Multi line comments begin with the token `/*` and end with the
token `*/`

### <a name="identifiers"></a>Identifiers (<small>[^Top](#top)</small>) ###
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

### Example ###

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

## <a name="program"></a>Program ##
A `program` is the main building block in a Fuzzer description.
Programs wrap all the the fuzzy logic descriptions for the desired
functionalities. One program per file is expected.

### Hedges ###
Hedges

### Functions ### 
Functions
* Piecewise
* External

### Inputs/Outputs ###
* Range, Step
#### Example ####
```
output veloc(-5 .. 5 step 0.1) {
    nb = {-5,1} {-2,1} {-1,0};
    z = {-2,1} {0,1} {1,0};
    pb = S[1,3,5](x);
}
```

### Reasoning ###
* Max/min
* Max/Dot

### Rules ###
* Input references
* Conjunction (`and`)
* Disjunction (`or`)`
#### Examples ####
```
rule r1(theta is very nb) {
    veloc = nb;
}
```


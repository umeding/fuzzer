Syntax
======

Content
-------
* [Example](#example)
* [Identifiers](#identifiers)

### Lexical Conventions ###
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

### <a name="identifiers"></a>Identifiers ###
Identifiers are names used to give an object, such as a hedge or a
function or a rule, a name so that it can be referenced from other
places in a description.

* Identifiers must begin with an alphabetic character or the
underscore character (`a-z A-Z _ `)
* Identifiers may contain alphabetic characters, numeric characters,
the underscore, and the dollar sign (`a-z A-Z 0-9 _ $`)


### <a name="example"></a>Example ###

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

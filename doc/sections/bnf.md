>#Fuzzer BNF

####**1. Source Text**
```
<source_text>
    ::= [ <package_decl> ] <program>

<package_decl>
    ::= package <IDENTIFIER> ;

<program>
    ::= program <name_of_program> { <program_item>* }

<name_of_program>
    ::= <IDENTIFIER>

<program_item>
    ::= <hegde_decl>
    ||= <function_decl>
    ||= <variable_decl>
    ||= <rules_decl>
    ||= <reasoning_decl>
```

####**General**
```
<comment>
    ::= <short_comment>
    ||= <long_comment>

<short_comment>
    ::= // <comment_text> <END-OF-LINE>

<long_comment>
    ::= /* <comment_text> */

<comment_text>
    ::= The comment text is zero or more ASCII characters
```


```
<IDENTIFIER>
    An identifier is any sequence of letters, digits, dollar signs ($),
    and underscore symbol, except that the first must be a letter or
    the underscore; the first character may not be a digit or $. Upper and
    lower case letters are considered to be different.
```

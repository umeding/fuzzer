                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */




%{
#undef YY_DECL
#define YY_DECL int mylex()
#include <string.h>
#include <stdlib.h>
#include "defs.h"
#include "y.tab.h"
extern int yylineno;
char *mkstr();
%}

achar								[A-Za-z]
qual								short|long|unsigned
decl								byte|int|float|double
digit               [0-9]
num                 {digit}+(\.{digit}+)?
name                [<>]|{achar}({achar}|{digit})*
ccode               \{([^\}]|\n)*\}
comment             \/\/.*
ws                  [ \t]*
%%
PROGRAM               return(PROGRAM);
FUNCTION              return(FUNCTION);
INPUT									return(INPUT);
OUTPUT								return(OUTPUT);
TYPE                  return(TYPE);
RANGE                 return(RANGE);
FROM									return(FROM);
TO                    return(TO);
STEP                  return(STEP);
MEMBERS               return(MEMBERS);
FUZZY                 return(FUZZY);
RULE                  return(RULE);
IF                    return(IF);
THEN                  return(THEN);
ALSO                  return(ALSO);
IS                    return(IS);
HEDGE                 return(HEDGE);
REASONING							return(REASONING);
"MAX-DOT"							return(MAXDOT);
"MAX-MIN"							return(MAXMIN);
"("                   return(LBR);
")"                   return(RBR);
"="                   return(EQUALS);
AND                   return(AND);
OR                    return(OR);
","                   return(COMMA);
"+"										return(PLUS);
"-"										return(MINUS);
"*"										return(MULT);
"/"										return(DIV);
"^"										return(EXP);
"||"									return(LOR);
"&&"									return(LAND);
{qual}                { yylval.name = mkstr(yytext,yyleng); return(QUAL); }
{decl}                { yylval.name = mkstr(yytext,yyleng); return(DECL); }
{num}                 { yylval.val = atof(yytext); return(NUM); }
{name}                { yylval.name = mkstr(yytext,yyleng); return(NAME); }
{ws}                  ;
\n                    { yylineno++; }
{ccode}               { yylval.name = mkstr(yytext,yyleng); return(CCODE); }
{comment}             ;
.                     return(yytext[0]);
%%

char *mkstr(name,len)
char *name;
int len;
{
	char *s;
	for(s=strchr(name,'\n'); s != NULL; s=strchr(s,'\n'))
		{ yylineno++; s++; }
	s = (char *) calloc(len+1,sizeof(char));
	strcpy(s,name);
	return s;
}


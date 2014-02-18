                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */



%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "defs.h"

/* External routines */
extern int mylex();
extern FILE *yyin;
extern char *yytext;

extern NODE *create_var();
extern void add_hedge();
extern void printall();
extern FILE *safe_open();

extern void initargs(), saveargs(), add_fcall();
extern FCALL *getargs();

extern void initpairs(), savepairs(), add_memb();
extern MEMB *getpairs();

extern void initfdefs(), savefdefs(), add_fdef();
extern FDEF *getfdefs();

extern NODE *add_mark(), *add_num(), *add_op();
extern float eval();

extern void new_rule();
extern int do_cond(), do_and(), do_or();

extern void init(), merge();
extern void maxdot(), maxmin();
extern void globals(), locals(), calc_crisp();

int yyparse();

/* Global variables */
/*int yylineno=1;*/
extern int yylineno;
int param_num;
char errmsg[70];
NODE *symbtab = NULL;
MEMB *tm = NULL;
FCALL *tf = NULL;
FDEF *fd = NULL;
NODE *curr_var = NULL;
FILE *fp_main, *fp_rule;
char *source = (char *)NULL;
/* #define YYDEBUG 1 */
%}

%union {
			 int ch;
			 char *name;
			 float val;
			 NODE *nd;
       }

%token PROGRAM
%token FUNCTION    MEMBERS   COMMA     FUZZY
%token INPUT       OUTPUT    TYPE      RANGE      TO     STEP
%token RULE        IF        THEN      ALSO
%token CCODE
%token REASONING   MAXDOT    MAXMIN
%token LBR         RB      
%token <name>      NAME
%token <val>       NUM
%token <name>      QUAL
%token <name>      DECL
%left  AND
%left  OR
%token EQUALS
%token HEDGE
%token FROM        IS
%left  PLUS        MINUS
%left  MULT        DIV
%left  EXP
%right UPLUS       UMINUS
%left	 LAND        LOR

%type  <ch>        cond
%type  <ch>        ans
%type  <ch>        iotype
%type  <ch>        rules
%type  <name>      vtype
%type  <val>       snum
%type  <nd>        expr

%%
start			 :  PROGRAM { fprintf(stdout,"\n\nWorking...\n"); init(fp_main,source); } program ;

program    :  NAME hedges functions variables rules reas
							{ globals(fp_main,$1); locals(fp_main,$5); calc_crisp(fp_main,fp_rule); }
           ;

hedges     :  hedges HEDGE NAME expr  { add_hedge($3,$4); }
           |  /* Nothing */
           ;

functions  :  functions FUNCTION NAME {initfdefs(&param_num);} fdef
							{ fd = getfdefs(param_num); add_fdef($3,fd); }
           |  /* Nothing */
					 ;

fdef			 :  fdef FROM expr TO expr IS expr { savefdefs($3,$5,$7); }
					 |  FROM expr TO expr IS expr { savefdefs($2,$4,$6); }
					 ;

variables  :  variables vheader members
           |  /* Nothing */
           ;

vheader    :  iotype NAME TYPE vtype RANGE snum TO snum STEP snum
							{ curr_var = create_var($1, $2, $4, $6, $8, $10); }
					 |  iotype NAME TYPE vtype RANGE snum TO snum
							{ curr_var = create_var($1, $2, $4, $6, $8, 1.); }
					 ;

iotype		 :  INPUT    { $$ = INP; }
					 |  OUTPUT   { $$ = OUTP; }
					 ;

vtype			 :  QUAL DECL   { $$ = strmrg($1,$2); }
					 |  QUAL | DECL
					 |  /* Nothing */  { $$ = "int"; }
					 ;

members    :  members FUZZY NAME MEMBERS { initpairs();} pairs
							{ tm = getpairs(); add_memb(curr_var,$3,tm); }
					 |  members FUZZY NAME FUNCTION NAME {initargs();} LBR args RBR
							{ tf = getargs(); add_fcall(curr_var,$3,$5,tf); }
           |  /* Nothing */
           ;

rules       : rules RULE NAME  { new_rule(fp_rule,$3); } IF cond THEN ans { $$ = max($1,$8); }
            |  /* Nothing */    { $$ = 0; }
            ;

cond       :  NAME IS NAME     { $$ = do_cond(fp_main,fp_rule,$1,"\0",$3,INP); }
					 |  NAME IS NAME NAME  { $$ = 1; do_cond(fp_main,fp_rule,$1,$3,$4,INP); }
					 |  LBR cond RBR    { $$ = $2; }
					 |  cond AND cond		{ $$ = do_and(fp_rule,$1,$3); }
					 |  cond OR cond    { $$ = do_or(fp_rule,$1,$3); }
           ;

ans         :  NAME EQUALS NAME     { $$ = do_cond(fp_main,fp_rule,$1,"\0",$3,OUTP); }
            |  NAME EQUALS NAME NAME  { $$ = do_cond(fp_main,fp_rule,$1,$3,$4,OUTP); }
           ;

args       :  args snum  { saveargs($2); }
					 |
           ;

pairs      :  pairs olb snum COMMA snum orb { savepairs($3,$5); }
					 |  olb snum COMMA snum orb { savepairs($2,$4); }
           ;

olb				 : LBR  |  /* Nothing */ ;

orb				 : RBR  |  /* Nothing */ ;

snum			 :  PLUS NUM   { $$ = $2; }
					 |  MINUS NUM  { $$ = -$2; }
					 |  NUM        { $$ = $1; }
					 ;

expr    :  LBR expr RBR    { $$ = $2; }
				|  expr PLUS expr  { $$ = add_op(PLUS,$1,$3); }
				|  expr MINUS expr { $$ = add_op(MINUS,$1,$3); }
				|  expr MULT expr  { $$ = add_op(MULT,$1,$3); }
				|  expr DIV expr   { $$ = add_op(DIV,$1,$3); }
				|  expr EXP expr   { $$ = add_op(EXP,$1,$3); }
				|  expr LOR expr   { $$ = add_op(LOR,$1,$3); }
				|  expr LAND expr  { $$ = add_op(LAND,$1,$3); }
				|  MINUS expr  %prec UMINUS  { $$ = add_op(UMINUS,$2,(NODE *)NULL); }
				|  PLUS expr   %prec UPLUS   { $$ = add_op(UPLUS,$2,(NODE *)NULL); }
				|  NUM      { $$ = add_num($1); }
				|  NAME			{ $$ = add_mark($1,&param_num); }
        ;

reas	 	: REASONING MAXDOT  { maxdot(fp_main); }
				| REASONING MAXMIN  { maxmin(fp_main); }
				| /* Nothing */   { maxdot(fp_main); }
				;

%%

int yylex()
{
#ifdef YYDEBUG
	fprintf(stdout,"(%s) ",yytext);
#endif
  return mylex();
}

void main(argc, argv)
int  argc;
char *argv[];
{
	FILE *fp_tab;

	fprintf(stdout,"\n----------------------------------");
	fprintf(stdout,"\nDCU Fuzzy Logic Compiler - Ver 1.2");
	fprintf(stdout,"\n (c) Dublin City University, 1992 ");
	fprintf(stdout,"\n----------------------------------\n");
	/* Open files */
	if (argc == 1)
		fprintf(stdout,"\nReading input from console...\n");
	if (argc >= 2) {
		yyin = safe_open(argv[1],"r");
		fprintf(stdout,"\nReading input from: %s",argv[1]);
		source = argv[1];
	}
	if (argc >= 3) {
		fp_main = safe_open(argv[2],"w");
		fprintf(stdout,"\nWriting output to: %s",argv[2]);
	}
	else
		fp_main = stdout;
	fp_rule = safe_open("temp.001","w+");

	yyparse();

  merge(fp_main,fp_rule);
	/* Close files */
	fclose(fp_main);
	fclose(fp_rule);

	if (argc >= 4)  {  /* Print symbol table */
		fprintf(stdout,"\nWriting symbol table to: %s\n",argv[3]);
		fp_tab = safe_open(argv[3],"w");
		printall(fp_tab,source);
	}
}


void yyerror (msg) char *msg;
/* Syntax error handler */
{
	fprintf(stderr,"\n[%d] %s: not expecting <%s> here!",yylineno,msg,yytext);
	fprintf(stderr,"\n\nTerminating compilation.\n");
	exit(1);
}

void myerror (msg)
/* Fatal error handler */
char *msg;
{
	fprintf(stderr,"\n[%d] Fatal error: %s",yylineno,msg);
	fprintf(stderr,"\n\nTerminating compilation.\n");
	exit(1);
}

void mywarn (msg)
/* Non-fatal error handler */
char *msg;
{
	fprintf(stderr,"\n[%d] Warning: %s",yylineno,msg);
}

void *safe_alloc(nelem,elsize)
size_t nelem, elsize;
{
	void *vd;
	vd = calloc(nelem,elsize);
	if (vd == NULL)
		myerror("not enough space to allocate more memory");
	return vd;
}

FILE *safe_open(filename,mode)
const char *filename, *mode;
{
	FILE *fp;
	fp = fopen(filename,mode);
	if (fp == NULL) {
		if (mode[0] == 'r')
			sprintf(errmsg,"cannot read from file <%s>",filename);
		else
			sprintf(errmsg,"cannot write to file <%s>",filename);
		myerror(errmsg);
	}
	return fp;
}


char *strmrg(s1,s2)
/* Merge two strings into one, returning a pointer to it */
/* Allocate new memory, free the old */
char *s1,*s2;
{
	char *s3;
	s3 = (char *) safe_alloc(strlen(s1)+strlen(s2),sizeof(char));
	sprintf(s3,"%s %s",s1,s2);
	free(s1); free(s2);
	return s3;
}


                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <ctype.h>
#include <float.h>
#include "defs.h"
#include "y.tab.h"

extern void *safe_alloc();
extern void myerror();
extern char errmsg[];

/********** ROUTINES TO STORE, EVALUATE AND PRINT EXPRESSIONS **********/

/********** EVALUATE **********/

PUBLIC float eval(eroot,args,argnum,x_arg)
/* Evaluate the expression rooted at <eroot> to a float */
NODE *eroot;    /* The root of the expression-tree */
float *args;    /* If expression is a function, these are the arguments */
int argnum;     /* The number of arguments in  <args> */
float x_arg;    /* The x-arg if expression is a function or hedge */
{
	float ft, fl, fr;
	char mk;
	ft = (float) 1;
	if (eroot->ntype == ENUM)
		ft = *(float *)(eroot->contents);
	else if (eroot->ntype == EMARK) {
		switch(mk = *(char *)(eroot->contents)) {
				case 'X':  ft = x_arg; break;        /* x-argument */
				case '<':  ft = -FLT_MAX; break;     /* minus infinity */
				case '>':  ft = FLT_MAX; break;      /* plus infinity */
				default:   if (mk-'A'< argnum) ft = args[mk-'A'];
		}
	}
	else if (eroot->ntype == EOP)  {
		switch(*(int *)(eroot->contents)) {
			case PLUS : 	ft = eval(eroot->left,args,argnum,x_arg) + eval(eroot->right,args,argnum,x_arg); break;
			case MINUS : 	ft = eval(eroot->left,args,argnum,x_arg) - eval(eroot->right,args,argnum,x_arg); break;
			case MULT : 	ft = eval(eroot->left,args,argnum,x_arg) * eval(eroot->right,args,argnum,x_arg); break;
			case DIV : 		ft = eval(eroot->left,args,argnum,x_arg) / eval(eroot->right,args,argnum,x_arg); break;
			case EXP : 		ft = (float) pow((double)eval(eroot->left,args,argnum,x_arg), (double)eval(eroot->right,args,argnum,x_arg)); break;
			case LAND :   fl=eval(eroot->left,args,argnum,x_arg); fr=eval(eroot->right,args,argnum,x_arg);
										ft = min(fl,fr); break;
			case LOR :    fl=eval(eroot->left,args,argnum,x_arg); fr=eval(eroot->right,args,argnum,x_arg);
										ft = max(fl,fr); break;
			case UPLUS :  ft = eval(eroot->left,args,argnum,x_arg);
			case UMINUS : ft = - eval(eroot->left,args,argnum,x_arg);
		}
	}
	return ft;
}


/********** STORE **********/

PRIVATE NODE *add_expr(el,ty,opand1,opand2)
/* Allocate memory and add node to the expression tree */
void *el;   /* An operator, number or marker */
ETYPE ty;   /* The type of <el> */
NODE *opand1, *opand2;   /* Operands, if <el> is an operator */
{
	NODE *nd;
	nd = (NODE *) safe_alloc(1,sizeof(NODE));
	nd->left = opand1;
	nd->right = opand2;
	nd->ntype = ty;
	nd->contents = (void *)el;
	return nd;
}


PUBLIC NODE *add_mark(ch,mnum)
/* Add a marker (representing a parameter) to the expression tree */
char *ch;         /* Parameter name */
int *mnum;        /* Max no. of parameters to date */
{
	char *vd;
	vd = (char *) safe_alloc(1,sizeof(char));
	*vd = toupper(ch[0]);
	if ((ch[0] != '>') && (ch[0] != '<') && ((ch[1] != '\0') || (ch[0] < 'A') || (ch[0] > 'X')))
	{
		sprintf(errmsg,"<%s> is not a valid name for a parameter",ch);
		myerror(errmsg);
	}
	if ((ch[0] >= 'A') && (ch[0] < 'X') && (ch[0]+1-'A' > *mnum))
		(*mnum) = ch[0] +1 -'A';
	return add_expr(vd,EMARK,(NODE *)NULL,(NODE *)NULL);
}

PUBLIC NODE *add_num(ft)
/* Add a number (float) to the expression tree */
float ft;
{
	float *vd;
	vd = (float *) safe_alloc(1,sizeof(float));
	*vd = ft;
	return add_expr(vd,ENUM,(NODE *)NULL,(NODE *)NULL);
}

PUBLIC NODE *add_op(op,arg1,arg2)
/* Add an operator and its operands to the expression tree */
int op;
NODE *arg1,*arg2;
{
	int *vd;
	vd = (int *) safe_alloc(1,sizeof(int));
	*vd = op;
	return add_expr(vd,EOP,arg1,arg2);
}



/********** PRINT **********/

PUBLIC void petree(tfile,end)
/* Print an expression whose tree is rooted at <end> */
/* Can't call <printree> because of unary operators */
NODE *end;
FILE *tfile;
{
	if (end == NULL) return;
	if (end->ntype == ENUM)
		fprintf(tfile,"%.1f", *(float *)(end->contents));
	else if (end->ntype == EMARK)
		fprintf(tfile,"%c",*(char *)(end->contents));
	else if (end->ntype == EOP)  {
		switch(*(int *)(end->contents)) {
			case PLUS : 	petree(tfile,end->left); fprintf(tfile,"+"); petree(tfile,end->right); break;
			case MINUS :  petree(tfile,end->left); fprintf(tfile,"-"); petree(tfile,end->right); break;
			case MULT :   petree(tfile,end->left); fprintf(tfile,"*"); petree(tfile,end->right); break;
			case DIV :    petree(tfile,end->left); fprintf(tfile,"/"); petree(tfile,end->right); break;
			case EXP :    petree(tfile,end->left); fprintf(tfile,"^"); petree(tfile,end->right); break;
			case LAND :   petree(tfile,end->left); fprintf(tfile,"&&"); petree(tfile,end->right);  break;
			case LOR :    petree(tfile,end->left); fprintf(tfile,"||"); petree(tfile,end->right); break;
			case UMINUS : fprintf(tfile,"-"); petree(tfile,end->left); break;
			case UPLUS :  fprintf(tfile,"+"); petree(tfile,end->left); break;
		}
	}
}



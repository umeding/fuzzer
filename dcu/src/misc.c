                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "defs.h"

extern NODE *symbtab;
extern NODE *addsymb(), *findsymb();
extern void myerror(), mywarn();
extern void *safe_alloc();
extern char *mkstr();
extern char errmsg[];

/* ************************************ */
/* Functions to process a FUNCTION CALL */
/* ************************************ */

static float args[MAXARG];    /* A list of all arguments processed for this call */
static int argind;            /* An index into <args> */

PUBLIC void add_fcall(cvar,sname,cname,ccall)
/* Add details of the call to the symbol table entry for the relevant variable */
NODE *cvar;    /* The current variable */
char *sname;   /* The name of the member function which contains the call */
char *cname;   /* The name of the called function */
FCALL *ccall;  /* The function call details */
{
	NODE *fn;
	FDEF *fd;
	ccall->fname = cname;
	fn = findsymb(symbtab,cname);
	if (fn == NULL)
		{ sprintf(errmsg,"function <%s> called but not declared",cname); myerror(errmsg); }
	fd = (FDEF *) fn->contents;
	if (fd->params > ccall->num)
		{ sprintf(errmsg,"should be %d (not %d) arguments in call to function <%s>",fd->params,ccall->num,cname); myerror(errmsg); }
	else if (fd->params < ccall->num)	{
		sprintf(errmsg,"ignoring any more than %d arguments in call to function <%s>",fd->params,cname);
		mywarn(errmsg);
		ccall->num = fd->params;
	}
	addsymb(&(((VAR *)cvar->contents)->members),sname,(void *)ccall,NCALL);
}


PUBLIC void initargs()
/* Call this before processing a function call */
{
	argind=0;
}

PUBLIC void saveargs(arg)
/* Add <arg> to the list of arguments which have been processed */
float arg;
{
	if (argind >= MAXARG) {
		sprintf(errmsg,"ignoring any more than %d arguments per function call",MAXARG);
		mywarn(errmsg);
	}
	else {
		args[argind] = arg;
		argind++;
	}
}

PUBLIC FCALL *getargs()
/* Allocate memory for and return all the details of the current function call */
{
	int i;
	FCALL *tcall;
	tcall = (FCALL *) safe_alloc(1,sizeof(FCALL));
	if (argind != 0)
		tcall->args = (float *) safe_alloc(argind,sizeof(float));
	for (i=0; i<argind; i++)
		tcall->args[i] = args[i];   /* Copy in the arguments */
	tcall->num = argind;          /* The total number of arguments */
	return tcall;
}


/* ****************************************** */
/* Functions to process a FUNCTION DEFINITION */
/* ****************************************** */

static NODE *f_from[MAXFDEF], *f_to[MAXFDEF], *f_is[MAXFDEF];
						/* lists of the expressions which define a function */
static int f_ind;   /* An index into these arrays */


PUBLIC void add_fdef(fname,fdef)
/* Add an entry to the symbol table for a function definition */
char *fname;
FDEF *fdef;
{
	addsymb(&symbtab,fname,(void *)fdef,NFUN);
}


PUBLIC void initfdefs(pnum)
/* Call this prior to processing a function definition */
int *pnum;
{
	f_ind=0;
	*pnum = 0;
}

PUBLIC void savefdefs(e_from,e_to,e_is)
/* Add this "line" of the function definition to the arrays */
NODE *e_from, *e_to, *e_is;
{
	if (f_ind >= MAXFDEF)
		{ sprintf(errmsg,"ignoring any more than %d definitions per function",MAXFDEF); mywarn(errmsg); }
	else {
		f_from[f_ind] = e_from;
		f_to[f_ind] = e_to;
		f_is[f_ind] = e_is;
		f_ind++;
	}
}

PUBLIC FDEF *getfdefs(pnum)
/* Allocate memory for and return the current function definition */
int pnum;
{
	FDEF *f_rec;
	int i;
	f_rec = (FDEF *) safe_alloc(1,sizeof(FDEF));
	f_rec->params = pnum;
	f_rec->from = (NODE **) safe_alloc(f_ind,sizeof(NODE *));
	f_rec->to = (NODE **) safe_alloc(f_ind,sizeof(NODE *));
	f_rec->is = (NODE **) safe_alloc(f_ind,sizeof(NODE *));
	for (i=0; i<f_ind; i++)
		{ (f_rec->from)[i] = f_from[i]; (f_rec->to)[i] = f_to[i]; (f_rec->is)[i] = f_is[i]; }
	f_rec->num = f_ind;   /* No. of "lines" in the definiton */
	return f_rec;
}



/* ******************************************* */
/* Functions to process a FUZZY SET DEFINITION */
/* ******************************************* */

static float p_x[MAXPAIR], p_y[MAXPAIR];  /* List of x and y co-ords */
static int p_ind;           /* Index into above arrays */

PUBLIC void add_memb(cvar,sname,mpts)
/* Add a fuzzy set definition to the symbol table entry for the relevant variable */
NODE *cvar;   /* The current variable */
char *sname;  /* Name of the fuzzy set */
MEMB *mpts;   /* Definition of the fuzzy set */
{
	int i;
	VAR *nvar;
	nvar = (VAR *)cvar->contents;
	addsymb(&(nvar->members),sname,(void *)mpts,NMEM);
	/* Check that points are within the declared range for the variable */
	for (i=0; i<mpts->num; i++) {
		if (mpts->x[i] < nvar->min) {
			mpts->x[i] = nvar->min;
			sprintf(errmsg,"changed the x-value <%f> to the min value <%f> for variable <%s>",mpts->x[i],nvar->min,cvar->name);
			mywarn(errmsg);
		}
		else if (mpts->x[i] > nvar->max) {
			mpts->x[i] = nvar->max;
			sprintf(errmsg,"changed the x-value <%f> to the max value <%f> for variable <%s>",mpts->x[i],nvar->max,cvar->name);
			myerror(errmsg);
		}
	}
}


PUBLIC void initpairs()
/* Call this prior to processing a fuzzy set definition */
{
	p_ind = 0;
}

PUBLIC void savepairs(x,y)
/* Save the current (x,y) co-ordinates */
float x, y;
{
	if (p_ind >= MAXPAIR) {
		sprintf(errmsg,"ignoring any more than %d points per fuzzy set",MAXPAIR);
		mywarn(errmsg);
	}
	else if ((p_ind > 0) && (x < p_x[p_ind-1]))	{
		sprintf(errmsg,"ignoring (%f, %f): x-values for points must be in ascending order",x,y);
		mywarn(errmsg);
	}
	else if ((y < 0) || (y > 1))	{
		sprintf(errmsg,"ignoring (%f, %f): y-values for points must be between 0 and 1",x,y);
		mywarn(errmsg);
	}
	else {
		p_x[p_ind] = x;
		p_y[p_ind] = y;
		p_ind++;
	}
}

PUBLIC MEMB *getpairs()
/* Allocate memory for and return the fuzzy set definition */
{
	MEMB *p_rec;
	int i;
	p_rec = (MEMB *) safe_alloc(1,sizeof(MEMB));
	p_rec->x = (float *) safe_alloc(p_ind,sizeof(float));
	p_rec->y = (float *) safe_alloc(p_ind,sizeof(float));
	for (i=0; i<p_ind; i++)
		{ (p_rec->x)[i] = p_x[i]; (p_rec->y)[i] = p_y[i]; }
	p_rec->num = p_ind;        /* No. of points in definition */
	return p_rec;
}




                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "defs.h"

extern NODE *symbtab;
extern NODE *findsymb();
extern char errmsg[];
extern void myerror();
extern void genmap();
extern int iround();


/********** ROUTINES TO GENERATE CODE FOR EVALUATING THE RULES **********/


static int tempnum;      /* The current temporary variable number */


PUBLIC void new_rule(rfile,rname)
/* Call this before processing a new rule */
FILE *rfile;
char *rname;
{
	tempnum = 0;
	fprintf(rfile,"\n\n\t/*****   CODE FOR RULE: %s   *****/",rname);
}




PRIVATE int geninp(rfile,vname,hname,mname,tvar,mapname)
/* Generate the code for evaluating an input variable */
/* ie. consulting the table for the fuzzy set */
FILE *rfile;
char *vname, *hname, *mname, *mapname;
VAR *tvar;
{
		tempnum++;
		fprintf(rfile,"\n\t/* %s is %s %s */",vname,hname,mname);
		fprintf(rfile,"\n\tindx = iround((float)(%s - (%s)%f) / (float)%f);",vname,tvar->type,tvar->min,tvar->step);
		fprintf(rfile,"\n\tt%03d = (float)%s[indx] / 255f;",tempnum,mapname);
		return tempnum;
}


PRIVATE int genoutp(rfile,vname,tvar,mapname)
/* Generate the code for assigning a value to the output variable */
/* ie. adding the area and moment to the running totals */
FILE *rfile;
char *vname, *mapname;
VAR *tvar;
{
	int msize;
	msize = iround((tvar->max-tvar->min)/tvar->step) +1;
	fprintf(rfile,"\n\t/* Fire strength (alpha) is t%03d */",tempnum);
	fprintf(rfile,"\n\tif (t%03d != 0f)  {",tempnum);
	fprintf(rfile,"\n\t\t/* Union with output array for <%s> */",vname);
	fprintf(rfile,"\n\t\tfor (i=0; i<%d; i++) {",msize);
	fprintf(rfile,"\n\t\t\tmap_i = (float)%s[i] / 255f;",mapname);
	fprintf(rfile,"\n\t\t\tO_%s[i] = max(O_%s[i], MEW(t%03d, map_i));",vname,vname,tempnum);
	fprintf(rfile,"\n\t\t}");
	fprintf(rfile,"\n\t}");
	return tempnum;
}



PUBLIC int do_and(rfile,t1,t2)
/* Generate the code for working out the conjunction of two conditions */
FILE *rfile;
int t1, t2;       /* Nos. of the temp. variables where the results of */
{						      /* the conditions to be AND-ed are stored */
	tempnum++;
	fprintf(rfile,"\n\tt%03d = min(t%03d, t%03d);    /* AND */",tempnum,t1,t2);
	return tempnum;
}

PUBLIC int do_or(rfile,t1,t2)
/* Generate the code for working out the disjunction of two conditions */
FILE *rfile;
int t1,t2;        /* As for <do_and> */
{
	tempnum++;
	fprintf(rfile,"\n\tt%03d = max(t%03d, t%03d);    /* OR */",tempnum,t1,t2);
	return tempnum;
}






PUBLIC int do_cond(mfile,rfile,vname,hname,mname,iotype)
/* Type check a rule-condition, and call relevant generation routines */
/* (This function generates no code itself) */
FILE *mfile, *rfile;
char *vname, *hname, *mname;    /* Names of variable, hedge and member fn. */
IOTYPE iotype;
{
	NODE *vnode, *mnode, *hnode=(NODE *)NULL;
	VAR *tvar;
	char mapname[28];  /* The name of the relevant map (array) */

	/*** (1) Work out name of map ***/
	if (hname[0] == '\0') 	sprintf(mapname,"%.8s_%.8s",vname,mname);
	else sprintf(mapname,"%.8s_%.8s_%.8s",vname,hname,mname);

	/*** (2) Check that variable name is ok ***/
	vnode = findsymb(symbtab,vname);
	if ((vnode == NULL) || (vnode->ntype != NVAR))
		{sprintf(errmsg,"variable <%s> used but not declared",vname); myerror(errmsg); return 0; }
	tvar = (VAR *)vnode->contents;
	if (tvar->io != iotype)
		{ sprintf(errmsg,"I/O usage of variable <%s> inconsistent with declaration",vname); myerror(errmsg); }

	/*** (3) Check that member set name is ok ***/
	mnode = findsymb(tvar->members,mname);
	if ((mnode == NULL) || ((mnode->ntype != NMEM) && (mnode->ntype != NCALL)))
		{sprintf(errmsg,"<%s> is not a member function for <%s>",mname,vname); myerror(errmsg); return 0; }

	/*** (4) Check that hedge function name is ok ***/
	if (hname[0] != '\0')  {
		hnode = findsymb(symbtab,hname);
		if ((hnode == NULL) || (hnode->ntype != NHED))
			{sprintf(errmsg,"hedge function <%s> used but not declared",hname,vname); myerror(errmsg); return 0; }
	}

	/*** (5) Call relevant generation routines ***/
	genmap(mfile,mapname,vnode,hnode,mnode);   /* Generate array */
	if (iotype == INP)  		      return geninp(rfile,vname,hname,mname,tvar,mapname);
	else /* (iotype == OUTP) */   return genoutp(rfile,vname,tvar,mapname);
}

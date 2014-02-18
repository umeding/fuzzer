                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include "defs.h"

extern void printree();
extern NODE *findsymb(), addsymb();
extern float eval();
extern NODE *symbtab;
extern void myerror();
extern char errmsg[];
extern void *safe_alloc();

/* ROUTINES DEALING WITH THE GENERATION AND PROCESSING OF MAP FUNCTIONS */

static NODE *m_done=NULL;  /* (The root node of) a binary tree containing */
												 /* the names of maps that were already generated */


PUBLIC int iround(db)
/* Round a value to the nearest integer */
float db;
{
	if (db - floor(db) >= 0.5)	return (int) db + 1;
	else 												return (int) db;
}


PUBLIC void mapmem(cvar,cmem,res)
/* Convert a fuzzy-set definition into a map */
VAR *cvar;   /* The current varaible */
MEMB *cmem;  /* The fuzzy-set definition */
int *res;    /* The resulting map */
{
	int pt, j;
	float this_x, all_x, all_y, offset, i;
	if (cmem->num!=0)	{
		for (pt=0; pt<cmem->num-1; pt++) {
		/* For each pair of (x,y) points in the fuzzy-set definition */
			all_x = (cmem->x[pt+1] - cmem->x[pt]);
			all_y = (cmem->y[pt+1] - cmem->y[pt]);
			for (i=cmem->x[pt]; i<=cmem->x[pt+1]; i+=cvar->step) {
			/* For each point between the current pair of x-values */
				j = iround((i - cvar->min) / cvar->step);  /* Array index */
				this_x = (i - cmem->x[pt]);
				offset = (all_y * this_x/all_x);
				res[j] = iround((float)255 * (cmem->y[pt] + offset));
			}
		}
	}
}



PRIVATE void mapfun(cvar,fcall,res)
/* Convert a function definition into a map */
VAR *cvar;
FCALL *fcall;
int *res;
{
	int pt, j;
	float c_from, c_to, i;
	NODE *fnd;
	FDEF *fdef;
	fnd = findsymb(symbtab,fcall->fname);
	if (fnd == NULL)
		{sprintf(errmsg,"call made to an undeclared function <%s>",fcall->fname); myerror(errmsg); }
	else {
		fdef = (FDEF *)fnd->contents;
		for (pt=0; pt<fdef->num; pt++)  {
		/* For each "line" of the function definition */
			c_from = eval(fdef->from[pt],fcall->args,fcall->num,(float)1);
			c_from = max(c_from, cvar->min);
			c_to = eval(fdef->to[pt],fcall->args,fcall->num,(float)1);
			c_to = min(c_to, cvar->max);
			for (i=c_from; i<=c_to; i+=cvar->step) {
			/* For each point in the range of the current "line" */
				j = iround((i - cvar->min) / cvar->step);  /* Array index */
				res[j] = iround((float)255 * eval(fdef->is[pt],fcall->args,fcall->num,i));
			}
		}
	}
}




PRIVATE void print_a_map(mfile,map,mname,msize)
/* Print a declaration for a map */
FILE *mfile;
int *map;
char *mname;  /* Name of the map */
int msize;    /* Size of the map */
{
	int i;
	fprintf(mfile,"\n\nstatic short int %s[] = {\n    ",mname);
	for (i=0; i<msize-1; i++) {
		fprintf(mfile,"%3d, ",map[i]);
		if ((i != 0) && (((i+1)/10)*10 == i+1)) fprintf(mfile,"\n    ");
	}
	fprintf(mfile,"%3d\n};",map[i]);
}





PUBLIC void genmap(mfile,mapname,vnode,hnode,mnode)
/* Generate a map for a particular condition */
FILE *mfile;
char *mapname;
NODE *vnode, *hnode, *mnode;  /* Variable hedge and member nodes */
{
	VAR *tvar;
	int i, *map, msize;

	/*** (1) Check if we've generated one of these already ***/
	if (findsymb(m_done,mapname) != NULL)  /* We have! */
		return;

	/*** (2) Call <mapmem> or <mapfun> to generate map ***/
	tvar = (VAR *)vnode->contents;
	msize = iround((tvar->max-tvar->min)/tvar->step) +1;
	map = (int *) safe_alloc(msize,sizeof(int));
	if (mnode->ntype == NMEM)
		mapmem(tvar,(MEMB *)mnode->contents,map);
	else if (mnode->ntype == NCALL)
		mapfun(tvar,(FCALL *)mnode->contents,map);

	/*** (3) Apply hedge function (if there is one) ***/
	if (hnode != NULL)  {
		for (i=0; i<msize; i++)
			map[i] = (float)255 * eval((NODE *)hnode->contents,(float *)NULL,0,(float)map[i]/(float)255);
	}

	/*** (4) Print the map (and note that we have done so) ***/
	print_a_map(mfile,map,mapname,msize);
	addsymb(&m_done,mapname,(void *)NULL,(NTYPE)0);
}

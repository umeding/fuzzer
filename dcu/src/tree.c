                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "defs.h"

extern void *safe_alloc();
extern void mywarn();
extern char errmsg[];


PRIVATE NODE *create(sname,scont,stype)
/* Allocate memory for a new binary tree node & fill in some fields */
char *sname;   /* Name of the node */
void *scont;   /* Contents of the node */
NTYPE stype;
{
  NODE *new;
	new = (NODE *) safe_alloc(1,sizeof(NODE));
	strcpy(new->name,sname);
	new->left = (new->right = NULL);
	new->contents = scont;
	new->ntype = stype;
  return new;
}

PUBLIC void initialise(sroot)
/* Free all the memory used by the tree rooted at <sroot> */
NODE *sroot;
{
  if (sroot != NULL)  {
    initialise(sroot->left);
		initialise(sroot->right);
		free(sroot->name);
    free(sroot);
    sroot = NULL;
  }
}

PUBLIC NODE *findsymb(sroot,sname)
/* Find and return the node in the tree rooted at <sroot> which has name <sname> */
NODE *sroot;
char *sname;
{
  int i;
	NODE *temp;

	temp = sroot;
  while (temp != NULL) {
    i = strcmp(sname,temp->name);
    if (i == 0)       return temp;
		else if (i > 0)   temp = temp->right;
		else if (i < 0)   temp = temp->left;
  }
	return temp;
}



PUBLIC NODE *addsymb(sroot,sname,scont,stype)
/* Add a node to the symbol table */
/* Print an error message if it's already there */
NODE **sroot;
char *sname;
void *scont;
NTYPE stype;
{
	int i=1;
  NODE *temp, *prev;

	if (*sroot == NULL)  /* Tree is empty */
		temp = *sroot = create(sname,scont,stype);
  else {
		temp = *sroot;
		while (temp != NULL) {
			prev = temp;
			i = strcmp(sname,temp->name);
			if (i == 0)  /* Already there */ {
				sprintf(errmsg,"ignoring redeclaration of the name <%s>",sname);
				mywarn(errmsg);
				return temp;
			}
			else if (i > 0)   temp = temp->right;
			else if (i < 0)   temp = temp->left;
		}
		i = strcmp(sname,prev->name);
		if (i > 0)       temp = prev->right = create(sname,scont,stype);
		else if (i < 0)  temp = prev->left = create(sname,scont,stype);
  }
  return temp;
}

PUBLIC void printree(tfile,sroot,pfun)
/* Print out a tree rooted at <sroot> to file <tfile> */
/* Use the function <pfun> to print each individual node */
FILE *tfile;
NODE *sroot;
void (*pfun)();
{
	if (sroot == NULL) return;
	printree(tfile,sroot->left,pfun);
	(*pfun)(tfile,sroot);
	printree(tfile,sroot->right,pfun);
}


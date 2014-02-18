                 /* ******************************* */
                 /*     DCU Fuzzy Compiler          */
                 /*     Dublin City University      */
                 /* ******************************* */


/********** Standard definitions **********/

#define Boolean int
#define FALSE   0
#define TRUE    !(FALSE)

#define PUBLIC              /* Accessible as an "extern" to other files */
#define PRIVATE static

#define min(x,y) (x<y?x:y)
#define max(x,y) (x>y?x:y)


/********** Maximum values for system variables **********/

#define NAMELEN 30                       /* Max length of a variable name */
#define MAXARG  ('X' - 'A')             /* Max. no. of args to a function */
#define MAXPAIR 40               /* Max. no. of pairs in a fuzzy-set defn */
#define MAPSIZE 300               /* Max. size of a fuzzy-set map (array) */
#define MAXFDEF 10                /* Max. no. of lines in a function defn */



/********** Binary-Tree realted definitions **********/

#define IOTYPE  int                 /* Variable types (input or output) */
#define INP     1
#define OUTP    2

#define NTYPE   int              /* The "type" of the symbol-table entry */
#define NVAR    1                                         /* a variable  */
#define NHED    2                                         /* a hedge fcn */
#define NFUN    3                                       /* Function defn */
#define NMEM    4                       /* a fuzzy-set defined by points */
#define NCALL   5              /* a fuzzy-set defined by a function call */



/* A Binary Tree Node */
typedef struct TREE {
		struct TREE *left, *right;                     /* Children sub-trees */
		char name[NAMELEN];                       /* Symbol name (the "key") */
		NTYPE ntype;               /* Hedge, function, variable or fuzzy set */
		void *contents;           /* Points to the definition for the symbol */
} NODE;


/*** The <contents> field will point to one of the following: ***/

/** (1) A variable definition **/
typedef struct  {
		char *type;					    		/* Eg. "short int", "long", "float" etc. */
		float min, max, step;        /* Universe of discourse and resolution */
		IOTYPE io;                              /* Input or output variable? */
		NODE *members;      /* A pointer to a tree containing its fuzzy-sets */
} VAR;


/** (2) A function definition **/
typedef struct {
		int params;               /* No. of parameters used in the function */
		NODE **from, **to, **is;         /* Three lists of expression trees */
								 /* (from[i], to[i], is[i]) is a "line" of the fcn defn */
		int num;                   /* The number of "lines" in the fcn defn */
} FDEF;


/** (3) A fuzzy-set definition, which can be: **/

/** (3a) A list of (x,y) points **/
typedef struct {
		float *x, *y;                      /* Lists of the x- and y- points */
		int num;                   /* The number of (x,y) pairs in the defn */
} MEMB;

/** (3b) A function call **/
typedef struct {
		char *fname;                    /* The name of the called function */
		float *args;                            /* A list of its arguments */
		int num;                                /* The number of arguments */
} FCALL;


/** (4) A hedge function, ie. an expression **/


/********** Definitions relating to (binary) expression trees **********/


#define  ETYPE int               /* Type of a node in an expression tree */
#define  ENUM 1                                    /* A (literal) number */
#define  EMARK 2                       /* A parameter name or "<" or ">" */
#define  EOP 3                                            /* An operator */


typedef struct {
		float elem;              /* Either the value of a number, or a token */
																	/* representing the marker or operator */
		ETYPE etype;                       /* ie. number, marker or operator */
} ESYMB;


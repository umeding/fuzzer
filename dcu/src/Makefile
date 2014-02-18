
YFLAGS=-d -v

OBJS=\
yacc.o \
lex.o \
decl.o \
expr.o \
map.o \
misc.o \
rules.o \
table.o \
tree.o 

fc: $(OBJS)
	gcc -o fc $(OBJS) -lfl -lm

clean:; rm -f $(OBJS) lex.c y.tab.* temp.*

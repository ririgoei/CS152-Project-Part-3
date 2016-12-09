grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE     : 'while' ;
FUNCTION  : 'function' ;
VAR       : 'var' ;
PRINT     : 'print' ;

// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL      : 'true' | 'false' ;
NULL      : 'null' ;

// Symbols
MUL       : '*' ;
DIV       : '/' ;
SEPARATOR : ';' ;
ADD       : '+' ;
SUB       : '-' ;
MOD       : '%' ;
GT        : '>' ;
GTE       : '>=' ;
LT        : '<' ;
LTE       : '<=' ;
EQ        : '==' ;

// Identifiers
ID		: [a-zA-Z_][a-zA-Z0-9_]* ;

// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
WS            : [ \t]+ -> skip ; // ignore whitespace


// ***Parsing rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block                          # while
    | PRINT '(' expr ')' SEPARATOR                      # print
    | SEPARATOR                                         # empty
    ;

expr: expr op=( '*' | '/' | '%' ) expr                  # MulDivMod
    | expr op=( '+' | '-' ) expr                        # AddSub
    | expr op=( '<' | '<=' | '>' | '>=' | '==') expr    # compare
    | FUNCTION parameter block                          # funcDec
    | VAR ID op='=' expr                                # varDec
    | expr argument                                     # funcApp
    | ID                                                # varRef
    | ID op='=' expr                                    # assign
    | INT                                               # int
    | BOOL                                              # bool
    | NULL                                              # null
    | '(' expr ')'                                      # parens
    ;

parameter: '(' (ID ',')* ID  ')'                        # withParam
   | '('  ')'                                           # emptyParam
   ;

argument: '(' (expr ',')* expr ')'                      # withArg
   | '('  ')'                                           # emptyArg
   ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;

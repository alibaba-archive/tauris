grammar TExpr;

@header {
    package antlr4.tauris;
}

parse
 : expression EOF
 ;

expression
 : LPAREN expression RPAREN                       #parenExpression
 | NOT expression                                 #notExpression
 | expression IN container                        #inExpression
 | expression NOT IN container                    #notInExpression
 | expression MATCH regex                         #matchExpression
 | expression IS type                             #isTypeExpression
 | expression IS NOT type                         #isNotTypeExpression
 | expression comparator expression               #comparatorExpression
 | expression binary expression                   #binaryExpression
 | variable                                       #variableExpression
 | calc                                           #calcExpression
 ;

variable
 : VARIABLE
 | literal
 | array
 ;

comparator
 : GT | GE | LT | LE | EQ | NE
 ;

binary
 : AND | OR
 ;

bool
 : TRUE | FALSE
 ;

literal
   : String
   | Integer
   | Float
   | Boolean
   ;

container
    : array
    | variable
    | String
    ;

array
   : '[' integers? ']'
   | '[' strings? ']'
   | '[' floats? ']'
   | '[' booleans? ']'
   ;

calc
   : bit
   ;

bit
   : bit BAND shift
   | bit BEOR shift
   | bit BIOR shift
   | shift
   ;

shift
   : shift LSHIFT plus
   | shift RSHIFT plus
   | shift RSHIFT3 plus
   | plus
   ;

plus
   : plus PLUS multiplying
   | plus MINUS multiplying
   | multiplying
   ;

multiplying
   : multiplying MUL atom
   | multiplying DIV atom
   | multiplying MOD atom
   | atom
   ;

atom
   : variable
   | scientific
   | LPAREN bit RPAREN
   | function
   ;

scientific
   : number E number
   | number
   ;

function
   : funcname LPAREN parameters RPAREN
   ;

funcname
   : IDENTIFIER
   ;

parameters
   : expression (',' expression)*
   ;

number
   : MINUS? DIGIT + (POINT DIGIT +)?
   ;

regex
   : Regex
   ;

type
   : IDENTIFIER
   ;

strings
    : String (',' String)* ','?
    ;

integers
    : Integer (',' Integer)* ','?
    ;

floats
    : Float (',' Float)* ','?
    ;

booleans
    : Boolean (',' Boolean)* ','?
    ;

Integer
   : '-' ? INT
   ;

Float
   : '-' ? INT '.' INT
   ;

Boolean
   : TRUE
   | FALSE
   ;

String
   : STRING
   ;

Regex
   : REGEX
   ;

fragment REGEX
   : '/' ( STRING_ESCAPE_SEQ | ~[\\\r\n'] )* '/'
   ;

fragment STRING
   : '\'' ( STRING_ESCAPE_SEQ | ~[\\\r\n'] )* '\''
   | '"' ( STRING_ESCAPE_SEQ | ~[\\\r\n"] )* '"'
   ;

fragment STRING_ESCAPE_SEQ
   : '\\' .
   ;

fragment ESC
   : '\\' (["\\/bfnrt])
   ;

fragment NAME
   : [a-zA-Z][a-zA-Z0-9_\\.]+
   ;


AND        : '&&' ;
OR         : '||' ;
NOT        : 'not';
IS         : 'is';
IN         : 'in';
TRUE       : 'true' ;
FALSE      : 'false' ;
GT         : '>' ;
GE         : '>=' ;
LT         : '<' ;
LE         : '<=' ;
EQ         : '==' ;
NE         : '!=' ;
MATCH      : '=~';
LPAREN     : '(' ;
RPAREN     : ')' ;
INT        : '-'? [0-9]+ ;
FLOAT      : '-'? [0-9]+ ( '.' [0-9]+ )? ;
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]* ;
VARIABLE
    : '$'[a-zA-Z_] [.a-zA-Z_0-9]*
    | '@'[a-zA-Z_] [.a-zA-Z_0-9]*
    ;

PLUS : '+';
MINUS: '-';
MUL  : '*';
DIV  : '/';
MOD  : '%';
POINT: '.';
E    : 'e' | 'E';
LSHIFT : '<<' ;
RSHIFT : '>>' ;
RSHIFT3 : '>>>' ;
BAND  : '&'; //位与
BEOR  : '^'; //异或
BIOR  : '|'; //位或
NL   : '\n';
DIGIT: ('0' .. '9');

WS         : [ \r\t\u000C\n]+ -> skip;

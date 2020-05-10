grammar Tauris ;

@header {
    package antlr4.tauris;
}

pipeline
    : inputGroup+ filterGroup* outputGroup+
    ;

inputGroup
    : 'input' plugins
    ;

filterGroup
    : 'filter' plugins
    ;

outputGroup
    : 'output' plugins
    ;

pluginGroup
    : ID plugins
    ;

plugins
    : '{' (assignment)* (plugin)+ '}'
    ;

plugin
    : name assignments
    ;


assignments
    : '{' (assignment)* '}'
    ;

assignment
    : name '=>' value ';'
    | name '=>' environValue ';'
    | name '=>' assignments
    | name '=>' plugins
    | name '=>' plugin
    | name '=>' hash
    ;

name
    : ID
    | 'input'
    | 'filter'
    | 'output'
    ;

value
   : simpleValue
   | environValue
   | array
   ;

simpleValue
   : String
   | Integer
   | Float
   | Boolean
   | Null
   ;

environValue
   : Environ
   ;

array
   : '[' integers? ']'
   | '[' strings? ']'
   | '[' floats? ']'
   | '[' booleans? ']'
   | '[' nulls? ']'
   | '[' ']'
   ;

hash
   : '{' keyValues '}'
   ;

keyValues
   : keyValue (',' keyValue)* ','?
   ;

keyValue
   : key ':' simpleValue
   | key ':' environValue
   ;

key
   : String
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

nulls
    : Null (',' Null)* ','?
    ;

Integer
   : '-' ? INT
   ;

Float
   : '-' ? INT '.' INT
   ;

Boolean
   : 'true'
   | 'false'
   ;

Null
   : 'null'
   ;

String
   : STRING
   ;

Environ
   : '`' ( NAME ) '`'
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

fragment INT
   : '0' | [1-9] [0-9]*
   ;

fragment NAME
   : [a-zA-Z][a-zA-Z0-9_\\.]+
   ;

// no leading zeros
fragment EXP
   : [Ee] [+\-]? INT
   ;

ID : [a-zA-Z][a-zA-Z0-9_]+ ;

WS: [ \t\n\r]+ -> skip ;
LINE_COMMENT: '#' ~[\r\n]* -> skip;
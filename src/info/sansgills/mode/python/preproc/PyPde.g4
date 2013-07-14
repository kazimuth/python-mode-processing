//grammar to parse pseudo-python
//note: ORDER MATTERS

grammar pyPde;

placeholder:   expr* EOF;

expr: literal NEWLINE;


//  LITERALS
literal:    STRING | INTEGER | FLOAT | IMAGINARY;





//TOKENS

//  STRING LITERAL
STRING:     STRINGPRE? (SHORTSTRING | LONGSTRING);
SHORTSTRING:'"' (~('\n'|'\\'|'"') | ESCAPE)* '"'  
        |   '\'' (~('\n'|'\\'|'\'') | ESCAPE)* '\'';
LONGSTRING: '"""' (~'\\' | ESCAPE)* '"""'
        |   '\'\'\'' (~'\\' | ESCAPE)* '\'\'\'';
fragment ESCAPE:        '\\'.;
fragment STRINGPRE:     'r' | 'u' | 'ur' | 'R' | 'U' | 'UR' | 'Ur' | 'uR' | 'b' | 'B' | 'br' | 'Br' | 'bR' | 'BR';

//  FLOATING POINT LITERAL
FLOAT:      [0-9]* '.' [0-9]* ([eE][+-]? [0-9]+)?;

//  IMAGINARY LITERALS
IMAGINARY:  (FLOAT | INTEGER) [jJ];

//  INTEGER LITERAL
//  not distinguishing between types, we don't really need to; python will catch mismatches
INTEGER:    ('0'[xXbBoO])? [0-9A-Fa-f]+ [lL]? ;

//OPERATORS
OPERATOR:   '+'|'-'|'*'|'**'|'/'|'//'|'%'|'<<'|'>>'|'&'|'|'|'^'|'~'|'<'|'>'|'<='|'>='|'=='|'!='|'<>';

//ASSIGNMENT
ASSIGN:     '=';

//IDENTIFIERS
IDENTIFIER: [a-zA-z_] [a-zA-Z0-9_]*;

//INDENT CHARACTER
//note: we're going to swing through the input code and turn all indentation into tabs before we hand it to the lexer
INDENT:     '\t';

//COMMENT CHARACTER
COMMENT:    '#';

//extra whitespace
WHITESPACE: ' '+ -> skip;   //throw it away

NEWLINE:    '\r'?'\n';

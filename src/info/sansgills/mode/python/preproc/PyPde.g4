//grammar to parse pseudo-python
//note: ORDER MATTERS

grammar PyPde;

placeholder:   (expression NEWLINE)* EOF;

stmt: simple_stmt | compound_stmt;
simple_stmt:;
compound_stmt:;



expression: test;

test:primary; //the toppest-level; TODO

//primaries: the most tightly bound operations
primary:    atom;


//atoms: basic values
atom: (literal | identifier | enclosure) trailer*;

//trailers: most tightly bound operations
trailer: '(' arg_list ')'       //function call on atom
       | '[' subscript_list ']' //subscript on atom
       | '.' identifier;        //field access on atom

arg_list:;//function arguments
subscript_list:;//subscript (e.g. list[0] = etc)

//literals.
literal:    STRING | INTEGER | FLOAT | IMAGINARY;
//variables / named things
identifier: IDENTIFIER;
//enclosures; things inside some sort of bracket
enclosure:  parenth_form | list_display | generator_expression | dict_display | set_display | string_conversion | yield_atom;

//enclosure rules
parenth_form:   '(' expression_list? ')'; //construct tuple
list_display:   ;   //creates list object
dict_display:   ;   //creates dict object
set_display:    ;   //creates set object
string_conversion:  '`' expression_list '`';    //convert tuple to string
generator_expression:   ;
yield_atom: '(' 'yield' expression_list? ')';   //generator


comprehension: expression;
comp_iter: comp_for | comp_if;
comp_for:;
comp_if:;

expression_list: expression (',' expression)* ','?; //list of expressions


//TOKENS; only tokenizing things that have complex rules; everything else used implicitly in parser rules (?)

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

//MODIFICATION+ASSIGNMENT
MODIFY_ASSIGN: '+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' | '<<=' | '>>=' | '**=' | '//=';

//ASSIGNMENT
ASSIGN:     '=';

//IDENTIFIERS
IDENTIFIER: [a-zA-z_] [a-zA-Z0-9_]*;

//INDENT CHARACTER
//note: we're going to swing through the input code and turn all indentation into tabs before we hand it to the lexer
//INDENT:     '\t';

//COMMENT CHARACTER
COMMENT:    '#';



//extra whitespace
WHITESPACE: ' '+ -> skip;   //throw it away

NEWLINE:    '\r'?'\n';

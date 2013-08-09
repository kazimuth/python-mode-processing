/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar PyPde2;

//some black magic
@lexer::members {
                 int bracket_nesting_level = 0;
                 int parenth_nesting_level = 0;
                 int curlybr_nesting_level = 0;
                 
                 int previous_indent_level = 0;
                 int current_indent_level = 0;
                 }


script: (NEWLINE | statement)*;


//lexer!
//strings first, so they'll gobble things up first

STRING: STRINGPREFIX? (SHORTSTRING | LONGSTRING);
fragment SHORTSTRING: '\'' (~('\\'|'\n'|'\'')|'\\'.)* '\'' 
                    | '"' (~('\\'|'\n'|'"')|'\\'.)* '"';
fragment LONGSTRING: '"""' (~'\\'|'\\'.)* '"""'
                   | '\'\'\'' (~'\\'|'\\'.)* '\'\'\'';
fragment STRINGPREFIX: 'r' | 'u' | 'ur' | 'R' | 'U' | 'UR' | 'Ur' | 'uR'
                     | 'b' | 'B' | 'br' | 'Br' | 'bR' | 'BR';

//numbers!
IMAGINARY: (FLOAT | [0-9]+) [jJ];

FLOAT: POINTFLOAT | EXPFLOAT;
fragment POINTFLOAT: [0-9]* ('.' [0-9]+) | [0-9]+ '.';
fragment EXPFLOAT: (POINTFLOAT | [0-9]+) [eE] [+-] [0-9]+;

INTEGER: (DEC | OCT | HEX | BIN) [lL]?; //not distinguishing between ints and longs, no need
fragment DEC: [1-9][0-9]* | '0';
fragment OCT: '0' [oO] [0-7]+ | '0' [0-7]+;
fragment HEX: '0' [xX] [0-9A-Fa-f]+;
fragment BIN: '0' [bB] [01]+;



//keywords!
AND : 'and';
DEL : 'del';
FROM : 'from';
NOT : 'not';
WHILE : 'while';
AS : 'as';
ELIF : 'elif';
GLOBAL : 'global';
OR : 'or';
WITH : 'with';
ASSERT : 'assert';
ELSE : 'else';
IF : 'if';
PASS : 'pass';
YIELD : 'yield';
BREAK : 'break';
EXCEPT : 'except';
IMPORT : 'import';
PRINT : 'print';
CLASS : 'class';
EXEC : 'exec';
IN : 'in';
RAISE : 'raise';
CONTINUE : 'continue';
FINALLY : 'finally';
IS : 'is';
RETURN : 'return';
DEF : 'def';
FOR : 'for';
LAMBDA : 'lambda';
TRY : 'try';



IDENTIFIER: [a-zA-Z_] [a-zA-Z0-9_]*;

//logical vs. physical line nonsense
LPAREN: '(' {parenth_nesting_level++;};
RPAREN: ')' {parenth_nesting_level--;};
LCURLY: '{' {curlybr_nesting_level++;};
RCURLY: '}' {curlybr_nesting_level--;};
LBRACKET: '[' {bracket_nesting_level++;};
RBRACKET: '[' {bracket_nesting_level--;};

//we're inside a grouping, newlines don't count
IMPLICIT_ESCAPE_NEWLINE: {parenth_nesting_level > 0 ||
                          curlybr_nesting_level > 0 ||
                          bracket_nesting_level > 0}? ('\n' | '\r\n') -> skip;
EXPLICIT_ESCAPE_NEWLINE: ('\\\n' | '\\\r\n') -> skip; //explicit line joins


NEWLINE: ('\n' | '\r\n') {current_indent_level = 0;}; //above not true; these newlines count

COMMENT: '#' (~'\n')* -> skip; //no need to keep these


WS: ' '+ -> skip;

// onto the parser
// atomic values: identifiers and literals (including enclodure literals
atom      :  identifier | literal | enclosure;

literal:    STRING | INTEGER | IMAGINARY | FLOAT;
identifier: IDENTIFIER;
enclosure :  parenth_form | generator_expression 
          | list_display | dict_display | set_display
          | string_conversion | yield_atom;

parenth_form: '(' expression_list ')'; //tuples

generator_expression: '(' expression comp_for ')';

list_display        :  '[' (expression_list | list_comprehension)? ']'; //lists; lotsa backwards compatibility cruft tho
list_comprehension  :  expression list_for;
list_for            :  FOR target_list IN old_expression_list (list_iter)?;
old_expression_list :  old_expression ((',' old_expression)+ ','?)?;
old_expression      :  or_test | old_lambda_form;
list_iter           :  list_for | list_if;
list_if             :  IF old_expression list_iter?;

dict_display       :  '{' (key_datum_list | dict_comprehension)? '}'; //dictionary
key_datum_list     :  key_datum (',' key_datum)* ','?;
key_datum          :  expression ':' expression;
dict_comprehension :  expression ':' expression comp_for;

set_display :  '{' (expression_list | comprehension) '}'; //set

comprehension :  expression comp_for; //elements of set and dictionary comprehensions
comp_for      :  FOR target_list IN or_test comp_iter?;
comp_iter     :  comp_for | comp_if;
comp_if       :  IF expression comp_iter?;

string_conversion :  '`' expression_list '`';

yield_atom       :  '('  yield_expression ')';
yield_expression :  YIELD expression_list?;


// primaries: most tightly bound operations

primary : primary '.' identifier # AttributeRef
        | primary '[' expression_list ']' # Subscription
        | primary '[' short_slice ']' # ShortSlicing
        | primary '[' slice_list ']'  # Slicing
        | primary '(' (argument_list ','? | expression comp_for)? ')' # Call
        | atom # PrimaryAtom
        ;

//attributeref :  primary '.' identifier;

//subscription :  primary '[' expression_list ']'; //BLAH

//slicing          :  simple_slicing | extended_slicing;
//simple_slicing   :  primary '[' short_slice ']';
//extended_slicing :  primary '[' slice_list ']';
slice_list       :  slice_item (',' slice_item)* ','?;
slice_item       :  expression | proper_slice | '...';
proper_slice     :  short_slice | long_slice;
short_slice      :  (expression)? ':' (expression)?;
long_slice       :  short_slice ':' (expression)?;

//call                 :  primary '(' (argument_list ','? | expression comp_for)? ')';


argument_list        :  positional_arguments (',' keyword_arguments)?
                            (',' '*' expression)? (',' keyword_arguments)?
                            (',' '**' expression)?
                          | keyword_arguments (',' '*' expression)?
                            (',' '**' expression)?
                          | '*' expression (',' '*' expression)? (',' '**' expression)?
                          | '**' expression;
positional_arguments :  expression (',' expression)*;
keyword_arguments    :  keyword_item (',' keyword_item)*;
keyword_item         :  identifier '=' expression;



//operations
power :  primary ('**' u_expr)?;
u_expr :  power | '-' u_expr | '+' u_expr | '~' u_expr;
m_expr :  u_expr | m_expr '*' u_expr | m_expr '//' u_expr | m_expr '/' u_expr | m_expr '%' u_expr;
a_expr :  m_expr | a_expr '+' m_expr | a_expr '-' m_expr;
shift_expr :  a_expr | shift_expr ( '<<' | '>>' ) a_expr;
and_expr :  shift_expr | and_expr '&' shift_expr;
xor_expr :  and_expr | xor_expr '^' and_expr;
or_expr  :  xor_expr | or_expr '|' xor_expr;
comparison    :  or_expr ( comp_operator or_expr )*;
comp_operator :  '<' | '>' | '==' | '>=' | '<=' | '<>' | '!=' | IS (NOT)? | (NOT)? IN;
or_test  :  and_test | or_test OR and_test;
and_test :  not_test | and_test AND not_test;
not_test :  comparison | NOT not_test;
conditional_expression :  or_test (IF or_test ELSE expression)?;
lambda_form     :  LAMBDA (parameter_list)? ':' expression;
old_lambda_form :  LAMBDA (parameter_list)? ':' old_expression;


//top level of expressions
expression:  conditional_expression | lambda_form;

//handy
expression_list :  expression ( ',' expression )* (',')?;

//STATEMENTS
// simple statements
simple_stmt:      expression_stmt
                 | assert_stmt
                 | assignment_stmt
                 | augmented_assignment_stmt
                 | pass_stmt
                 | del_stmt
                 | print_stmt
                 | return_stmt
                 | yield_stmt
                 | raise_stmt
                 | break_stmt
                 | continue_stmt
                 | import_stmt
                 | global_stmt
                 | exec_stmt;

expression_stmt:  expression_list;

assignment_stmt: (target_list '=')+ expression_list | yield_expression;
target_list: target (',' target)* ','?;
target:  identifier #TargetIdentifier //essentially a primary, but without atoms
      | '(' target_list ')' #TargetTuple
      | '[' target_list ']' #TargetList
      | target '.' identifier # TargetAttributeRef
      | target '[' expression_list ']' # TargetSubscription
      | target '[' short_slice ']' #TargetShortSlicing
      | target '[' slice_list ']' # TargetLongSlicing
      ;
//      | attributeref
//      | subscription
//      | slicing ;

augmented_assignment_stmt:  augtarget augop (expression_list | yield_expression);
augtarget:  identifier # AugTargetIdentifier
       | primary '.' identifier # AugTargetAttributeRef
       | primary '[' expression_list ']' # AugTargetSubscription
       | primary '[' short_slice ']' #AugTargetShortSlicing
       | primary '[' slice_list ']' # AugTargetLongSlicing
        ;
augop:  '+=' | '-=' | '*=' | '/=' | '//=' | '%=' | '**=' | '>>=' | '<<=' | '&=' | '^=' | '|=';


assert_stmt: ASSERT expression (',' expression)?;


pass_stmt: PASS; //complicated stuff

del_stmt: DEL target_list;

print_stmt: PRINT ((expression (',' expression)* (',')?)?
          | '>>' expression ((',' expression)+ (',')?)?); //not sure what this is

return_stmt: RETURN expression_list?;

yield_stmt: YIELD yield_expression;

raise_stmt: RAISE (expression (expression (expression)?)?)?; //silly raise statement

break_stmt: BREAK;

continue_stmt: CONTINUE;

import_stmt     : IMPORT module (AS name)? ( ',' module (AS name)? )*
                     | FROM relative_module IMPORT identifier (AS name)? ( ',' identifier (AS name)? )*
                     | FROM relative_module IMPORT '(' identifier (AS name)? ( ',' identifier (AS name)? )* ','? ')'
                     | FROM module IMPORT '*';
module          : (identifier '.')* identifier;
relative_module :  '.'* module | '.'+;
name            : identifier;

global_stmt :  GLOBAL identifier (',' identifier)*;

exec_stmt :  EXEC or_expr (IN expression (',' expression)?)?; //or_expr because expressions with lower precedence than '|' aren't allowed

         

// full statements
statement: (stmt_list NEWLINE | compound_stmt);
stmt_list: simple_stmt (';' simple_stmt)* ';'?;



//compound statements
compound_stmt:  if_stmt
    | while_stmt
    | for_stmt
    | try_stmt
    | with_stmt
    | funcdef
    | classdef
    | decorated;

suite: stmt_list NEWLINE | NEWLINE INDENT statement+ DEDENT;

if_stmt:  IF expression ':' suite 
          (ELIF expression ':' suite)* 
          (ELSE ':' suite)?;

while_stmt:  WHILE expression ':' suite
             (ELSE ':' suite)?;

for_stmt:  FOR target_list IN expression_list ':' suite
           (ELSE ':' suite)?;

try_stmt:  ((TRY ':' suite
            (EXCEPT (expression ((AS | ',') target)? )? ':' suite)+
            (ELSE ':' suite)?
            (FINALLY ':' suite)? )
           |(TRY ':' suite
             FINALLY ':' suite));

with_stmt:  WITH with_item (',' with_item)* ':' suite;
with_item:  expression (AS target)?;

funcdef:  DEF identifier '(' parameter_list ')' ':' suite;
parameter_list:  (defparameter ',')*
                 ('*' identifier (',' '**' identifier)?
                 |'**' identifier
                 |defparameter ','?);
defparameter:  parameter ('=' expression)?;
parameter:  identifier | '(' parameter (',' parameter)* ','? ')'; //sublist

classdef:  CLASS identifier ('(' expression_list ')')? ':' suite;

decorated: decorator+ (classdef | funcdef);
decorator:  '@' identifier ('.' identifier)*  ('(' (argument_list ','? )? ')')? NEWLINE;







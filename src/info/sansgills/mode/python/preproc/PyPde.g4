/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar PyPde;

@parser::header {
                 package info.sansgills.mode.python.preproc;
                 }

@lexer::header {
                package info.sansgills.mode.python.preproc;
                
                import java.util.Stack;
                import java.util.Deque;
                import java.util.ArrayDeque;
                import java.lang.Integer;
                import org.antlr.v4.runtime.CommonToken;
                import org.antlr.v4.runtime.misc.Interval;
                }

//some black magic
@lexer::members {
                 int bracket_nesting_level = 0;
                 int parenth_nesting_level = 0;
                 int curlybr_nesting_level = 0;
                                                   
                 Stack<Integer> whitespace_stack = new Stack<Integer>();
                 {
                        whitespace_stack.push(new Integer(0));
                  }
                 
                 
                 Deque<Token> pendingTokens = new ArrayDeque<Token>();
                 
                 @Override
                 public Token nextToken() {                        
                        while(pendingTokens.isEmpty()){
                               Token token = super.nextToken();
                               switch(token.getType()){
                               case EOF: //EOF; dedent until we're at the bottom of the stack and then pop off a newline
                                      //System.out.println("EOF");
                                      if(whitespace_stack.peek().intValue() != 0){
                                      		 pendingTokens.add(new CommonToken(NEWLINE, "\n"));
                                             while(whitespace_stack.peek().intValue() != 0){
                                                    whitespace_stack.pop();
                                                    pendingTokens.add(new CommonToken(DEDENT, ""));
                                             }
                                             pendingTokens.add(new CommonToken(NEWLINE, "\n"));
                                      }
                                      pendingTokens.add(new CommonToken(EOF, "<EOF>"));
                                      break;
                               case NEWLINE: 
                                      //System.out.println("newline");
                                      pendingTokens.add(token);
                                      //dynamically generate *DENT tokens
                                      int next = 0; //level of indentation on next line
                                      int current_position = token.getText().length();
                                      String followingText = _input.getText(new Interval(_tokenStartCharIndex, _tokenStartCharIndex+32));
                                      boolean empty_line = false;
                                      while(true){
                                             if(current_position >= followingText.length()){
                                                    followingText = _input.getText(new Interval(_tokenStartCharIndex, _tokenStartCharIndex+current_position+32));
                                                    if(current_position >= followingText.length()){
                                                           //we've reached the end of the text
                                                           break;
                                                    }
                                             }
                                             if(followingText.charAt(current_position) == ' '){
                                                    next++;
                                             }else if(followingText.charAt(current_position) == '\t'){
                                                    next+=8;
                                                    next-=next%8;
                                             }else if(followingText.charAt(current_position) == '\r' || followingText.charAt(current_position) == '\n'){
                                                    empty_line = true;
                                                    break;
                                             }else{
                                                    break;
                                             }
                                             current_position++;
                                      }
                                      if(empty_line){
                                                     //System.out.println("empty line");
                                                     break;
                                                     }
                                      
                                      //next now matches the amount of whitespace beginning the next line
                                      int cur = whitespace_stack.peek().intValue();
                                      //System.out.println("next line: "+next+" current line: "+cur);
                                      if(next > cur){
                                             whitespace_stack.push(new Integer(next));
                                             pendingTokens.add(new CommonToken(INDENT, ""));
                                             //System.out.println("indenting");
                                      }else if(next < cur){
                                             while(next < whitespace_stack.peek().intValue()){
                                                    whitespace_stack.pop();
                                                    pendingTokens.add(new CommonToken(DEDENT, ""));
                                                    //System.out.println("dedenting");
                                             }
                                      }
                                      break;
                               default:
                                      pendingTokens.add(token);
                                      break;
                               }
                        }
                        return pendingTokens.poll();
                 }
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

COMMENT: '#' (~'\n')* -> skip; //no need to keep these

//logical vs. physical line nonsense
LPAREN: '(' {parenth_nesting_level++;};
RPAREN: ')' {parenth_nesting_level--;};
LCURLY: '{' {curlybr_nesting_level++;};
RCURLY: '}' {curlybr_nesting_level--;};
LBRACKET: '[' {bracket_nesting_level++;};
RBRACKET: ']' {bracket_nesting_level--;};

//we're inside a grouping, newlines don't count
IMPLICIT_ESCAPE_NEWLINE: {parenth_nesting_level > 0 ||
                          curlybr_nesting_level > 0 ||
                          bracket_nesting_level > 0}? ('\n' | '\r\n') -> skip;
EXPLICIT_ESCAPE_NEWLINE: '\\' '\r'? '\n' -> skip; //explicit line joins


NEWLINE: '\r'?'\n'; //above not true; these newlines count

//never generate these automatically
INDENT: {false}? 'IN';
DEDENT: {false}? 'DE';

WS: [ \t]+ -> channel(HIDDEN);


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
 
yield_atom       :  '(' yield_expression ')';
yield_expression :  YIELD expression_list?;


// primaries: most tightly bound operations
 
primary : primary '.' identifier # AttributeRef
        | primary '[' expression_list ']' # Subscription
        | primary '[' short_slice ']' # ShortSlicing
        | primary '[' slice_list ']'  # Slicing
        | primary '(' (argument_list ','? | expression comp_for)? ')' # Call
        | atom # PrimaryAtom
        ;

slice_list       :  slice_item (',' slice_item)* ','?;
slice_item       :  expression | proper_slice | '...';
proper_slice     :  short_slice | long_slice;
short_slice      :  (expression)? ':' (expression)?;
long_slice       :  short_slice ':' (expression)?;



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

suite: stmt_list NEWLINE #SimpleSuite
	 | NEWLINE INDENT (statement | NEWLINE)+ DEDENT #ComplexSuite
	 ;

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

funcdef:  DEF identifier '(' parameter_list? ')' ':' suite;
parameter_list:  (defparameter ',')*
                 ('*' identifier (',' '**' identifier)?
                 |'**' identifier
                 |defparameter ','?);
defparameter:  parameter ('=' expression)?;
parameter:  identifier | '(' parameter (',' parameter)* ','? ')'; //sublist

classdef:  CLASS identifier ('(' expression_list? ')')? ':' suite;

decorated: decorator+ (classdef | funcdef);
decorator:  '@' identifier ('.' identifier)*  ('(' (argument_list ','? )? ')')? NEWLINE;







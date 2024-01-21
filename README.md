#Recursive Descent Parser for MODULA-2 Subset

This project implements a recursive descent parser for a subset of the MODULA-2 programming language. The grammar rules define the structure of MODULA-2 programs using production rules. The parser is designed to recognize and parse valid MODULA-2 code based on the specified grammar.

#Grammar Rules
<
The parser adheres to the following grammar rules:
	module-decl -> module-heading    declarations   procedure-decl   block    name  .
 	module-heading   -> module        name      ; 
	block    begin        stmt-list         end
	declarations     const-decl    var-decl    
    	const-decl   const    const-list     |       
const-list       ( name      =    value   ;  )* 
	var-decl   var    var-list     |      
var-list     ( var-item     ;  )*    
var-item     name-list     :     data-type    
name-list     name    ( ,   name )*
	data-type     integer    |    real   |     char 
	procedure-decl     procedure-heading        declarations        block       name  ;
	procedure-heading    procedure        name      ; 
stmt-list      statement    ( ;   statement )*  
	statement   ass-stmt   |    read-stmt    |    write-stmt    |      if-stmt   
                                  |  while-stmt    |     repeat-stmt  |   exit-stmt   |   call-stmt    |       
ass-stmt  name     :=      exp
exp  term     (  add-oper    term  )* 
	term  factor   ( mul-oper   factor )*     
	factor   “(“     exp     “)”     |     name      |      value
	add-oper   +    |     -  
	mul-oper  *     |     /       |      mod     |    div
	read-stmt readint   “(“    name-list “)”     |  readreal   “(“    name-list  “)”     
                              |     readchar    “(“    name-list    “)”    |    readln  
  	write-stmt writeint  “(“  write-list “)”   |  writereal   “(“  write-list    “)”     
                               writechar  “(“    write-list “)”     |    writeln  
write-list     write-item    ( ,   write-item )*
write-item     name   |    value   
if-stmt  if  condition   then   stmt-list   elseif-part   else-part    end
elseif-part  ( elseif  condition   then   stmt-list  )*      
	else-part    else     stmt-list     |    
	while-stmt  while      condition       do      stmt-list   end
	repeat-stmt     loop      stmt-list       until        condition   
	exit-stmt     exit      
call-stmt     call name          (*  This is a procedure name   *)
	condition    name-value       relational-oper        name-value   
          	name-value   name    |      value 
relational-oper   =      |     |=    |    <     |       <=     |     >     |     >=
name  letter ( letter | digit )*
value  integer-value   |   real-value
integer-value  digit ( digit )*    
real-value  digit ( digit )*. digit ( digit )*
>


import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final LexicalAnalyser lexicalAnalyser;
    private Token token;

    //Just to hold the tokens in case wanted to print them
    List<Token> generatedToken = new ArrayList<>();

    public Parser(LexicalAnalyser lexicalAnalyser) {
        this.lexicalAnalyser = lexicalAnalyser;
        try {
            this.token = lexicalAnalyser.getNextToken();
            generatedToken.add(token);
        } catch (Exception e) {
            throw new RuntimeException("error parsing at line 0 ");
        }
    }

    //given module-decl  module-heading    declarations   procedure-decl   block    name  .
    public void parseModuleDecal() throws Exception {
        parseModuleHeading();
        parseDeclarations();
        parseProcedureDeclerations();
        parseBlock();
        parseName();
        consumeToken("DOT");
    }

    //for parsing the module name;
    private void parseModuleHeading() throws Exception {
        consumeToken(LexicalAnalyser.MODULE);
        consumeToken(LexicalAnalyser.IDENTIFIER);
        consumeToken(LexicalAnalyser.SEMICOLON);
    }

    //used for parsing constants and var declarations
    private void parseDeclarations() throws Exception {
        parseConstantDeclarations();
        parseVarDeclarations();
        parseConstantDeclarations();
//        System.out.println(token.getType() + ", " + token.getValue());
    }

    //parse constants
    private void parseConstantDeclarations() throws Exception {
        //we have constant declarations -> constList | lambda -> (name = value )*;
//        System.out.println("Curr: "+ token.getType()+" --- "+token.getValue());
        if (token.getType().equals(LexicalAnalyser.CONST)) {
//            token = lexicalAnalyser.getNextToken();
            consumeToken(LexicalAnalyser.CONST);
            parseConstantList();
        }
    }

    //parse constatns as long s no other key word has accord , const a=10; b=100 etc....
    private void parseConstantList() throws Exception {
        while (!(token.getType().equals(LexicalAnalyser.VAR) || token.getType().equals(LexicalAnalyser.BEGIN)
                || token.getType().equals(LexicalAnalyser.PROCEDURE))) { // we are still declaring const list
            parseName();
            consumeToken(LexicalAnalyser.EQUAL);
            parseValue();
            consumeToken(LexicalAnalyser.SEMICOLON);
        }
    }

    //parse var declaration for module
    private void parseVarDeclarations() throws Exception {
        if (token.getType().equals(LexicalAnalyser.VAR)) {
            consumeToken(LexicalAnalyser.VAR);
            parseVarList();
        }
    }

    //keep parsing as long as the no other key word accord
    private void parseVarList() throws Exception {
        while (!(token.getType().equals(LexicalAnalyser.CONST) || token.getType().equals(LexicalAnalyser.BEGIN)
                || token.getType().equals(LexicalAnalyser.PROCEDURE))) {
            parseVarItem();
        }
    }

    private void parseVarItem() throws Exception {
        parseNameList();
        //after list of names we have type
        consumeToken(LexicalAnalyser.DATATYPE);
        parseDataType();
        consumeToken(LexicalAnalyser.SEMICOLON);
    }

    //parse the var name lists x,c,a,b until-> : (type)
    private void parseNameList() throws Exception {
        parseName();
        while (token.getType().equals(LexicalAnalyser.COMMA)) {
            consumeToken(LexicalAnalyser.COMMA);
            parseName();
        }
    }

    //parsing data types
    public void parseDataType() throws Exception {
        if (token.getType().equalsIgnoreCase(LexicalAnalyser.INTEGER_LITERAL)) {
            consumeToken(LexicalAnalyser.INTEGER_LITERAL);  // Consume datatype

        } else if (token.getType().equalsIgnoreCase(LexicalAnalyser.REAL_LITERAL)) {
            consumeToken(LexicalAnalyser.REAL_LITERAL);

        } else if (token.getType().equalsIgnoreCase(LexicalAnalyser.CHAR_LITERAL)) {
            consumeToken(LexicalAnalyser.CHAR_LITERAL);
        } else {
            parseError("Invalid datatype");
        }
    }

    //used for parsing procedures
    // self-Note:parse procedure declarations using same one as for the module since they are the same don't build a new one :)
    private void parseProcedureDeclerations() throws Exception {
        while (token.getType().equals(LexicalAnalyser.PROCEDURE)) {
            parseProcedureHeading();
            parseDeclarations();
            parseBlock();
            parseName();
            consumeToken(LexicalAnalyser.SEMICOLON);
        }
    }

    //parse the heading of (procedure name;)
    private void parseProcedureHeading() throws Exception {
        consumeToken(LexicalAnalyser.PROCEDURE);
        parseName();
        consumeToken(LexicalAnalyser.SEMICOLON);
    }

    //consuming body/block of procedure/module starting with (begin) and ending with (end)
    private void parseBlock() throws Exception {
        consumeToken(LexicalAnalyser.BEGIN);
        parseStatementLists();
        consumeToken(LexicalAnalyser.END);
    }

    //used to parse the list of stmts given by stmt-list statement    ( ;   statement )*
    private void parseStatementLists() throws Exception {
        parseStatement();
        while (token.getType().equals(LexicalAnalyser.SEMICOLON)) {//didn't reach end yet
            consumeToken(LexicalAnalyser.SEMICOLON);
            if (token.getType().equals(LexicalAnalyser.END))
                break;
            parseStatement();
        }
    }

    //for pasing each statement on its own
    //statement   ass-stmt   |    read-stmt    |    write-stmt    |      if-stmt
    // |  while-stmt    |     repeat-stmt  |   exit-stmt   |   call-stmt    |   lambda
    private void parseStatement() throws Exception {
        if (token.getType().equals(LexicalAnalyser.IDENTIFIER)) {
            parseAssignmentStmt();
        } else if (token.getType().equals(LexicalAnalyser.READINT) ||
                token.getType().equals(LexicalAnalyser.READREAL) ||
                token.getType().equals(LexicalAnalyser.READCHAR) ||
                token.getType().equals(LexicalAnalyser.READLN)) {
            parseReadStmt();
        } else if (token.getType().equals(LexicalAnalyser.WRITEINT) ||
                token.getType().equals(LexicalAnalyser.WRITEREAL) ||
                token.getType().equals(LexicalAnalyser.WRITECHAR) ||
                token.getType().equals(LexicalAnalyser.WRITELN)) {
            parseWriteStmt();
        } else if (token.getType().equals(LexicalAnalyser.IF)) {
            parseIfStmt();
        } else if (token.getType().equals(LexicalAnalyser.WHILE)) {
            parseWhileStmt();
        } else if (token.getType().equals(LexicalAnalyser.LOOP)) {
            parseRepeatStmt();
        } else if (token.getType().equals(LexicalAnalyser.EXIT)) {
            parseExitStmt();
        } else if (token.getType().equals(LexicalAnalyser.CALL)) {
            parseCallStmt();
        } else {
            // here is lamda condition no statements
        }

    }


//This block for parsing assignments

    //    ____________________________________________________________________________
    //parsing assignment statement given ass-stmt  name:=expression
    private void parseAssignmentStmt() throws Exception {
        parseName();
        consumeToken(LexicalAnalyser.ASSIGNMENT);
        parseExpression();
    }

    //parsing expr given exp  term(  add-oper    term  )*
    private void parseExpression() throws Exception {
        parseTerm();
        //parsing add-opreations given add-oper   +    |     -
        while (token.getType().equals(LexicalAnalyser.PLUS) || token.getType().equals(LexicalAnalyser.MINUS)) {
            consumeToken(token.getType());
            parseTerm();
        }
    }

    //parsing term given term  factor( mul-oper   factor )*
    private void parseTerm() throws Exception {
        parseFactor();
        //parsing mul-operations given mul-oper  *     |     /       |      mod     |    div
        while (token.getType().equals(LexicalAnalyser.DIVIDE) ||
                token.getType().equals(LexicalAnalyser.MULTIPLY)
                || token.getType().equals(LexicalAnalyser.DIV) || token.getType().equals(LexicalAnalyser.MOD)) {
            consumeToken(token.getType());
            parseFactor();
        }
    }

    //parsing a factor given factor   “(“     exp     “)”     |     name      |      value
    private void parseFactor() throws Exception {
        if (token.getType().equals(LexicalAnalyser.OPEN_PAREN)) {
            consumeToken(LexicalAnalyser.OPEN_PAREN);
            parseExpression();
            consumeToken(LexicalAnalyser.CLOSE_PAREN);
        } else if (token.getType().equals(LexicalAnalyser.IDENTIFIER)) {
            parseName();
        } else if (token.getType().equals(LexicalAnalyser.NUMBER)) {
            parseValue();
        } else {
            parseError("Expected a factor, but got: " + token.getType());
        }
    }
//    ____________________________________________________________________________

    // parsing read statement block
    //____________________________________________________________________________

    //for parsing read statements
// read-stmt
// readint   “(“    name-list “)”     |  readreal   “(“    name-list  “)”
// |     readchar    “(“    name-list    “)”    |    readln
    //self-note: type mismatch errors don't arise in this stage
    private void parseReadStmt() throws Exception {
        if (token.getType().equals(LexicalAnalyser.READLN)) {
            consumeToken(LexicalAnalyser.READLN);
        } else {
            consumeToken(token.getType());
            consumeToken(LexicalAnalyser.OPEN_PAREN);
            parseNameList();
            consumeToken(LexicalAnalyser.CLOSE_PAREN);
        }
    }

    //____________________________________________________________________________

    //    for parsing write statements
    //____________________________________________________________________________
    //given write-stmt 
    // writeint  “(“  write-list “)”   |  writereal   “(“  write-list    “)”
    // | writechar  “(“    write-list “)”     |    writeln
    private void parseWriteStmt() throws Exception {
        if (token.getType().equals(LexicalAnalyser.WRITELN)) {
            consumeToken(LexicalAnalyser.WRITELN);
        } else {
            consumeToken(token.getType());
            consumeToken(LexicalAnalyser.OPEN_PAREN);
            parseWriteList();
            consumeToken(LexicalAnalyser.CLOSE_PAREN);
        }
    }

    //given write-list     write-item    ( ,   write-item )*
    private void parseWriteList() throws Exception {
        parseWriteItem();
        while (token.getType().equals(LexicalAnalyser.COMMA)) {
            consumeToken(LexicalAnalyser.COMMA);
            parseWriteItem();
        }
    }

    //write-item     name   |    value
    private void parseWriteItem() throws Exception {
        if (token.getType().equals(LexicalAnalyser.IDENTIFIER) || token.getType().equals(LexicalAnalyser.NUMBER)) {
            consumeToken(token.getType());
        } else {
            parseError("Invalid write Item should be either " + LexicalAnalyser.IDENTIFIER + ", or" + LexicalAnalyser.NUMBER);
        }
    }

//____________________________________________________________________________


    //    parsing if statement
//    ____________________________________________________________________________

    //production rule given: if-stmt  if  condition   then   stmt-list   elseif-part   else-part    end
    private void parseIfStmt() throws Exception {
        consumeToken(LexicalAnalyser.IF);
        parseCondition();
        consumeToken(LexicalAnalyser.THEN);
        parseStatementLists();
        parseElseIfPart();
        parseElsePart();
        consumeToken(LexicalAnalyser.END);
    }

    //elseif-part  ( elseif  condition   then   stmt-list  )*
    private void parseElseIfPart() throws Exception {
        while (token.getType().equals(LexicalAnalyser.ELSIF)) {
            consumeToken(LexicalAnalyser.ELSIF);
            parseCondition();
            consumeToken(LexicalAnalyser.THEN);
            parseStatementLists();
        }
    }

    //    else-part    else     stmt-list     |   lamda
    private void parseElsePart() throws Exception {
        if (token.getType().equals(LexicalAnalyser.ELSE)) {
            consumeToken(LexicalAnalyser.ELSE);
            parseStatementLists();
        }
    }

    //    condition    name-value       relational-oper        name-value
    //note name-value is equal to write-item both equals (name | value)
    private void parseCondition() throws Exception {
        parseWriteItem();
        parseRelationalOperations();
        parseWriteItem();
    }

    //given relational-oper   =      |     |=    |    <     |       <=     |     >     |     >=
    private void parseRelationalOperations() throws Exception {
        if (token.getType().equals(LexicalAnalyser.EQUAL) ||
                token.getType().equals(LexicalAnalyser.NOT_EQUAL) ||
                token.getType().equals(LexicalAnalyser.LESS_THAN) ||
                token.getType().equals(LexicalAnalyser.LESS_THAN_OR_EQUAL) ||
                token.getType().equals(LexicalAnalyser.GREATER_THAN) ||
                token.getType().equals(LexicalAnalyser.GREATER_THAN_OR_EQUAL)) {

            consumeToken(token.getType());
        } else {
            parseError("Expected relational operation, but got: " + token.getType());
        }
    }

    //____________________________________________________________________________

    //    parsing while
//    ____________________________________________________________________________
//    given: while-stmt  while      condition       do      stmt-list   end
    private void parseWhileStmt() throws Exception {
        consumeToken(LexicalAnalyser.WHILE);
        parseCondition();
        consumeToken(LexicalAnalyser.DO);
        parseStatementLists();
        consumeToken(LexicalAnalyser.END);
    }
//____________________________________________________________________________


    //    parsing Repeat statement
//    ____________________________________________________________________________
//    given:repeat-stmt     loop      stmt-list       until        condition
    private void parseRepeatStmt() throws Exception {
        consumeToken(LexicalAnalyser.LOOP);
        parseStatementLists();
        consumeToken(LexicalAnalyser.UNTIL);
        parseCondition();
    }
//    ____________________________________________________________________________

    //    parsing ExitStatement
//    ____________________________________________________________________________
//    given: exit-stmt     exit
    private void parseExitStmt() throws Exception {
        consumeToken(LexicalAnalyser.EXIT);
    }

    //parsing call statement
//    ____________________________________________________________________________
//    given: call-stmt     call name          (*  This is a procedure name   *)
    private void parseCallStmt() throws Exception {
        consumeToken(LexicalAnalyser.CALL);
        parseName();
    }

    //check that we have a name here
    private void parseName() throws Exception {
        if (token.getType().equals(LexicalAnalyser.IDENTIFIER)) {
            consumeToken(LexicalAnalyser.IDENTIFIER);
        } else {
            parseError("Expected identifier, but got " + token.getType());
        }
    }

    private void parseValue() throws Exception {
        if (token.getType().equals(LexicalAnalyser.NUMBER)) {
            consumeToken(LexicalAnalyser.NUMBER);
        } else {
            parseError("Expected Number, but got " + token.getType() +
                    " at line " + token.getLineNumber());
        }
    }


    private void consumeToken(String expectedType) throws Exception {
        if (token.getType().equals(expectedType)) {
            token = lexicalAnalyser.getNextToken();
            generatedToken.add(token);
        } else {
            throw new Error("Lexical error:Expected token type '" + expectedType + "', but got '" +
                    token.getType() + "', for token " + token.getValue(), token.getLineNumber());
        }
    }

    private void parseError(String message) throws Exception {
        throw new Error("Parsing Error: " + message + ", at token: " + token.getValue(), token.getLineNumber());
    }
}
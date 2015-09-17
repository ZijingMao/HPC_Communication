import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Parser {
	    // Define two flags to identify positions 
		public static int labelNumber;
		public int lexemePointer = -1;
		
		// Define output stream for output file
		public OutputStream parseOut, cfgOut, mipsOut;
		public static java.io.PrintStream printStream;
		public static java.io.PrintStream cfgStream;
		public static java.io.PrintStream mipsStream;
		
		// Define token and token list for next token
		public int nextToken;
		public ArrayList<Integer> tokenList = new ArrayList<Integer>();	
		public ArrayList<String> lexemeList = new ArrayList<String>();
		public Parser(Scanner scanner, String parseOutName, String cfgName, String mipsOut) throws FileNotFoundException{
			try {
				this.parseOut = new FileOutputStream(parseOutName);
				this.cfgOut = new FileOutputStream(cfgName);
				this.mipsOut = new FileOutputStream(mipsOut);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			printStream = new java.io.PrintStream(parseOut);
			cfgStream = new java.io.PrintStream(cfgOut);
			mipsStream = new java.io.PrintStream(mipsOut);
			this.lexemeList= scanner.lexemeList; 
			this.tokenList = scanner.tokenList;	
			labelNumber = 2;
		}
		
		public Program_ast program() throws Exception{
			Program_ast program_ast = new Program_ast();
			nextToken = getNextToken();
			if(nextToken != Symbols.PROGRAM){
				throw new Exception("error:must start with PROGRAM");
			}			
			while((nextToken = getNextToken()) != Symbols.BEGIN){
				Declarations_ast declarations_ast = declarations();
				program_ast.declarations.add(declarations_ast);
			}
			if(nextToken != Symbols.BEGIN){
				throw new Exception("error:must followed by BEGIN");
			}
			nextToken = getNextToken();
			while(nextToken  != Symbols.END){
				Statement_ast statement_ast = statementSequence();
				program_ast.statements.add(statement_ast);
			}
			if(nextToken != Symbols.END){
				throw new Exception("error:must followed by END");
			}
			if((nextToken = getNextToken()) != Symbols.EOF){
				throw new Exception("error:must ended by EOF");
			}
			return program_ast;
		}
		
		private Declarations_ast declarations() throws Exception {
			Declarations_ast declarations_ast = new Declarations_ast();
			if(nextToken == Symbols.VAR){
				if((nextToken = getNextToken()) != Symbols.IDENT){
					throw new Exception("error:must followed by IDENT");
				}
				declarations_ast.name = lexemeList.get(lexemePointer);
				if((nextToken = getNextToken()) != Symbols.AS){
					throw new Exception("error:must followed by AS");
				}
				nextToken = getNextToken();
				declarations_ast.type = type();
				
				if((nextToken = getNextToken()) != Symbols.SC){
					throw new Exception("error:must followed by SC");
				}
			}
			else{
				throw new Exception("error: delaration().");
			}
			
			return declarations_ast;
		}
		
		private Type_ast type() throws Exception {
			Type_ast type_ast = new Type_ast();	
			if(nextToken == Symbols.INT || nextToken == Symbols.BOOL || nextToken == Symbols.CHAR){
				type_ast.type = lexemeList.get(lexemePointer);
			}else{
				throw new Exception("error: type()");
			}	
			return type_ast;
		}

		private Statement_ast statementSequence() throws Exception {
			Statement_ast statement_ast;
			if(nextToken == Symbols.IDENT){
				statement_ast = assignment();
			}
			else if(nextToken == Symbols.IF){
				statement_ast = ifStatement();
			}
			else if(nextToken == Symbols.WHILE){
				statement_ast = whileStatement();
			}
			else if(nextToken == Symbols.WRITEINT){
				statement_ast = writeInt();
			}
			else{
				throw new Exception("error: statement");
			}		
			if(nextToken != Symbols.SC){
				throw new Exception("error:must followed by SC");
			}	
			nextToken = getNextToken();
			return statement_ast;
		}
		
		private Assignment_ast assignment() throws Exception {
			Assignment_ast assignment_ast = new Assignment_ast();		
			if(nextToken == Symbols.IDENT){
				assignment_ast.left = lexemeList.get(lexemePointer);
				if((nextToken = getNextToken())  != Symbols.ASSIGN){
					throw new Exception("error:must followed by ASSIGN"); 
				}
				assignment_ast.sign = ":=";
				if((nextToken = getNextToken())  == Symbols.READINT){
					assignment_ast.sign = ":= readInt";
					nextToken = getNextToken();
				}
				else if(nextToken == Symbols.IDENT || nextToken == Symbols.NUMBER || nextToken == Symbols.TRUE || 
						nextToken == Symbols.FALSE ||nextToken == Symbols.LPAREN || nextToken == Symbols.CHARACTER){
					assignment_ast.right = expression();
				}
				else{
					throw new Exception("error:assignment()"); 
				}
			}
			else
				throw new Exception("error:assignment()");				
			return assignment_ast;
		}
		
		private IfStatement_ast ifStatement() throws Exception {
			IfStatement_ast ifStatement_ast = new IfStatement_ast();	
			if(nextToken != Symbols.IF){
				throw new Exception("error:must start with IF"); 
			}
			ifStatement_ast.name = "if";
			nextToken=getNextToken();
			if(nextToken == Symbols.IDENT || nextToken == Symbols.NUMBER || nextToken == Symbols.TRUE || 
					nextToken == Symbols.FALSE ||nextToken == Symbols.LPAREN){
				ifStatement_ast.expression = expression();
			}
			if(nextToken != Symbols.THEN){
				throw new Exception("error:must followed by THEN"); 
			}
			nextToken=getNextToken();
			while(nextToken  != Symbols.ELSE && nextToken != Symbols.END){
				Statement_ast Statement_ast = statementSequence();
				ifStatement_ast.thenStatements.add(Statement_ast);
			}
			
			while(nextToken  != Symbols.END){
				Statement_ast Statement_ast = statementSequence();
				ifStatement_ast.elseStatements.add(Statement_ast);
			}
			if(nextToken != Symbols.END){
				throw new Exception("error: must followed by END"); 
			}
			nextToken=getNextToken();
			return ifStatement_ast;
		}

		private WhileStatement_ast whileStatement() throws Exception {
			WhileStatement_ast whileStatement_ast = new WhileStatement_ast();		
			if(nextToken != Symbols.WHILE){
				throw new Exception("error:must start with WHILE"); 
			}
			whileStatement_ast.name = "while";
			nextToken = getNextToken();
			if(nextToken == Symbols.IDENT || nextToken == Symbols.NUMBER || nextToken == Symbols.TRUE || nextToken == Symbols.FALSE ||
					nextToken == Symbols.LPAREN){
				whileStatement_ast.expression = expression();
			}
			if(nextToken != Symbols.DO){
				throw new Exception("error:must followed by DO"); 
			}
			nextToken = getNextToken();
			while(nextToken != Symbols.END){
				Statement_ast statement_ast = statementSequence();
				whileStatement_ast.whileStatements.add(statement_ast);
			}	
			if(nextToken != Symbols.END){
				throw new Exception("error:must followed by END"); 
			}
			nextToken = getNextToken();
			return whileStatement_ast;
		}
		
		private WriteInt_ast writeInt() throws Exception {
			WriteInt_ast writeInt_ast = new WriteInt_ast();
			if(nextToken != Symbols.WRITEINT){
				throw new Exception("WRITEINT error."); 
			}
			writeInt_ast.name = "writeInt";
			nextToken=getNextToken();
			if(nextToken == Symbols.IDENT || nextToken == Symbols.NUMBER || nextToken == Symbols.TRUE || nextToken == Symbols.FALSE ||
					nextToken == Symbols.LPAREN){
				writeInt_ast.expression = expression();
			}	
			return writeInt_ast;
		}
		
		private Expression_ast expression() throws Exception {
		    Expression_ast expression_ast = new Expression_ast();
			expression_ast.left = simpleExpression();
			if(nextToken == Symbols.EQUAL || nextToken == Symbols.UNEQUAL || nextToken == Symbols.MORE ||
					nextToken == Symbols.MOREEQ ||nextToken == Symbols.LESS || nextToken == Symbols.LESSEQ){
				expression_ast.op = lexemeList.get(lexemePointer);		
				nextToken = getNextToken();
				expression_ast.right = simpleExpression();
			}
			else if(nextToken == Symbols.SC||nextToken == Symbols.DO||nextToken == Symbols.THEN){
			}
			else{
				throw new Exception("error:expression()");
			}	
			return expression_ast;
		}

		private SimpleExpression_ast simpleExpression() throws Exception {
			SimpleExpression_ast simpleExpression_ast = new SimpleExpression_ast();
			simpleExpression_ast.left = term();
			if(nextToken == Symbols.PLUS || nextToken == Symbols.MINUS){
				simpleExpression_ast.op = lexemeList.get(lexemePointer);
				nextToken = getNextToken();
				simpleExpression_ast.right = term();
			}
			else if(nextToken == Symbols.SC || nextToken == Symbols.EQUAL || nextToken == Symbols.UNEQUAL || nextToken == Symbols.MORE ||
						nextToken == Symbols.MOREEQ || nextToken == Symbols.LESS || nextToken == Symbols.LESSEQ||nextToken == Symbols.DO||
						nextToken == Symbols.THEN){
			}
			else{
				throw new Exception("error:simpleExpression().");
			}
			return simpleExpression_ast;
		}

		private Term_ast term() throws Exception{
			Term_ast term_ast = new Term_ast();
			term_ast.left = factor();
			if(nextToken == Symbols.TIMES ||nextToken == Symbols.DIVIDE ||nextToken == Symbols.MOD){
				term_ast.op = lexemeList.get(lexemePointer);
				nextToken = getNextToken();	
				term_ast.right = factor();
			}
			else if(nextToken == Symbols.SC || nextToken == Symbols.EQUAL || nextToken == Symbols.UNEQUAL || nextToken == Symbols.MORE ||
						nextToken == Symbols.MOREEQ || nextToken == Symbols.LESS || nextToken == Symbols.LESSEQ ||nextToken == Symbols.PLUS || 
						nextToken == Symbols.MINUS || nextToken == Symbols.DO || nextToken == Symbols.THEN){
			}
			else{
				throw new Exception("error: term()");
			}
			return term_ast;
		}
			
		private Factor_ast factor() throws Exception {
			Factor_ast factor_ast = new Factor_ast();	
			if(nextToken == Symbols.IDENT || nextToken == Symbols.NUMBER || nextToken == Symbols.TRUE || nextToken == Symbols.FALSE || nextToken == Symbols.CHARACTER){
				factor_ast.name = lexemeList.get(lexemePointer);
				factor_ast.TokenValue = nextToken; 
				nextToken = getNextToken();
			}
			else if(nextToken == Symbols.LPAREN){
				nextToken = getNextToken();
				factor_ast.expression = expression();
				if((nextToken = getNextToken()) == Symbols.RPAREN){
					nextToken = getNextToken();
				}
				else{
					throw new Exception("error:paren of factor");
				}
			}
			else if(nextToken == Symbols.SC){
			}
			else{
				throw new Exception("error:factor()");
			}
			return factor_ast;
		}
	
		private int getNextToken(){
			lexemePointer++;
			return tokenList.remove(0);
		}	
		
		public void getAst() throws Exception{
			Program_ast program_ast = new Program_ast();
			program_ast = program();
			program_ast.draw();
			SymbolList list = new SymbolList();
			program_ast.typeChecking(list);
			program_ast.program_iloc();
			program_ast.mips();
		}
		
	

}
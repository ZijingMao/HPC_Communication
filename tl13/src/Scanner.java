import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Scanner {
	public InputStream inputStream;
	public BufferedReader buf;
	public int nextToken;
	public int token;
	public int nextChar;
	public int charClass;
	public int pointer;
	public ArrayList<Character> lexeme;
	public ArrayList<String> lexemeList;
	public ArrayList<Integer> tokenList;
	
	public Scanner(InputStream inputStream) throws IOException{
		this.inputStream = inputStream;
		buf = new BufferedReader(new InputStreamReader(inputStream));
		lexeme = new ArrayList<Character>(); 
		lexemeList = new ArrayList<String>();
		tokenList = new ArrayList<Integer>();
		pointer = 0;
	}
		
	public void scan() throws IOException{
		getChar();
		while(nextToken != Symbols.EOF){
			commentFilter();
			int currentToken;
			if((currentToken = lex())!= Symbols.EXITCOMMENT)
					tokenList.add(currentToken);
		}
	}

	private int lex() throws IOException {
		while(isLine(nextChar) || isSpace(nextChar)){
			getNonBlank();
			clearLine();
		}
		lexeme = new ArrayList<Character>(); 
		
		switch(charClass){
		case Symbols.LOWERCASE:{
			addChar();
			getChar();
			while(charClass == Symbols.LOWERCASE || charClass == Symbols.CAPITAL){
				addChar();
				getChar();
			}
			nextToken = getKey(lexeme);
			if(charClass == Symbols.LINE){
				clearLine();
			}
			break;
		}
		case Symbols.CAPITAL:{
			addChar();
			getChar();
			while(charClass == Symbols.CAPITAL || charClass == Symbols.DIGIT){
				addChar();
				getChar();
			}
			nextToken = Symbols.IDENT;
			if(charClass == Symbols.LINE){
				clearLine();
			}
			break;
		}
		case Symbols.DIGIT:{
			addChar();
			getChar();
			while(charClass == Symbols.DIGIT){
				addChar();
				getChar();
			}
			nextToken = Symbols.NUMBER;
			if(charClass == Symbols.LINE){
				clearLine();
			}
			break;
		}
		case Symbols.CHARACTER:{
			getChar();
			if(nextChar != '\\'){
				addChar();
				getChar();
				if(nextChar == '\''){
					nextToken = Symbols.CHARACTER;
				}
			}else{
				getChar();
				if(nextChar == '\''){					
					if(peek() != '\''){
						lexeme.add('\\');
					}else{
						lexeme.add('\'');
						read();
					}
					nextToken = Symbols.CHARACTER;
				}
			}
			getChar();
			break;
		}
		case Symbols.UNKNOWN:{
			lookUp(nextChar);
			getChar();
			if(charClass == Symbols.LINE){
				clearLine();
			}
			break;
		}
		case Symbols.EOF:{
			nextToken = Symbols.EOF;
			lexeme.add('E');
			lexeme.add('O');
			lexeme.add('F');
			break;
		}
		}
		
		if(charClass == Symbols.COMMENTSIGN){
			while(!isLine(read()));
			nextToken = Symbols.EXITCOMMENT;
			getChar();
			return nextToken;
		}
		else if(charClass == Symbols.COMMENT){
			while(read() != '$');
			nextToken = Symbols.EXITCOMMENT;
			getChar();
			return nextToken;
		}
		lexemeList.add(getLexeme());
		System.out.println("Token ID: " + nextToken + 
				", lexeme: " + lexemeList.get(pointer++));
		
		return nextToken;
	}

	private void clearLine() throws IOException {
		while(isLine(nextChar))
			getChar();
	}

	private int lookUp(int c) throws IOException {		
		switch(c){
		case '+':
			addChar();
			nextToken = Symbols.PLUS;
			break;
		case '-':
			addChar();
			nextToken = Symbols.MINUS;
			break;
		case '*':
			addChar();
			nextToken = Symbols.TIMES;
			break;
		case '(':
			addChar();
			nextToken = Symbols.LPAREN;
			break;
		case ')':
			addChar();
			nextToken = Symbols.RPAREN;
			break;
		case '=':
			addChar();
			nextToken = Symbols.EQUAL;
			break;
		case ':':
		addChar();
		if(peek() == '='){
			lexeme.add('=');
			read();
			nextToken = Symbols.ASSIGN;
		}else{
			nextToken = Symbols.EOF;
		}			
		break;
		case ';':
			addChar();
			nextToken = Symbols.SC;
			break;
		case '!':
		addChar();
		if(peek() == '='){
			lexeme.add('=');
			read();
			nextToken = Symbols.UNEQUAL;
		}else{
			nextToken = Symbols.EOF;
		}			
		break;
		case '>':
			addChar();
			if(peek() == '='){
				lexeme.add('=');
				read();
				nextToken = Symbols.MOREEQ;
			}else{
				nextToken = Symbols.MORE;
			}			
			break;
		case '<':
			addChar();
			if(peek() == '='){
				lexeme.add('=');
				read();
				nextToken = Symbols.LESSEQ;
			}else{
				nextToken = Symbols.LESS;
			}			
			break;

		default:
			addChar();
			nextToken = Symbols.EOF;
			break;
		}
		return nextToken;
	}
	
	private void addChar() {
		lexeme.add((char)nextChar);
	}

	private void getNonBlank() throws IOException {
		while (isSpace(nextChar))
			getChar();
	}

	private int getKey(ArrayList<Character> lexe) {
		StringBuilder result = new StringBuilder(lexe.size());
		for(Character c : lexe){
			result.append(c);
		}
		String lex = result.toString();
		
		if(lex.equals("begin")){
			return Symbols.BEGIN;
		}else if(lex.equals("end")){
			return Symbols.END;
		}else if(lex.equals("if")){
			return Symbols.IF;
		}else if(lex.equals("else")){
			return Symbols.ELSE;
		}else if(lex.equals("then")){
			return Symbols.THEN;
		}else if(lex.equals("div")){
			return Symbols.DIVIDE;
		}else if(lex.equals("mod")){
			return Symbols.MOD;
		}else if(lex.equals("while")){
			return Symbols.WHILE;
		}else if(lex.equals("do")){
			return Symbols.DO;
		}else if(lex.equals("program")){
			return Symbols.PROGRAM;
		}else if(lex.equals("var")){
			return Symbols.VAR;
		}else if(lex.equals("as")){
			return Symbols.AS;
		}else if(lex.equals("int")){
			return Symbols.INT;
		}else if(lex.equals("bool")){
			return Symbols.BOOL;
		}else if(lex.equals("char")){
			return Symbols.CHAR;
		}else if(lex.equals("readInt")){
			return Symbols.READINT;
		}else if(lex.equals("writeInt")){
			return Symbols.WRITEINT;
		}else if(lex.equals("false")){
			return Symbols.FALSE;
		}else if(lex.equals("true")){
			return Symbols.TRUE;
		}else{
			return Symbols.EOF;
		}
	}

	private void commentFilter() throws IOException {
		if(charClass == Symbols.COMMENTSIGN){
			System.out.println("This is comment: ");
			while(nextToken!=Symbols.EXITCOMMENT){
				lex();
				if(nextToken == Symbols.EOF){
					break;
				}
			}
			System.out.println("Passed comment.");
		}
		else if(charClass == Symbols.COMMENT){
			System.out.println("This is comment: ");
			while(nextToken!=Symbols.EXITCOMMENT){
				lex();
				if(nextToken == Symbols.EOF){
					break;
				}
			}
			System.out.println("Passed comment.");
		}
	}

	private void getChar() throws IOException {
		if((nextChar = peek()) != -1){
			if(isCapital(nextChar)){
				charClass = Symbols.CAPITAL;
				nextChar = read();
			}else if(isDigit(nextChar)){
				charClass = Symbols.DIGIT;
				nextChar = read();
			}else if(isLowCase(nextChar)){
				charClass = Symbols.LOWERCASE;	
				nextChar = read();
			}else if(nextChar == '%'){		
				charClass = Symbols.COMMENTSIGN;
			}else if(nextChar == '$'){
				charClass = Symbols.COMMENT;
				read();
				nextChar = read();
			}else if(nextChar == '\''){
				charClass = Symbols.CHARACTER;
				nextChar = read();
			}else if(isLine(nextChar)){
				charClass = Symbols.LINE;
				nextChar = read();
			}
			
			else{
				charClass = Symbols.UNKNOWN;
				nextChar = read();
			}
		}else{
			charClass = -1;				
		}		
	}
	
	private int peek() throws IOException {
		int c = -1;
		buf.mark(1);
		c = buf.read();
		buf.reset();
		return c;
	}
	
	private int read() throws IOException{
		int c = -1;
		c = buf.read();
		return c;
	}
	
	private boolean isLine(int nl){
		if(nl == '\n' || nl == '\r'){
			return true;
		}
		return false;
	}
	
	private boolean isSpace(int ns){
		if(ns == ' ' || ns == '\t'){
			return true;
		}
		return false;
	}

	private boolean isDigit(int nn) {
		if(nn >= 48 && nn < 58){
			return true;
		}
		return false;
	}

	private boolean isCapital(int nc) {
		if(nc >= 65 && nc < 91){
			return true;
		}
		return false;
	}
	
	private boolean isLowCase(int ns) {
		if(ns >= 97 && ns < 123){
			return true;
		}
		return false;
	}
	
	private String getLexeme(){
		StringBuilder result = new StringBuilder(lexeme.size());
		for(Character c : lexeme){
			result.append(c);
		}
		String lex = result.toString();
		return lex;
	}

}

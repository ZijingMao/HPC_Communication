import java.util.ArrayList;
import java.util.Iterator;

public class IfStatement_ast extends Statement_ast{
	public String name;
	public Expression_ast expression;
	public ArrayList<Statement_ast> thenStatements;
	public ArrayList<Statement_ast> elseStatements;
	public String color;
	
	public IfStatement_ast(){
		this.name = null;
		this.expression = null;
		this.thenStatements = new ArrayList<Statement_ast>();
		this.elseStatements = new ArrayList<Statement_ast>();	
		this.color = "/pastel13/3";;
	}

	public boolean typeChecking(SymbolList list) throws Exception {
		if(expression != null){
			String type = expression.typeChecking(list).type;
			if(type == null)
				return false;
			if(type.equals("bool")){
				color = "/pastel13/2";
			}else{
				this.color = "/pastel13/1";
				System.out.println("Expression in if statement should be boolean.");
				return false;
			}
		}
		if(thenStatements.size() > 0){
			Iterator<Statement_ast> i = thenStatements.iterator(); 
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.typeChecking(list);
			}
		}
		if(elseStatements.size() > 0){
			Iterator<Statement_ast> i = elseStatements.iterator(); 
			
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.typeChecking(list);
			}
		}
		return true;
	}
	
	public void draw(int p) {
		int q = Parser.labelNumber;
		if(name != null){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+q+" [label=\""+name+"\",fillcolor=\""+color+"\",shape=box]");
			Parser.printStream.println("\tn"+p+" -> n"+q);
		}
		if(expression != null){
			expression.draw(q);
		}
		int r = Parser.labelNumber;
		if(thenStatements.size() > 0){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+r+" [label=\"then stmt\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn"+q+" -> n"+r);
			Iterator<Statement_ast> i = thenStatements.iterator(); 
			
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.draw(r);
			}
		}
		int t = Parser.labelNumber;
		if(elseStatements.size() > 0){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+t+" [label=\"else stmt\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn"+q+" -> n"+t);
			Iterator<Statement_ast> i = elseStatements.iterator(); 
			
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.draw(t);
			}
		}
	}

	public int stmtType() {
		return 1;
	}
}
import java.util.ArrayList;
import java.util.Iterator;

public class WhileStatement_ast extends Statement_ast{
	public String name;
	public Expression_ast expression;
	public ArrayList<Statement_ast> whileStatements;
	public String color;
	
	public WhileStatement_ast(){
		this.name = null;
		this.expression = null;
		this.whileStatements = new ArrayList<Statement_ast>();
		this.color = "/x11/white";
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
				System.out.println("Expression in while statement should be boolean.");
				return false;
			}
		}
		if(whileStatements.size() > 0){
			Iterator<Statement_ast> i = whileStatements.iterator(); 
			
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
		if(whileStatements.size() > 0){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+r+" [label=\"stmt list\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn"+q+" -> n"+r);
			Iterator<Statement_ast> i = whileStatements.iterator(); 
			
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.draw(r);
			}
		}	
	}

	public int stmtType() {
		return 2;
	}
}
public class WriteInt_ast extends Statement_ast{
	public String name;
	public Expression_ast expression;
	public String color;
	
	public WriteInt_ast(){
		this.name = null;
		this.expression = null;
		this.color = "/x11/white";
	}
	
	public boolean typeChecking(SymbolList list) throws Exception {
		if(expression != null){
			if(expression.typeChecking(list).type.equals("int")){
				color = "/pastel13/3";
				return true;
			}else{
				this.color = "/pastel13/1";
				System.out.println("writeInt's expression must evaluate to an integer");
			}
		}
		return false;
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
	}

	public int stmtType() {
		return 3;
	}


}
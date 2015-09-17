public class Assignment_ast extends Statement_ast{
	public String sign;
	public String left;
	public Expression_ast right;
	public String color;
	
	public Assignment_ast(){
		sign = null;
		left = null;
		right = null;
		color = "/x11/white";
	}
	
	public boolean typeChecking(SymbolList list) throws Exception {
		if(!list.list.containsKey(left)){
			color = "/pastel13/1";
			System.out.println("All variables must be declared with a particular type.");
			return false;
		}
		Type_ast type_ast = list.list.get(left);
		if(type_ast.type.equals("int")){
			color = "/pastel13/3";
		}else if(type_ast.type.equals("bool")){
			color = "/pastel13/2";
		}	
		else if(type_ast.type.equals("char")){
			color = "/pastel13/3";
		}
		if(sign.equals(":=")){
			if(!type_ast.type.equals(right.typeChecking(list).type)){
				color = "/pastel13/1";
				System.out.println("The right-hand side must be an expression of the variable's type.");
				return false;
			}
			return true;
		}
		else{
			if(!type_ast.type.equals("int")){
				color = "/pastel13/1";
				System.out.println("ReadInt must be an integer.");
				return false;
			}
			return true;
		}
	}

	public void draw(int p) {
		int q = Parser.labelNumber;
		if(sign != null){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+q+" [label=\""+sign+"\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn"+p+" -> n"+q);
		}
		int r = Parser.labelNumber;
		if(left != null){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+r+" [label=\""+left+"\",fillcolor=\""+color+"\",shape=box]");
			Parser.printStream.println("\tn"+q+" -> n"+r);
		}
		if(right != null){
			right.draw(q);
		}	
	}

	public int stmtType() {
		return 0;
	}
	
}
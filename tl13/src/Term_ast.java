public class Term_ast {
	public Factor_ast right;
	public Factor_ast left;
	public String op;
	public String color;
	
	public Term_ast(){
		left = null;
		right = null;
		op = null;
		color = null;//"/x11/white";
	}

	public Type_ast typeChecking(SymbolList list) throws Exception {
		if(op != null){
			if(left.typeChecking(list).type.equals("int") || right.typeChecking(list).type.equals("int")){
				color = "/pastel13/3";
				return new Type_ast("int");
			}else{
				this.color = "/pastel13/1";
				System.out.println("The operands of all OP2, OP3, and OP4 operators must be integers.");
			}
		}
		else{
			if(left != null){
				return left.typeChecking(list);
			}
			if(right != null){
				return right.typeChecking(list);
			}
		}
		return null;		
	}
	
	public void draw(int p) {
		int q = Parser.labelNumber;
		if(op != null){
			Parser.labelNumber++;
			Parser.printStream.println("\tn"+q+" [label=\""+op+"\",fillcolor=\""+color+"\",shape=box]");
			Parser.printStream.println("\tn"+p+" -> n"+q);
			if(left != null){
				left.draw(q);
			}
			if(right != null){
				right.draw(q);
			}
		}
		else{
			if(left != null){
				left.draw(p);
			}
			if(right != null){
				right.draw(p);
			}
		}
	}

}
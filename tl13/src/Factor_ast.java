public class Factor_ast {
	public String name;
	public int TokenValue;
	public Expression_ast expression;
	public String color;
	
	public Factor_ast(){
		name = null;
		TokenValue = 0;
		expression = null;
		color = "/pastel13/3";
	}

	public Type_ast typeChecking(SymbolList list) throws Exception {
		if(name != null){
			if(TokenValue == Symbols.NUMBER && Integer.parseInt(name) >= 0 && Integer.parseInt(name) < Integer.MAX_VALUE){
				color = "/pastel13/3";
				return new Type_ast("int");
			}
			else if(TokenValue == Symbols.TRUE || TokenValue == Symbols.FALSE){
				color = "/pastel13/2";
				return new Type_ast("bool");
			}
			else if(TokenValue == Symbols.CHARACTER){
				color = "/pastel13/3";
				return new Type_ast("char");
			}
			else if(TokenValue == Symbols.IDENT){
				if(list.list.get(name).type.equals("int")){
					color = "/pastel13/3";
				}else{
					color = "/pastel13/2";
				}
				return list.list.get(name);
			}
			else{
				color = "/pastel13/1";
				System.out.println("Integer out of range.");
				return new Type_ast();
			}
		}
		else if(expression != null){
			return expression.typeChecking(list);
		}
		else{
			return null;
		}		
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
}
public class Declarations_ast {
	public String name;
	public Type_ast type;
	public String color;
	
	public Declarations_ast(){
		this.name = null;
		this.type = null;
		this.color = "/x11/white";
	}
	
	public Declarations_ast(String name, Type_ast type){
		this.name = name;
		this.type = type;
	}

	public boolean typeChecking(SymbolList list) throws Exception {
		if(name != null && type == null){
			color = "/pastel13/1";
			System.out.println("All variable must be declared with a particular type"); 
			return false;
		}
		
		if(!list.list.containsKey(name)){
			list.list.put(name, type);
			return true;
		}
		else{
			color = "/pastel13/1";
			System.out.println("Each variable may only be declared once"); 
			return false;
		}
	}
	
	public void draw(int p) {
		int q = Parser.labelNumber++;
		if(type.type.equals("int")){
			this.color = "/pastel13/3";
		}else{
			this.color = "/pastel13/2";
		}
		Parser.printStream.println("\tn"+ q +" [label=\""+"declaration: \'" + name + "\'"+"\",fillcolor=\""+color+"\",shape=box]");
		Parser.printStream.println("\tn"+p+" -> n"+q);
		int r = Parser.labelNumber++;
		Parser.printStream.println("\tn"+ r +" [label=\""+ type.type +"\",fillcolor=\"/x11/white\",shape=box]");
		Parser.printStream.println("\tn"+ q +" -> n"+ r);
	}


}
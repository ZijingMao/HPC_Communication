import java.util.ArrayList;
import java.util.Iterator;

public class Program_ast {
	public ArrayList<Declarations_ast> declarations;
	public ArrayList<Statement_ast> statements;
	public static int regNumber = 0;
	public Block block;
	public int whileid;
	public int blockNumber = 0;
	public ArrayList<Mips> MIPS = new ArrayList<Mips>();
	public ArrayList<String> reg = new ArrayList<String>();
	public static int offset = 0;
	public static int l=0;
	
	public Program_ast(){
		declarations = new ArrayList<Declarations_ast>();
		statements = new ArrayList<Statement_ast>();
		block = new Block();
	}
	
	public void typeChecking(SymbolList list) throws Exception{
		if(declarations.size() > 0){
			Iterator<Declarations_ast> i = declarations.iterator(); 
			while(i.hasNext()){
				Declarations_ast declaration_ast = i.next();
				if(!declaration_ast.typeChecking(list)){
					System.out.println("declaration error.");
				}
			}		
		}	
		if(statements.size() > 0){
			Iterator<Statement_ast> i = statements.iterator(); 
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				if(!statement_ast.typeChecking(list)){
					System.out.println("statements sequence error.");
				}
			}
		}
		System.out.println("typeChecking success");
	}
	
	public void draw(){
		Parser.printStream.println("digraph parseTree {");
		Parser.printStream.println("\tordering=out;");
		Parser.printStream.println("\tnode [shape = box, style = filled];");
		
		Parser.printStream.println("\tn1 [label=\"program\",fillcolor=\"/x11/white\",shape=box]");
		int p = Parser.labelNumber++;
		if(declarations.size() > 0){
			Parser.printStream.println("\tn"+p+" [label=\"decl list\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn1 -> n"+p);
			Iterator<Declarations_ast> i = declarations.iterator(); 
			while(i.hasNext()){
				Declarations_ast declarations_ast = i.next();
				declarations_ast.draw(p);
			}			
		}
		int q = Parser.labelNumber++;
		if(statements.size() > 0){
			Parser.printStream.println("\tn"+q+" [label=\"stmt list\",fillcolor=\"/x11/white\",shape=box]");
			Parser.printStream.println("\tn1 -> n"+q);
			Iterator<Statement_ast> i = statements.iterator(); 
			while(i.hasNext()){
				Statement_ast statement_ast = i.next();
				statement_ast.draw(q);
			}
		}
		Parser.printStream.println("}");
	}
	
	public void program_iloc() {
		Instruction instruction = new Instruction("","B"+blockNumber,"");
        block.instructions.add(instruction);
        blockNumber++;
		declaration_iloc();
		try {
			statements_iloc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int num = 1;
	    String s="";
	    Parser.cfgStream.println("digraph parseTree {");
	    Parser.cfgStream.println("node [shape = none]; edge [tailport = s]; entry subgraph cluster { color=\"/x11/white\""); 
	    Parser.cfgStream.println("B0 [label=<<table border=\"0\">");
		for(Instruction i : block.instructions){
			if(i.target.equals("")){
				if(i.opcode.equals("")){
					Parser.cfgStream.println("<tr><td border=\"1\" colspan=\"3\">"+i.source+"</td></tr>");
					s = i.source;		
				}else{
					Parser.cfgStream.println("<tr><td align=\"left\">"+i.opcode+"</td><td align=\"left\">"+i.source+"</td></tr>");
					if(i.opcode.equals("jumpI")||i.opcode.equals("cbr")){
						String[] source = i.source.split(",");
						Parser.cfgStream.println("</table>>,fillcolor=\"/x11/white\",shape=box]");
						if(i.opcode.equals("jumpI")){
							Parser.cfgStream.println(s+"->"+i.source);
						}
						else{
							Parser.cfgStream.println(s+"->"+ source[0]);
							if(!source[1].equals("exit")){
								Parser.cfgStream.println(s+"->"+ source[1]);
							}
						}

						if((!(statements.get(statements.size()-1).stmtType() == 2) || !(block.instructions.indexOf(i) == block.instructions.size()-1))
							&& (!(statements.get(statements.size()-1).stmtType() == 1) || !(block.instructions.indexOf(i) == block.instructions.size()-1))){
							Parser.cfgStream.println("B"+num+" [label=<<table border=\"0\">");
							num++;
						}
					}
					
				}
			}else{
				Parser.cfgStream.println("<tr><td align=\"left\">"+i.opcode+"</td><td align=\"left\">"+i.source+"</td><td align=\"left\">=&gt; "+i.target+"</td></tr>");
			}
		}
		if(!(statements.get(statements.size()-1).stmtType() == 2)){
			if(!(statements.get(statements.size()-1).stmtType() == 1)){
				Parser.cfgStream.println("</table>>,fillcolor=\"/x11/white\",shape=box]");
				Parser.cfgStream.println("} entry -> B0 B"+(blockNumber-1)+" -> exit }");
			}else{
				Parser.cfgStream.println("} entry -> B0}");
			}
		}else{
			Parser.cfgStream.println("} entry -> B0 B"+whileid+" -> exit }");
		}
	}
	
	public void declaration_iloc(){
		if(declarations.size() > 0){
			for(Declarations_ast d : declarations){
				if(d.type.type.equals("int")){
					Instruction instruction = new Instruction("loadI","0","r_"+d.name);
			        block.instructions.add(instruction);
				}
				else if(d.type.type.equals("bool")){
					Instruction instruction = new Instruction("loadI","false","r_"+d.name);
			        block.instructions.add(instruction);
				}else{
					Instruction instruction = new Instruction("loadI","0","r_"+d.name);
			        block.instructions.add(instruction);
				}
			}		
		}
	}
	
	public void statements_iloc() throws Exception{
		if(statements.size() > 0){
			for(Statement_ast s : statements){
				switch(s.stmtType()){
				case 0:
					assignment_iloc((Assignment_ast) s);
				    break;
				case 1:
					ifStatement_iloc((IfStatement_ast) s);
					break;
				case 2:
					whileStatement_iloc((WhileStatement_ast) s);
					break;
				case 3:
					writeInt_iloc((WriteInt_ast) s);
					break;
				default: 
					throw new Exception("statement error");
										
				}
			}
		}
	}

	private void writeInt_iloc(WriteInt_ast s) {
		expression_iloc(s.expression);
		Instruction instruction;
		if(s.expression == null){
			instruction = new Instruction("writeInt","r_"+s.name,"");
			block.instructions.add(instruction);
		}
		else{
			instruction = new Instruction("writeInt", "r"+(regNumber-1),"");
			//regNumber++;	
			block.instructions.add(instruction);
		}
	}

	private void expression_iloc(Expression_ast expression) {
		if(expression.op != null){
			simpleExpression_iloc(expression.left);
			int a = regNumber-1;
			simpleExpression_iloc(expression.right);
			int b = regNumber-1;
			if(expression.op.equals("=")){
				Instruction instruction = new Instruction("cmp_EQ","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(expression.op.equals("!=")){
				Instruction instruction = new Instruction("cmp_NE","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(expression.op.equals("<")){
				Instruction instruction = new Instruction("cmp_LT","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(expression.op.equals(">")){
				Instruction instruction = new Instruction("cmp_GT","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(expression.op.equals("<=")){
				Instruction instruction = new Instruction("cmp_LE","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(expression.op.equals(">=")){
				Instruction instruction = new Instruction("cmp_GE","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
		}
		else{
			simpleExpression_iloc(expression.left);
		}
	}

	private void simpleExpression_iloc(SimpleExpression_ast simpleExpression) {
		if(simpleExpression.op != null){
			term_iloc(simpleExpression.left);
			int a = regNumber-1;
			term_iloc(simpleExpression.right);
			int b = regNumber-1;
			if(simpleExpression.op.equals("+")){
				Instruction instruction = new Instruction("add","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(simpleExpression.op.equals("-")){
				Instruction instruction = new Instruction("sub","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
		}
		else{
			term_iloc(simpleExpression.left);
		}
		
	}

	private void term_iloc(Term_ast term) {
		if(term.op != null){
			factor_iloc(term.left);
			int a = regNumber-1;
			factor_iloc(term.right);
			int b = regNumber-1;
			if(term.op.equals("*")){
				Instruction instruction = new Instruction("mult","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(term.op.equals("div")){
				Instruction instruction = new Instruction("div","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
			else if(term.op.equals("mod")){
				Instruction instruction = new Instruction("mod","r"+a +","+"r"+b, "r"+regNumber++);
				block.instructions.add(instruction);
			}
		}
		else{
			factor_iloc(term.left);
		}
	}

	private void factor_iloc(Factor_ast factor) {
		if(factor.name != null){
			if(factor.TokenValue == Symbols.NUMBER){
				Instruction instruction = new Instruction("loadI",factor.name,"r"+regNumber);
				regNumber++;
				block.instructions.add(instruction);
			}
			else if(factor.TokenValue == Symbols.TRUE || factor.TokenValue == Symbols.FALSE){
				Instruction instruction = new Instruction("loadI",factor.name,"r"+regNumber);
				regNumber++;
				block.instructions.add(instruction);
			}
			else if(factor.TokenValue == Symbols.IDENT){
				Instruction instruction = new Instruction("i2i","r_"+factor.name,"r"+regNumber);
				regNumber++;
				block.instructions.add(instruction);
			}
			else if(factor.TokenValue == Symbols.CHARACTER){
				int val = (int)factor.name.charAt(0);
				Instruction instruction = new Instruction("loadI",Integer.toString(val),"r"+regNumber);
				regNumber++;
				block.instructions.add(instruction);
			}
		}
		else if(factor.expression != null){
			expression_iloc(factor.expression);
		}
		
	}

	private void whileStatement_iloc(WhileStatement_ast s) throws Exception {
		Instruction instructionStart = new Instruction("","B"+ blockNumber,"");
		int a = blockNumber;
		blockNumber++;
		Instruction instructionJumpWhile = new Instruction("jumpI","B"+a,"");
		block.instructions.add(instructionJumpWhile);
		block.instructions.add(instructionStart);
		expression_iloc(s.expression);
		
		Instruction instruction = new Instruction("","B"+ blockNumber,"");
		int b = blockNumber;
		blockNumber++;
		block.instructions.add(instruction);
		if(s.whileStatements.size() > 0){
			for(Statement_ast whileStmt : s.whileStatements){
				switch(whileStmt.stmtType()){
				case 0:
					assignment_iloc((Assignment_ast) whileStmt);
				    break;
				case 1:
					ifStatement_iloc((IfStatement_ast) whileStmt);
					break;
				case 2:
					whileStatement_iloc((WhileStatement_ast) whileStmt);
					break;
				case 3:
					writeInt_iloc((WriteInt_ast) whileStmt);
					break;
				default: 
					throw new Exception("statement error");									
				}
			}
		}
		
		Instruction instructionDo = new Instruction("jumpI","B"+a,"");
		block.instructions.add(instructionDo);
		 
		if(statements.indexOf(s) == statements.size()-1){
			whileid = a;
			for(Instruction i : block.instructions){
				if(i.source.equals("B"+b)){
					Instruction instructionWhile = new Instruction("cbr","r"+regNumber,"B"+b+","+"exit");
					regNumber++;
					block.instructions.add(block.instructions.indexOf(i),instructionWhile);
					break;
				}
			}
		}else{
			Instruction instructionContinue = new Instruction("","B"+blockNumber,"");
			int c = blockNumber;
			blockNumber++;
			block.instructions.add(instructionContinue);
			
			for(Instruction i : block.instructions){
				if(i.source.equals("B"+b)){
					Instruction instructionWhile = new Instruction("cbr","r"+regNumber,"B"+b+","+"B"+c);
					regNumber++;
					block.instructions.add(block.instructions.indexOf(i),instructionWhile);
					break;
				}
			}
		}
	}

	private void ifStatement_iloc(IfStatement_ast s) throws Exception {
		expression_iloc(s.expression);
		Instruction instruction = new Instruction("","B"+ blockNumber,"");
		int a = blockNumber;
		blockNumber++;
		block.instructions.add(instruction);
		if(s.thenStatements.size() > 0){
			for(Statement_ast thenStmt : s.thenStatements){
				switch(thenStmt.stmtType()){
				case 0:
					assignment_iloc((Assignment_ast) thenStmt);
				    break;
				case 1:
					ifStatement_iloc((IfStatement_ast) thenStmt);
					break;
				case 2:
					whileStatement_iloc((WhileStatement_ast) thenStmt);
					break;
				case 3:
					writeInt_iloc((WriteInt_ast) thenStmt);
					break;
				default: 
					throw new Exception("statement error");									
				}
			}
		}
	
		Instruction instructionElse = new Instruction("","B"+ blockNumber,"");
		int b = blockNumber;
		blockNumber++;
		block.instructions.add(instructionElse);
		if(s.elseStatements.size() > 0){
			for(Statement_ast elseStmt : s.elseStatements){
				switch(elseStmt.stmtType()){
				case 0:
					assignment_iloc((Assignment_ast) elseStmt);
				    break;
				case 1:
					ifStatement_iloc((IfStatement_ast) elseStmt);
					break;
				case 2:
					whileStatement_iloc((WhileStatement_ast) elseStmt);
					break;
				case 3:
					writeInt_iloc((WriteInt_ast) elseStmt);
					break;
				default: 
					throw new Exception("statement error");									
				}
			}
		}
		
		int c = 0;
		if(!(statements.indexOf(s) == statements.size()-1)){
			Instruction instructionContinue = new Instruction("","B"+blockNumber,"");
			c = blockNumber;
			blockNumber++;
			block.instructions.add(instructionContinue);
		}
		
		for(Instruction i : block.instructions){
			if(i.opcode.equals("")&&i.source.equals("B"+a)){
				Instruction instructionIf = new Instruction("cbr","r"+regNumber,"B"+a+","+"B"+b);
				regNumber++;
				block.instructions.add(block.instructions.indexOf(i),instructionIf);
				break;
			}
		}

		if(statements.indexOf(s) == statements.size()-1){
			for(Instruction i : block.instructions){
				if(i.opcode.equals("")&&i.source.equals("B"+b)){
					Instruction instructionJumpThen = new Instruction("jumpI","exit","");
					block.instructions.add(block.instructions.indexOf(i),instructionJumpThen);
					break;
				}
			}
			Instruction instructionJumpElse = new Instruction("jumpI","exit","");
			block.instructions.add(instructionJumpElse);
		}else{
			for(Instruction i : block.instructions){
				if(i.opcode.equals("")&&i.source.equals("B"+b)){
					Instruction instructionJumpThen = new Instruction("jumpI","B"+c,"");
					block.instructions.add(block.instructions.indexOf(i),instructionJumpThen);
					break;
				}
			}
			
			for(Instruction i : block.instructions){
				if(i.opcode.equals("")&&i.source.equals("B"+c)){
					Instruction instructionJumpElse = new Instruction("jumpI","B"+c,"");
					block.instructions.add(block.instructions.indexOf(i),instructionJumpElse);
					break;
				}
			}	
		}
	}

	private void assignment_iloc(Assignment_ast s) {
		if(s.right != null){
			expression_iloc(s.right);
			Instruction instruction = new Instruction("i2i","r"+(regNumber-1),"r_"+s.left);
			block.instructions.add(instruction);
		}
		else{
			Instruction instruction = new Instruction("readInt","r_"+s.left,"");
			block.instructions.add(instruction);
		}
		
	}	
	
	
	
	public void mips(){
		Mips mips1s = new Mips("",".data");
		MIPS.add(mips1s);
		Mips mips2s = new Mips("newline:",".asciiz \"\\n\"");
		MIPS.add(mips2s);
		Mips mips3s = new Mips("",".text");
		MIPS.add(mips3s);
		Mips mips4s = new Mips("",".globl main");
		MIPS.add(mips4s);
		Mips mips5s = new Mips("main:","");
		MIPS.add(mips5s);
		Mips mips6s = new Mips("li","$fp,","0x7ffffffc");
		MIPS.add(mips6s);
		
		for(Instruction i : block.instructions){
			if(i.opcode.equals("loadI")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
			    mips = new Mips("# loadI",i.source,i.target);
				MIPS.add(mips);				
				
				Mips mips1 = new Mips("li","$t0,", i.source);
				MIPS.add(mips1);
				Mips mips2 = new Mips("sw","$t0,", offset+"($fp)");
				reg.add(i.target);
				offset=offset-4;
				MIPS.add(mips2);
			}
			
			
			else if(i.opcode.equals("writeInt")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# writeInt",i.source,"");
				MIPS.add(mips);				
				
				Mips mips1 = new Mips("li","$v0,","1");
				MIPS.add(mips1);	
				
				boolean flag = false;
				int n = 0;
				for(String s : reg){
					if(s.equals(i.source)){
						flag = true;
						n = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				if(flag == true){
					Mips mips2 = new Mips("lw","$t0,", n +"($fp)");
					MIPS.add(mips2);
				}else{
					Mips mips2 = new Mips("lw","$t0,", offset +"($fp)");
					reg.add(i.source);
					offset = offset-4;
					MIPS.add(mips2);
				}
								
				Mips mips3 = new Mips("add","$a0,","$t0, $zero");
				MIPS.add(mips3);	
				Mips mips4 = new Mips("syscall","","");
				MIPS.add(mips4);
				Mips mips5 = new Mips("li","$v0,","4");
				MIPS.add(mips5);	
				Mips mips6 = new Mips("la","$a0,","newline");
				MIPS.add(mips6);	
				Mips mips7 = new Mips("syscall","","");
				MIPS.add(mips7);	
			}
			
			else if(i.opcode.equals("readInt")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# readInt",i.source,"");
				MIPS.add(mips);	
				Mips mips1 = new Mips("li","$v0,","5");
				MIPS.add(mips1);
				Mips mips2 = new Mips("syscall","","");
				MIPS.add(mips2);
				Mips mips3 = new Mips("add","$t0,","$v0, $zero");
				MIPS.add(mips3);
				
				int n = 0;
				boolean flag = false;
				for(String s : reg){
					if(s.equals(i.source)){
						flag = true;
						n = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				if(flag == true){
					Mips mips4 = new Mips("sw","$t0,", n +"($fp)");
					MIPS.add(mips4);
				}else{
					Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
					reg.add(i.source);
					offset = offset-4;
					MIPS.add(mips4);
				}
				
			}
			
			else if(i.opcode.equals("add")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# add",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("addu","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("sub")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# sub",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("subu","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("mult")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# mult",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("mul","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("div")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# div",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("divu","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("mod")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# mod",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("rem","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("cmp_EQ")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_EQ",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("seq","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("cmp_NE")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_NE",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("sne","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("cmp_LT")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_LT",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("slt","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
			
			else if(i.opcode.equals("cmp_GT")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_GT",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("sgt","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
		
			else if(i.opcode.equals("cmp_LE")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_LE",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("sle","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
		
		
			else if(i.opcode.equals("cmp_GE")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cmp_GE",i.source,i.target);
				MIPS.add(mips);
				
				String[] source = i.source.split(",");
				int n1=0;
				int n2=0;
				for(String s : reg){
					if(s.equals(source[0])){
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				for(String s : reg){
					if(s.equals(source[1])){
						n2 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				Mips mips1 = new Mips("lw","$t0,",n1 +"($fp)");
				MIPS.add(mips1);
				Mips mips2 = new Mips("lw","$t1,",n2 +"($fp)");
				MIPS.add(mips2);
				Mips mips3 = new Mips("sge","$t0,","$t0, $t1");
				MIPS.add(mips3);
				
                Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
			    reg.add(i.target);
				offset = offset-4;
				MIPS.add(mips4);		
			}
		
			else if(i.opcode.equals("i2i")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# i2i",i.source,i.target);
				MIPS.add(mips);		
				
				int n=0;
				for(String s : reg){
					if(s.equals(i.source)){
						n = reg.indexOf(s)*(-4);
						break;
					} 
				}
				Mips mips1 = new Mips("lw","$t0,",n +"($fp)");
				MIPS.add(mips1);
				
				Mips mips2 = new Mips("add","$t0,","$t0, $zero");
				MIPS.add(mips2);
				
				int n1 = 0;
				boolean flag = false;
				for(String s : reg){
					if(s.equals(i.target)){
						flag = true;
						n1 = reg.indexOf(s)*(-4);
						break;
					} 
				}
				
				if(flag == true){
					Mips mips4 = new Mips("sw","$t0,", n1 +"($fp)");
					MIPS.add(mips4);
				}else{
					Mips mips4 = new Mips("sw","$t0,", offset +"($fp)");
					reg.add(i.target);
					offset = offset-4;
					MIPS.add(mips4);
				}
			}
			
			else if(i.opcode.equals("jumpI")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				if(i.source.equals("exit")){
					mips = new Mips("# exit","","");
					MIPS.add(mips);	
					Mips mips1 = new Mips("li","$v0,","10");
					MIPS.add(mips1);
					Mips mips2 = new Mips("syscall","","");
					MIPS.add(mips2);
				}
				else{
					mips = new Mips("# jumpI",i.source,"");
					MIPS.add(mips);	
					Mips mips1 = new Mips("j",i.source,"");
					MIPS.add(mips1);	
				}
			}
			
			else if(i.opcode.equals("")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips(i.source+":","");
				MIPS.add(mips);
				
			}
			
			else if(i.opcode.equals("cbr")){
				Mips mips = new Mips("","","");
				MIPS.add(mips);	
				mips = new Mips("# cbr",i.source,i.target);
				MIPS.add(mips);
				reg.add(i.source);
				offset = offset-4;
				int n=0;
				for(String s : reg){
					if(s.equals(i.source)){
						n = reg.indexOf(s)*(-4);
						break;
					} 
				}
				String[] target = i.target.split(",");
				Mips mips2 = new Mips("bne","$t0, $zero", target[0]);
				MIPS.add(mips2);
				Mips mips1 = new Mips("sw","$t0,",n +"($fp)");
				MIPS.add(mips1);
				Mips mips3 = new Mips("L"+l+++":","");
				MIPS.add(mips3);
				
				if(target[1].equals("exit")){
					Mips mips4 = new Mips("# exit","","");
					MIPS.add(mips4);	
					Mips mips5 = new Mips("li","$v0,","10");
					MIPS.add(mips5);
					Mips mips6 = new Mips("syscall","","");
					MIPS.add(mips6);
				}
				else{
					Mips mips4 = new Mips("j",target[1],"");
					MIPS.add(mips4);
				}
				
			}
		}
		
		Mips mips0 = new Mips("","","");
		MIPS.add(mips0);	
		mips0 = new Mips("# exit","","");
		MIPS.add(mips0);	
		Mips mips1 = new Mips("li","$v0,","10");
		MIPS.add(mips1);
		Mips mips2 = new Mips("syscall","","");
		MIPS.add(mips2);
		for(Mips mips:MIPS){
			Parser.mipsStream.println(mips.toString());
		}
		
	}
	
	
}




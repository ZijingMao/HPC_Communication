public class Instruction {
	String opcode;
	String source;
	String target;

	public Instruction(String opcode,String source,String target){
		this.opcode = opcode;
		this.source = source;
		this.target = target;
	}
}


public class Mips {
	String op;
	String rd;
	String rs;
	String title;
	String postfix;
	
	
	public Mips(String op,String rd,String rs){
		this.op = op;
		this.rd = rd;
		this.rs = rs;
	}
	
	public Mips(String title,String postfix){
		this.title = title;
		this.postfix = postfix;

	}
	
	public String toString(){
		String mips;
		if((title!=null)&&(postfix==null)){
			mips = title;
		}
		else if((title!=null)&&(postfix!=null)){
			mips = title +"\t"+ postfix;
		}
		else if((title==null)&&(postfix!=null)){
			mips = "\t"+ postfix;
		}
		else{
			mips = "\t" + op+ " " + rd +" " + rs;
		}
		return mips;
	}

}

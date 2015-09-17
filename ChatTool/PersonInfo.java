package HW4;

import java.io.Serializable;

public class PersonInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public String clientIP;
	public int clientPort;
	public int x, y;
	public String age;
	public String name;
	public String sex;
	public int id;
	
	//public ArrayList<ClientInfo> clientList = new ArrayList<ClientInfo>(); 
	
	public void addInfo(int x, int y, String age, String name, String sex){
		this.x = x;
		this.y = y;
		this.age = age;
		this.name = name;
		this.sex = sex;
	}
	
	public PersonInfo(String ip){
		this.clientIP = ip;
	}

	public PersonInfo() {
		
	}
}

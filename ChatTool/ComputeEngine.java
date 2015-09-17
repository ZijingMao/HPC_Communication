package HW4;

import java.rmi.*;
import java.util.ArrayList;

public class ComputeEngine implements DataPassing {

	public String str;
	public ClientInfo ci;
	
	public ArrayList<ClientInfo> cList = new ArrayList<ClientInfo>();
	
	public ComputeEngine() {
        super();
    }
	
	public ArrayList<PersonInfo> viewClient(PersonInfo personInfo, int range) {
		ArrayList<PersonInfo> piList = new ArrayList<PersonInfo>();
		for(ClientInfo c : cList){
			if((Math.pow((c.myself.x - personInfo.x), 2) +
					Math.pow((c.myself.y - personInfo.y), 2)) < 
					Math.pow(range, 2)){
				piList.add(c.myself);
			}
		}
		return piList;
	}

	public synchronized void register(PersonInfo p){
			p.id = RMIServer.id++;
			ci.myself = p;
			cList.add(ci);
	}

	public String getMsg() throws RemoteException {
		return null;
	}

	public ClientInfo search(String string) {
		for(ClientInfo c : cList){
			String name = c.myself.name;
			String tmp = name + "-" + c.myself.clientIP;
			if(tmp.equals(string)){
				return c;
			}
		}
		return null;
	}

	public void upPosition(PersonInfo pi) {
		String myInfo = pi.name + "-" + pi.clientIP;
		int i = 0;
		for(ClientInfo c : cList){
			i++;
			String name = c.myself.name;
			String tmp = name + "-" + c.myself.clientIP;
			if(tmp.equals(myInfo)){
				cList.get(i).myself.x = pi.x;
				cList.get(i).myself.y = pi.y;
			}
		}
	}

	public void quit(PersonInfo pi) {
		String myInfo = pi.name + "-" + pi.clientIP;
		int i = 0;
		for(ClientInfo c : cList){
			i++;
			String name = c.myself.name;
			String tmp = name + "-" + c.myself.clientIP;
			if(tmp.equals(myInfo)){
				cList.remove(i);
			}
		}
	}

}

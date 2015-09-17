package HW4;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface {

	private static final long serialVersionUID = 1L;
	
	public PersonInfo myself = new PersonInfo();

	protected ClientInfo() throws RemoteException {
		super();
	}

	public void printMessage(String tmp) {
		System.out.println(tmp);
	}
	
}

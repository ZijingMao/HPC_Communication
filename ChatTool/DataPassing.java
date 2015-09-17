package HW4;

import java.rmi.*;
import java.util.ArrayList;

public interface DataPassing extends Remote {

	public ClientInfo search(String string);

	public void register(PersonInfo pi);

	public ArrayList<PersonInfo> viewClient(PersonInfo pi, int range);

	public void upPosition(PersonInfo pi);

	public void quit(PersonInfo pi);
	
}

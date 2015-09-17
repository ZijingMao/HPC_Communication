package HW4;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
	
	public static int id = 0;

	public static void main(String[] args){
		
        try {
        	LocateRegistry.createRegistry(1099);
            DataPassing msg = new ComputeEngine();
			Naming.rebind("DataPassing", msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("RMI starts.");   
		
	}
}

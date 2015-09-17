package HW4;

import java.rmi.Remote;

public interface ClientInterface extends Remote {

	void printMessage(String tmp);

}

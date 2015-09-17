package HW4;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RMIClient {

	public String serverIP, clientIP, clientID;
	public int serverPort, clientPort;
	
	public int x, y;
	public String age;
	public String name;
	public String sex;
	public JTextField outgoing;
	public JTextArea incoming;
	
	public ArrayList<PersonInfo> personListInRange;
	public String message;
	
	public static void main(String args[]) {
		if(args.length != 13){
			System.out.println("Incorrect arg number.");
			return;
		}
		
        (new RMIClient()).start(args);

	}
	
	public void start(String[] args){
		setInfo(args);
		buildGUI();			
		startReadThread();
	}
	 
	
	private void startReadThread() {
		Thread t = new Thread(new ReadRunner());
		t.start();
	}
	
	public class ReadRunner implements Runnable {

		public void run() {
			if (System.getSecurityManager() == null) {
			     System.setSecurityManager(new SecurityManager());
			}
		     
			try {
			    String name = "rmi://" + serverIP + ":1099/messageServer";
			    DataPassing comp = (DataPassing) Naming.lookup(name);
			    
			    PersonInfo pi = new PersonInfo();
			    ClientInterface neighbour = new ClientInfo();
				
				String[] command = message.split(" ");
				int size = command.length;
				if(command[0].equals("send")){
					neighbour = comp.search(command[1]);
					String msg = "";
					for(int i = 2; i < command.length; i++){
						msg+=(command[i]+" ");
					}
					if(neighbour != null){
						String tmp = "User "+pi.name+" talk to you: "+msg;
						neighbour.printMessage(tmp);
					}else{
						System.out.println("No such user found");
					}
				}
				
				switch(size){
				case 5:{
					int x = Integer.parseInt(command[0]);
					int y = Integer.parseInt(command[1]);
					String age = command[2];
					String myname = command[3];
					String sex = command[4];
					pi.addInfo(x, y, age, myname, sex);
					comp.register(pi);
					System.out.println("Server catch a client: " + pi.clientIP +
							" " + pi.clientPort + " " + pi.id);
					break;
				}
				case 3:{
					if(command[0].equals("go")){
						pi.x += Integer.parseInt(command[1]);
						pi.y += Integer.parseInt(command[2]);
						comp.upPosition(pi);
					}
					break;
				}
				case 2:{
					if(command[0].equals("list")){
						int range = Integer.parseInt(command[1]);
						ArrayList<PersonInfo> personListInRange = comp.viewClient(pi, range); 
						if(personListInRange.size() == 0){
							System.out.println("no other client in range.");
						}else{
							System.out.println("command syntax error");
						}
						for(PersonInfo p : personListInRange){
							System.out.println(p.name+","+p.clientIP);
						}
					}else if(command[0].equals("get") && command[1].equals("location")){
						System.out.println(pi.x+", "+pi.y);
					}else{
						System.out.println("command syntax error.");
					}
					break;
				}
				case 1:{
					if(command[0].equals("quit")){
						System.out.println("quit from server.");
						comp.quit(pi);
					}else{
						System.out.println("command syntax error.");
					}
					break;
				}
				default:
					System.out.println("command syntax error.");
				}	
			
			} catch (Exception e) {
			    System.err.println("RMIClient exception:");
			    e.printStackTrace();
			}
		}
	}

	private void buildGUI() {
		JFrame frame = new JFrame(name+"'s client");
		JPanel panel = new JPanel();
		
		outgoing = new JTextField(20);
		incoming = new JTextArea(15, 30);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane inScroller = new JScrollPane(incoming);
		inScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JButton send = new JButton("Send");
        send.addActionListener(new SendActionListener());
        
        panel.add(inScroller);
        panel.add(outgoing);
        panel.add(send);

        frame.getContentPane().add(panel);
        frame.setSize(400,360);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}
	
	public class SendActionListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            sendMessage(outgoing.getText());
        }
    }
	

	private void sendMessage(String text) {
		message = text;
	}

	public void setInfo(String[] args){
		this.serverIP = args[1];
		this.serverPort = Integer.parseInt(args[3]);
		this.name = args[5];
		this.x = Integer.parseInt(args[7]);
		this.y = Integer.parseInt(args[8]);
		this.sex = args[10];
		this.age = args[12];
	}
        
}

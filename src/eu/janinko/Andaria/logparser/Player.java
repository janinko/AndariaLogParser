package eu.janinko.Andaria.logparser;

import java.util.ArrayList;

import eu.janinko.Andaria.logparser.messages.LocatedMessage;
import eu.janinko.Andaria.logparser.messages.Message;


public class Player implements MessageReceiver {
	protected String name;
	protected Account acc;
	protected int uid;
	
	protected ArrayList<Message> history;

	public Player(String name, int uid, Account acc) {
		this.name = name;
		this.uid = uid;
		this.acc = acc;
		this.history = new ArrayList<Message>();
	}
	public Player(String name, Account acc){
		this(name, -1, acc);
	}
	public Player(String name) {
		this(name, -1, null);
	}
	public Player(String name, int uid) {
		this(name, uid, null);
	}
	@Override
	public void receiveMessage(Message m){
		history.add(m);
		//System.out.println("P" + name + ": " + m.getMessage());
		/*switch(m.getType()){
		case PlayerSays:
			handleSays(m); break;
		case PlayerSaysMuted:
			handleSaysMuted(m); break;
		case PlayerSaysParty:
			handleSaysParty(m); break;
		case Located:
			LocatedMessage lm = (LocatedMessage) m;
			System.out.println(name + ": " + lm.getPosX() + "," + lm.getPosY() + "," + lm.getPosZ());
		default:
			//System.err.println("Unknown message: " + m.getType());
			break;
		}*/
	}

	private void handleSaysParty(Message m) {
		//System.out.println(name + ":P " + m.getMessage());
		
	}
	private void handleSaysMuted(Message m) {
		//System.out.println(name + ":M " + m.getMessage());
	}
	private void handleSays(Message m) {
		//System.out.println(name + ": " + m.getMessage());
	}

	public boolean haveName() {
		return name != null;
	}

	public void setName(String n) {
		name = n;
	}

	public boolean haveUid() {
		return uid > 0;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public boolean haveAcc(){
		return acc != null;
	}
	
	public void setAcc(Account a){
		acc = a;
	}
	
	@Override
	public String toString() {
		return "Player [name=" + name + ", acc=" + (acc != null?acc.getName():"-") + ", uid=" + uid + "]";
	}
	
	public String getName() {
		return name;
	}
	public int getUid() {
		return uid;
	}
	public Account getAcc() {
		return acc;
	}
	
}

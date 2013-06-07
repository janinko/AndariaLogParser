package eu.janinko.Andaria.logparser.model;

import eu.janinko.Andaria.logparser.model.MessageReceiver;
import java.util.HashSet;

import eu.janinko.Andaria.logparser.messages.Message;

public class Account implements MessageReceiver {
	String name;
	HashSet<Player> players;
	
	public Account(String name){
		this.name = name;
		players = new HashSet<Player>();
	}	
	
	public void addPlayer(Player p){
		players.add(p);
	}

	@Override
	public void receiveMessage(Message m){
		//System.out.println("A" + name + ": " + m.getMessage());
	}

	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		String s = "Account [name=" + name + ", players=";
		for(Player p : players){
			s+= p.getName() + ", ";
		}
		s+= "]";
		return s;
		
	}

}

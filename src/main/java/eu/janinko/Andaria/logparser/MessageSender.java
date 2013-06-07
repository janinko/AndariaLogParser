package eu.janinko.Andaria.logparser;

import eu.janinko.Andaria.logparser.messages.Message;
import eu.janinko.Andaria.logparser.model.Account;
import eu.janinko.Andaria.logparser.model.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;



public class MessageSender implements Runnable {
	//private HashMap<String, Player> byName;
	//private HashMap<Integer, Player> byUid;

	//private HashMap<String, HashSet<Player>> virtualPlayersBox;
	//private HashMap<String, Player> virtualPlayers;
	
	private HashMap<String, Account> accounts;
	
	private ConcurrentLinkedQueue<PlayersFunctionCall> inputQueue;
	boolean running = true;
	
	PlayersDB database;
	
	MessageFilter filter = null;
	

	//private HashSet<Player> unknownName;
	//private HashSet<Player> unknownUid;
	//private HashSet<Player> unknownAcc;
	
	public MessageSender(){
		database = new PlayersDB();
		//byName = new HashMap<String, Player>();
		//byUid = new HashMap<Integer, Player>();

		//virtualPlayersBox = new HashMap<String, HashSet<Player>>();
		//virtualPlayers = new HashMap<String, Player>();
		
		accounts = new HashMap<>();
		
		inputQueue = new ConcurrentLinkedQueue<>();

		//unknownName = new HashSet<Player>();
		//unknownUid = new HashSet<Player>();
		//unknownAcc = new HashSet<Player>();
	}
	
	/*public Player newPlayer(String name, Integer uid, Account acc){
		Player p;
		if(name != null && uid != null){
			p = new Player(name, uid.intValue(), acc);
			byName.put(name, p);
		}else if(name != null){
			p = new Player(name, acc);
			byName.put(name, p);
			unknownUid.add(p);
		}else if(uid != null){
			p = new Player(uid,acc);
			byUid.put(uid, p);
			//unknownName.add(p);
		}else{
			throw new NullPointerException();
		}
		return p;
	}*/
	
	public void sendMessage(String name, Integer uid, String acc, Message m){
		inputQueue.add(new PlayersFunctionCall(name, uid, acc, m,PlayersFunctionType.SendMessage));
	}
	
	private void handleMessage(String name, Integer uid, String acc, Message m){
		if(name == null && uid == null && acc == null){
			throw new NullPointerException();
		}
		if(name == null && acc == null){
			throw new IllegalArgumentException();
		}
		if(filter != null && !filter.filter(name, uid, acc, m)){
			return;
		}
		if(name != null || uid != null){
			Player p = database.getPlayer(name, uid, acc);
			p.receiveMessage(m);
		}else{
			assert(acc != null);
			assert(name == null);
			assert(uid == null);
			handleMessageAcc(acc,m);
		}
		/*
		if(name != null){
			if(uid != null){
				if(acc != null){
					handleMessageAll(name, uid, acc, m);
				}else{
					handleMessage(name,uid,m);
				}
			}else if(acc != null){
				handleMessage(name, acc, m);
			}else{
				handleMessageName(name, m);
			}
		}else{
			assert(acc != null);
			assert(uid == null);
			handleMessageAcc(acc,m);
		}*/
	}
	
	/* OLD handle message
	private void handleMessageAll(String name, Integer uid, String acc, Message m){
		Player p;
		if(byUid.containsKey(uid)){
			p = byUid.get(uid);
			if(byName.containsKey(name)){
				Player pp = byName.get(name);
				if(pp != p){
					throw new InvalidPlayersState("Name and UID do not match." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
				}
			}else if (virtualPlayersBox.containsKey(name)){
				Set<Player> ps = virtualPlayersBox.get(name);
				if(!ps.contains(p)){
					throw new InvalidPlayersState("VirtualPlayersBox doesn't contain player" + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
				}
			}else{
				byName.put(name, p);
				p.setName(name);
			}
		}else if(byName.containsKey(name)){
			p = byName.get(name);
			byUid.put(uid, p);
			p.setUid(uid);
		}else if(virtualPlayers.containsKey(name)){
			if(!accounts.containsKey(acc)){
				throw new InvalidPlayersState("Ambigiuos player name." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
			}
			Account a = accounts.get(acc);
			
			int count = 0;
			Player pc = null;
			for(Player pp : virtualPlayersBox.get(name)){
				if(pp.getAcc() == a){
					count++;
					pc = pp;
				}
			}
			if(count == 1){
				p = pc;
				if(a == null){
					a = new Account(acc);
					accounts.put(acc, a);
					a.addPlayer(p);
					p.setAcc(a);
				}
			}else{
				throw new InvalidPlayersState("Ambiguous player name." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
			}
		}else{
			p = new Player(name, uid);
			byName.put(name, p);
			byUid.put(uid, p);
		}
		
		
		if(accounts.containsKey(acc)){
			Account a = accounts.get(acc);
			if(p.getAcc() == null){
				if(a.players.contains(p)){
					throw new InvalidPlayersState("Account shouldn't contain this player, yet." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
				}
				p.setAcc(a);
				a.addPlayer(p);
			}else{
				if(p.getAcc() != a){
					throw new InvalidPlayersState("Account should contain this player." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
				}
			}
		}else if(p.getAcc() != null){
			throw new InvalidPlayersState("Player shouldn't contain account, yet." + " n: " + name + "; u: " + uid + "; a: " + acc + "; m: " + m);
		}else{
			Account a = new Account(acc);
			accounts.put(acc, a);
			a.addPlayer(p);
			p.setAcc(a);
		}
		p.receiveMessage(m);
	}
	
	private void handleMessage(String name, Integer uid, Message m){
		Player p;
		if(byUid.containsKey(uid)){
			p = byUid.get(uid);
			if(byName.containsKey(name)){
				Player pp = byName.get(name);
				if(pp != p){
					throw new InvalidPlayersState("Name and UID do not match." + " n: " + name + "; u: " + uid + "; m: " + m);
				}
			}else if (virtualPlayersBox.containsKey(name)){
				Set<Player> ps = virtualPlayersBox.get(name);
				if(!ps.contains(p)){
					throw new InvalidPlayersState("VirtualPlayersBox doesn't contain player" + " n: " + name + "; u: " + uid + "; m: " + m);
				}
			}else{
				byName.put(name, p);
				p.setName(name);
			}
		}else if(byName.containsKey(name)){
			p = byName.get(name);
			byUid.put(uid, p);
			p.setUid(uid);
		}else if(virtualPlayers.containsKey(name)){
			throw new InvalidPlayersState("Ambiguous player name." + " n: " + name + "; u: " + uid + "; m: " + m);
		}else{
			p = new Player(name, uid);
			byName.put(name, p);
			byUid.put(uid, p);
		}
		p.receiveMessage(m);
	}
	
	private void handleMessage(String name, String acc, Message m){
		Player p;
		if(byName.containsKey(name)){
			p = byName.get(name);
			if(accounts.containsKey(acc)){
				Account a = accounts.get(acc);
				if(p.getAcc() == null){
					if(a.players.contains(p)){
						throw new InvalidPlayersState("Account shouldn't contain this player, yet" + " n: " + name + "; a: " + acc + "; m: " + m);
					}
					p.setAcc(a);
					a.addPlayer(p);
				}else{
					if(p.getAcc() != a){
						throw new InvalidPlayersState("Account should contain this player." + " n: " + name + "; a: " + acc + "; m: " + m);
					}
				}
			}else if(p.getAcc() != null){
				throw new InvalidPlayersState("Player shouldn't contain account, yet." + " n: " + name + "; a: " + acc + "; m: " + m);
			}else{
				Account a = new Account(acc);
				accounts.put(acc, a);
				a.addPlayer(p);
				p.setAcc(a);
			}
		}else if(virtualPlayersBox.containsKey(name)){
			Account a = null;
			if(accounts.containsKey(acc)){
				a = accounts.get(acc);
			}
			int count = 0;
			Player pc = null;
			for(Player pp : virtualPlayersBox.get(name)){
				if(pp.getAcc() == a){
					count++;
					pc = pp;
				}
			}
			if(count == 1){
				p = pc;
				if(a == null){
					a = new Account(acc);
					accounts.put(acc, a);
					a.addPlayer(p);
					p.setAcc(a);
				}
			}else{
				throw new InvalidPlayersState("Ambiguous player name." + " n: " + name + "; u: " + acc + "; m: " + m);
			}
		}else{
			Account a;
			if(accounts.containsKey(acc)){
				a = accounts.get(acc);
			}else{
				a = new Account(acc);
			}
			p = new Player(name, a);
			byName.put(name, p);
			accounts.put(acc, a);
			a.addPlayer(p);
		}
		p.receiveMessage(m);
	}

	private void handleMessageName(String name, Message m) {
		if(byName.containsKey(name)){
			byName.get(name).receiveMessage(m);
		}else if(virtualPlayers.containsKey(name)){
			virtualPlayers.get(name).receiveMessage(m);
		}else{
			Player p = new Player(name);
			byName.put(name, p);
			p.receiveMessage(m);
		}
	}
	*/
	
	private void handleMessageAcc(String acc, Message m){
		if(accounts.containsKey(acc)){
			accounts.get(acc).receiveMessage(m);
		}else{
			Account a = new Account(acc);
			accounts.put(acc, a);
			a.receiveMessage(m);
		}
	}
	
	public void setFilter(MessageFilter f){
		filter = f;
	}

	public void print() {
		HashSet<Player> players = new HashSet<>();
		//players.addAll(byName.values());
		//players.addAll(byUid.values());
		//players.addAll(unknownUid);

		for(Player p: players){
			System.out.println(p);
		}

		for(Account a: accounts.values()){
			System.out.println(a);
		}
		
	}
	
	public void altNameSet(String name, String altname){
		/*Player p = byName.get(name);
		if(p == null){
			throw new IllegalStateException("player '" + name + "' seems to be nonexistent yet");
		}
		if(byName.containsKey(altname)){
			Player vpo = byName.get(altname);
			if(virtualPlayersBox.containsKey(altname)){
				throw new IllegalStateException("virtualPlayers already contains " + altname);
			}
			HashSet<Player> vpls = new HashSet<Player>();
			virtualPlayersBox.put(altname, vpls);
			vpls.add(p);
			vpls.add(vpo);
			byName.remove(vpo);
		}else if(virtualPlayersBox.containsKey(altname)){
			virtualPlayersBox.get(altname).add(p);
		}else{
			byName.put(altname, p);
		}*/
	}
	
	public void altNameReset(String name, Integer uid, String altname){
		/*Player p = byName.get(name);
		if(p == null){
			throw new IllegalStateException("player '" + name + "' seems to be nonexistent yet");
		}
		if(byName.containsKey(altname)){
			Player vpo = byName.get(altname);
			if(virtualPlayersBox.containsKey(altname)){
				throw new IllegalStateException("virtualPlayers should not contain " + altname);
			}
			byName.remove(vpo);
		}else if(virtualPlayersBox.containsKey(altname)){
			virtualPlayersBox.get(altname).add(p);
		}else{
			byName.put(altname, p);
		}*/
	}
	
	/*private boolean checkConsistency(String name, Integer uid, String acc){
		Account a = null;
		Player pn = null;
		Player pu = null;
		if(acc != null){
			a = accounts.get(acc);
		}
		if(name != null){
			pn = byName.get(name);
		}
		if(uid != null){
			pu = byUid.get(uid);
		}
		return false;
	}*/
	

	public void rename(Integer uid, String altname) {
		inputQueue.add(new PlayersFunctionCall(altname, uid, null, null,PlayersFunctionType.Rename));
	}

	private void handleRename(Integer uid, String altname) {
	/*	if(!byUid.containsKey(uid)){
			throw new InvalidPlayersState("Player not found by uid " + " an: " + altname + "; u: " + uid);
		}
		Player p = byUid.get(uid);
	System.out.print("rename " + uid + " to '" + altname + "'");
		if(byName.containsValue(p)){
			int count=0;
			String oldname = null;
			String ooldname = null;
			for( Entry<String, Player> e : byName.entrySet()){
				if(e.getValue() == p){
					count++;
					ooldname = oldname;
					oldname = e.getKey();
				}
			}
			if(count != 1){
				if((count == 2) && (p.getName() == oldname || p.getName() == ooldname) && (altname == oldname || altname == ooldname )){
					byName.remove(altname);
					oldname = p.getName();
				}else{
					throw new InvalidPlayersState("byName contains multiple same players " + " an: " + altname + "; u: " + uid);
				}
			}
	System.out.print(" from '" + oldname + "' B");
			byName.remove(oldname);
			p.setName(altname);
		}else{
			int count = 0;
			String oldname = null;
			for(Entry<String, HashSet<Player>> e : virtualPlayersBox.entrySet()){
				for(Player pp : e.getValue()){
					if(pp == p){
						oldname = e.getKey();
						count++;
					}
				}
			}
			if(count != 1){
				throw new InvalidPlayersState("virtualPlayersBox contains multiple same players " + " an: " + altname + "; u: " + uid);
			}
	System.out.print(" from '" + oldname + "' V");
			HashSet<Player> ps = virtualPlayersBox.get(oldname);
			ps.remove(p);
			if(ps.isEmpty()){
				virtualPlayersBox.remove(oldname);
			}
			p.setName(altname);
		}
		if(byName.containsKey(altname)){
	System.out.print(", byName conains alt");
			Player op = byName.get(altname);
			Player vp = new Player(altname);
			HashSet<Player> ps = new HashSet<Player>();
			virtualPlayers.put(altname, vp);
			virtualPlayersBox.put(altname, ps);
			ps.add(op);
		}
		if(virtualPlayers.containsKey(altname)){
	System.out.print(", virtualPlayers conains alt");
			HashSet<Player> ps = virtualPlayersBox.get(altname);
			ps.add(p);
		}else{
	System.out.print(", OK");
			byName.put(altname, p);
		}
		System.out.println();*/
	}

	@Override
	public void run() {
		while(running){
			if(!inputQueue.isEmpty()){
				PlayersFunctionCall smc = inputQueue.poll();
				switch(smc.t){
				case SendMessage:
					handleMessage(smc.name, smc.uid, smc.acc, smc.m); break;
				case Rename:
					handleRename(smc.uid, smc.name); break;
				}
			}else{
				try {
					Thread.sleep(28);
				} catch (InterruptedException e) {
					e.printStackTrace();
					running = false;
				}
			}
		}
		
	}

	public void setRunning(boolean b) {
		this.running = b;
	}
	

}
class PlayersFunctionCall{
	String name;
	Integer uid;
	String acc;
	Message m;
	PlayersFunctionType t;
	
	public PlayersFunctionCall(String name, Integer uid, String acc, Message m, PlayersFunctionType t) {
		this.name = name;
		this.uid = uid;
		this.acc = acc;
		this.m = m;
		this.t = t;
	}
}

enum PlayersFunctionType{
	SendMessage,
	Rename
}
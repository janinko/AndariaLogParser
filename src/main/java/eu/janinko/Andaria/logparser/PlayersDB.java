package eu.janinko.Andaria.logparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayersDB {
	private Map<String, Jmeno> jmena;
	private Map<Integer, RealnaPostava> uids;
	private Map<String, Ucet> accs;
	
	public PlayersDB(){
		jmena = new HashMap<String, Jmeno>();
		uids = new HashMap<Integer, RealnaPostava>();
		accs = new HashMap<String, Ucet>();
	}
	
	public Player getPlayer(String name, Integer uid, String acc){
		if(uid == null && name == null) throw new IllegalArgumentException("Must be set uid, or at least name.");
		
		Postava p = null;
		if(uid != null){ // if UID is set try to find player by UID
			p = getByUID(name, uid, acc);	// in p is desired player
		}
		if(p == null && name != null){ // if we didn't find player, and know name, try to find by Name
			p = getByName(name, uid, acc);	// in p is either player or virtual player
		}
		if(p == null){	// if this UID and name isn't present yet,
			p = newPlayer(name, uid, acc);	// create new player
		}
		return p.getPlayer();
	}

	private RealnaPostava newPlayer(String name, Integer uid, String acc) {
		RealnaPostava p = new RealnaPostava();
		if(name != null){
			setName(p, name);
		}
		if(uid != null){
			setUid(p, uid);
		}
		if(acc != null){
			setAcc(p, acc);
		}
		
		return p;
	}

	
	// Najde postavu podle UID a pripadne nastavi name a acc
	private RealnaPostava getByUID(String name, Integer uid, String acc){
		RealnaPostava p = uids.get(uid);
		if(p == null) return null;
		
		if(acc != null && p.acc == null){
			setAcc(p, acc);
		}
		if(name != null && !p.jmena.contains(name)){
			setName(p, name);
		}
		
		return p;
	}
	
	private Postava getByName(String name, Integer uid, String acc){
		Jmeno j = jmena.get(name);
		if(j == null) return null;
		RealnaPostava p = j.getPostava(uid, acc);
		if(p == null){
			return j.vp;
		}
		
		if(acc != null && p.acc != null && !acc.equals(p.acc)){
			return null;
		}
		if(acc != null && p.acc == null){
			setAcc(p, acc);
		}
		if(uid != null && p.uid != null && !uid.equals(p.uid)){
			return null;
		}
		if(uid != null && p.uid == null){
			setUid(p, uid);
		}
		
		return p;
	}
	
	
	
	private void setName(RealnaPostava p, String name) {
		p.jmena.add(name);
		Jmeno j;
		if(jmena.containsKey(name)){
			j = jmena.get(name);
		}else{
			j = new Jmeno(name);
			jmena.put(name, j);
		}
		j.addPostava(p);
		p.p.setName(name);
	}
	
	private void setUid(RealnaPostava p, Integer uid) {
		p.uid = uid;
		uids.put(uid, p);
		p.p.setUid(uid);
	}
	
	private void setAcc(RealnaPostava p, String acc) {
		p.acc = acc;
		Ucet u;
		if(accs.containsKey(acc)){
			u = accs.get(acc);
		}else{
			u = new Ucet(acc);
			accs.put(acc, u);
		}
		u.postavy.add(p);
		p.p.setAcc(u.a);
	}

	public Set<Player> getPlayers() {
		HashSet<Player> ps = new HashSet<Player>();
		
		for(Jmeno j : jmena.values()){
			for(RealnaPostava rp : j.postavy){
				ps.add(rp.p);
			}
		}
		for(RealnaPostava rp : uids.values()){
			ps.add(rp.p);
		}
		for(Ucet u : accs.values()){
			for(RealnaPostava rp : u.postavy){
				ps.add(rp.p);
			}
		}
		
		return ps;
	}

}




class Jmeno{
	String jmeno;
	Set<RealnaPostava> postavy;
	VirtualniPostava vp;
	
	Jmeno(String jmeno){
		this.jmeno = jmeno;
		postavy = new HashSet<RealnaPostava>();
	}

	void addPostava(RealnaPostava p) {
		if(this.postavy.size() == 1 && vp == null){
			vp = new VirtualniPostava(jmeno);
			vp.p = new Player(jmeno);
		}
		postavy.add(p);
	}

	RealnaPostava getPostava(Integer uid, String acc){
		//System.out.println("postavy.size() = " + postavy.size());
		if(postavy.size() == 0) throw new IllegalStateException();
		if(postavy.size() == 1) return postavy.iterator().next();
		if(uid == null && acc == null) return null;
		for(RealnaPostava p : postavy){
			if((uid != null && uid.equals(p.uid)) || (acc != null && acc.equals(p.acc))){
				return p;
			}
		}
		return null;
	}
}

class Ucet implements Comparable<Ucet>{
	String jmeno;
	Set<RealnaPostava> postavy;
	Account a;
	
	Ucet(String jmeno){
		this.jmeno = jmeno;
		postavy = new HashSet<RealnaPostava>();
		a = new Account(jmeno);
	}
	
	@Override
	public int hashCode() {
		return jmeno.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ucet other = (Ucet) obj;
		return jmeno.equals(other.jmeno);
	}

	@Override
	public int compareTo(Ucet other) {
		return jmeno.compareTo(other.jmeno);
	}
	
}

interface Postava{
	Player getPlayer();
}

class VirtualniPostava implements Postava{
	String jmeno;
	Player p;
	
	VirtualniPostava(String jmeno){
		this.jmeno = jmeno;
		p = new Player(jmeno);
	}

	@Override
	public Player getPlayer() {
		return p;
	}
	
}

class RealnaPostava implements Postava{
	Set<String> jmena;
	Integer uid;
	String acc;
	Player p;
	
	RealnaPostava(){
		jmena = new HashSet<String>();
		p = new Player(null);
	}

	@Override
	public Player getPlayer() {
		return p;
	}	
}
package eu.janinko.Andaria.logparser.filter;

import eu.janinko.Andaria.logparser.MessageFilter;
import eu.janinko.Andaria.logparser.messages.Message;
import java.util.Set;

public class UidFilter implements MessageFilter {
	
	int uids[];

	public UidFilter(int ... u){
		uids = u;
	}
	
	public UidFilter(Set<Integer> u){
		uids = new int[u.size()];
		int i=0;
		for(int uid : u){
			uids[i++] = uid;
		}
	}

	@Override
	public boolean filter(String name, Integer uid, String acc, Message m) {
		if(uid == null) return false;
		for(int i : uids){
			if(uid == i) return true;
		}
		return false;
	}
	
	

}

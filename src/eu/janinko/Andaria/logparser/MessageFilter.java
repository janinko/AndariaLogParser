package eu.janinko.Andaria.logparser;

import eu.janinko.Andaria.logparser.messages.Message;

public interface MessageFilter {
	
	boolean filter(String name, Integer uid, String acc, Message m);

}

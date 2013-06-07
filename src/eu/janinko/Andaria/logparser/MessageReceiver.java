package eu.janinko.Andaria.logparser;

import eu.janinko.Andaria.logparser.messages.Message;

public interface MessageReceiver {

	public abstract void receiveMessage(Message m);

}
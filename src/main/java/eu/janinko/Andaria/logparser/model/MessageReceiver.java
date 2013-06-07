package eu.janinko.Andaria.logparser.model;

import eu.janinko.Andaria.logparser.messages.Message;

public interface MessageReceiver {

	public abstract void receiveMessage(Message m);

}
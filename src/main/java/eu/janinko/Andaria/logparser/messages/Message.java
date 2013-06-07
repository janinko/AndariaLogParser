package eu.janinko.Andaria.logparser.messages;
import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.janinko.Andaria.logparser.MessageType;


public class Message {
	protected Calendar datetime;
	protected String message;
	protected MessageType type;

	public Message(Calendar dt, String m, MessageType t){
		datetime = (Calendar) dt.clone(); 
		message = new String(m);
		type = t;
	}
	
	public String getMessage(){
		return message;
	}

	public Calendar getDateTime(){
		return datetime;
	}
	
	public MessageType getType(){
		return type;
	}

}

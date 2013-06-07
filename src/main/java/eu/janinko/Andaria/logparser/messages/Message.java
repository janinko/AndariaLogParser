package eu.janinko.Andaria.logparser.messages;
import eu.janinko.Andaria.logparser.MessageType;
import java.util.Calendar;

public class Message {
	protected Calendar datetime;
	protected CharSequence message;
	protected MessageType type;

	public Message(Calendar dt, CharSequence m, MessageType t){
		datetime = (Calendar) dt.clone(); 
		message = m;
		type = t;
	}
	
	public String getMessage(){
		return message.toString();
	}

	public Calendar getDateTime(){
		return datetime;
	}
	
	public MessageType getType(){
		return type;
	}

}

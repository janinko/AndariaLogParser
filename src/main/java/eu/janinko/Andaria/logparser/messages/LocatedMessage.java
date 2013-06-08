package eu.janinko.Andaria.logparser.messages;

import eu.janinko.Andaria.logparser.MessageType;
import java.util.Calendar;

public class LocatedMessage extends Message {
	int posX, posY, posZ;
	
	
	public LocatedMessage(Calendar c, CharSequence m, int posX, int posY, int posZ, MessageType t){
		super(c,m,t);
		if(!t.is(MessageType.Located)) throw new IllegalArgumentException("MessageType must be MessageType.Located, is " + t);

		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public LocatedMessage(Calendar c, CharSequence m, int posX, int posY, int posZ){
		this(c,m,posX,posY,posZ,MessageType.Located);
	}
	
	public int getPosX() {
		return posX;
	}


	public int getPosY() {
		return posY;
	}


	public int getPosZ() {
		return posZ;
	}

}

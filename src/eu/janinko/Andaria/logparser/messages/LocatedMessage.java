package eu.janinko.Andaria.logparser.messages;

import java.util.Calendar;

import eu.janinko.Andaria.logparser.MessageType;

public class LocatedMessage extends Message {
	int posX, posY, posZ;
	
	
	public LocatedMessage(Calendar c, String m, int posX, int posY, int posZ, MessageType t){
		super(c,m,t);
		if(!t.is(MessageType.Located)) throw new IllegalArgumentException("MessageType must be MessageType.Located, is " + t);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
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
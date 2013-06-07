package eu.janinko.Andaria.logparser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import eu.janinko.Andaria.logparser.messages.LocatedMessage;

public class LocationTellerPlayer extends Player {
	private Player player;

	protected int posX = 0;
	protected int posY = 0;
	protected int posZ = 0;
	
	protected Calendar lastTold;
	protected int lastIndex;
	
	public LocationTellerPlayer(Player p){
		super(p.name, p.uid, p.acc);
		player = p;
		lastTold = new GregorianCalendar(1,1,1);
		lastIndex = -1;
	}
	
	public String getPosAt(Calendar c){
		if(lastTold.compareTo(c) > 0){
			lastTold = new GregorianCalendar(1,1,1);
			lastIndex = -1;
		}
		int idx = lastIndex + 1;

		
		
		while( idx < player.history.size() && 
				c.compareTo(player.history.get(idx).getDateTime()) > 0){
			
			if(player.history.get(idx).getType().is(MessageType.Located)){
				LocatedMessage lm = (LocatedMessage) player.history.get(idx);
				posX = lm.getPosX();
				posY = lm.getPosY();
				posZ = lm.getPosZ();
			}
			idx++;
		}
		lastIndex = idx -1;
		
		return posX + "," + posY +","+ posZ;
	}
	
	public static void main(String args[]){
		try {
			Parser p = new Parser("sphere2012-04-06:201.log");
			p.parse();
			
			Set<Player> players = p.sender.database.getPlayers();

			Set<LocationTellerPlayer> moji = new HashSet<LocationTellerPlayer>();
			
			for(Player pl : players){
				if(pl.uid == 0x307fd || 
				   pl.uid == 0x257e4){
					moji.add(new LocationTellerPlayer(pl));
				}
			}
			
			Calendar c = new GregorianCalendar(2012, 4, 6, 20, 45, 0);
			Calendar targ = new GregorianCalendar(2012, 4, 6, 21, 30, 0);
			
			//20:30 - 20:55
			while (c.compareTo(targ) < 0){
				for(LocationTellerPlayer ltp : moji){
					String pos = ltp.getPosAt(c);
					System.out.println(c.get(Calendar.HOUR_OF_DAY) + ":" + 
									   c.get(Calendar.MINUTE) + ":" +
									   c.get(Calendar.SECOND) + "\t" +
									   ltp.name + "\t" + pos);
				}
				c.add(Calendar.SECOND, 1);
			}
			p.stop();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
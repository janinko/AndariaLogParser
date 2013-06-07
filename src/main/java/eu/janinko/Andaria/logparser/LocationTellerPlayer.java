package eu.janinko.Andaria.logparser;

import eu.janinko.Andaria.logparser.messages.LocatedMessage;
import eu.janinko.Andaria.logparser.model.Player;
import eu.janinko.Andaria.logparser.parsers.MainParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

public class LocationTellerPlayer extends Player {
	private Player player;

	protected int posX = 0;
	protected int posY = 0;
	protected int posZ = 0;
	
	protected Calendar lastTold;
	protected int lastIndex;
	
	public LocationTellerPlayer(Player p){
		super(p.getName(), p.getUid(), p.getAcc());
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

		
		
		while( idx < player.getHistory().size() &&
				c.compareTo(player.getHistory().get(idx).getDateTime()) > 0){
			
			if(player.getHistory().get(idx).getType().is(MessageType.Located)){
				LocatedMessage lm = (LocatedMessage) player.getHistory().get(idx);
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
			MainParser p = new MainParser(new FileReader("sphere2012-04-06:201.log"));
			p.start();
			p.parse();
			
			Set<Player> players = p.getSender().database.getPlayers();

			Set<LocationTellerPlayer> moji = new HashSet<>();
			
			for(Player pl : players){
				if(pl.getUid() == 0x307fd ||
				   pl.getUid() == 0x257e4){
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

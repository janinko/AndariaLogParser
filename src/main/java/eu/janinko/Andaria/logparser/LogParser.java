package eu.janinko.Andaria.logparser;

import eu.janinko.Andaria.logparser.parsers.Parser;
import eu.janinko.Andaria.logparser.model.Player;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import eu.janinko.Andaria.logparser.filter.UidFilter;

public class LogParser {
	static SimpleDateFormat sdf =
	          new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public static void main(String args[]){
		String filename = null;
		HashSet<Integer> uids = new HashSet<Integer>();
		Calendar from = null;
		Calendar to = null;
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-f")){
				filename = args[++i]; continue;
			}
			if(args[i].equals("-u")){
				uids.add(Integer.parseInt(args[++i],16)); continue;
			}
			if(args[i].equals("--from")){
				from = Calendar.getInstance();
				try {
					from.setTime(sdf.parse(args[++i]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				continue;
			}
			if(args[i].equals("--to")){
				to = Calendar.getInstance();
				try {
					to.setTime(sdf.parse(args[++i]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				continue;
			}
			System.err.println("Spatny argument: " + args[i]);
			System.exit(1);
		}
		if(filename == null){
			System.err.println("Musíš specifikovat soubor. ( -f )");
			System.exit(1);
		}
		if(from == null){
			System.err.println("Musíš specifikovat odkdy. ( --from )");
			System.exit(1);
		}
		if(to == null){
			System.err.println("Musíš specifikovat dokdy. ( --to )");
			System.exit(1);
		}
		Parser parser = null;
		try {
			parser = new Parser(filename);
		} catch (Exception e) {
			System.err.println("Chyba otevirani souboru.");
			System.exit(1);
		}
		UidFilter filter = null;
		if(!uids.isEmpty()){
			filter = new UidFilter(uids);
		}
		
		parser.getSender().setFilter(filter);
		try {
			parser.parse();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		parser.stop();
		
		Set<Player> players = parser.getSender().database.getPlayers();
		Set<LocationTellerPlayer> moji = new HashSet<LocationTellerPlayer>();
		for(Player pl : players){
			if(uids.contains(pl.getUid())){
				moji.add(new LocationTellerPlayer(pl));
			}
		}

		while (from.compareTo(to) < 0){
			for(LocationTellerPlayer ltp : moji){
				String pos = ltp.getPosAt(from);
				int h = from.get(Calendar.HOUR_OF_DAY);
				int m = from.get(Calendar.MINUTE);
				int s = from.get(Calendar.SECOND);
				System.out.println( (h<10 ? "0" : "") + h + ":" + 
									(m<10 ? "0" : "") + m + ":" +
									(s<10 ? "0" : "") + s + "\t" +
								   ltp.getName() + "\t" + pos);
			}
			from.add(Calendar.SECOND, 1);
		}
		
		
	}

}

package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;

/**
 *
 * @author janinko
 */
public class NMParser implements Parser{
	private Log log;
	private MessageSender sender;
	private TimeParser timeParser;

	private String[] kouzla = {
		"s_lightning_new",
		"s_flamestrike_new",
		"s_ice_storm_new",
		"s_magic_arrow_new",
		"s_reveal_new",
		"s_heal_new"
	};

	public NMParser(Log log, MessageSender sender, TimeParser timeParser) {
		this.log = log;
		this.sender = sender;
		this.timeParser = timeParser;
	}

	@Override
	public void parseLine() {
		String name = log.wl.getUntil(" - Cast");
		log.wl.substring(8);
		String spelnum = null;
		if (log.wl.matches("[0-9a-f]+")) {
			spelnum = log.wl.toString();
		} else if (log.wl.matches("[0-9]{3}_kouzla")) {
			spelnum = "719_kouzla";
		} else {
			for (String kouzlo: kouzla){
				if( log.wl.equals(kouzlo)){
					spelnum = kouzlo;
					break;
				}
			}
			if(spelnum == null){
				log.unknownLine("parseStandartLine CastSpecial");
			}
		}// TODO
		//System.out.println(line + " --- " + name + " CS " + spelnum);
	}

}

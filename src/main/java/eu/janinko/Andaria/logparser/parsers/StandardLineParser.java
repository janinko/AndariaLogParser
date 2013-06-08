package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;
import eu.janinko.Andaria.logparser.MessageType;
import eu.janinko.Andaria.logparser.messages.LocatedMessage;
import eu.janinko.Andaria.logparser.messages.Message;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author janinko
 */
public class StandardLineParser implements Parser{

	private Log log;
	private MessageSender sender;
	private TimeParser timeParser;

	public StandardLineParser(Log log, MessageSender sender, TimeParser timeParser) {
		this.log = log;
		this.sender = sender;
		this.timeParser = timeParser;
	}


	Matcher match_stlCOMMON = Pattern.compile("[^(0:-]+ \\(.*").matcher("");
	Matcher match_stlMAJITEL = Pattern.compile("[^(0:-]+ \\(majitel\\) \\(.*").matcher("");
	Matcher match_stlPOSXY = Pattern.compile("\\(0[0-9a-f]+/[0-9]+,[0-9]+\\).*").matcher("");
	Matcher match_stlPOSXYZ = Pattern.compile("\\(0[0-9a-f]+/[0-9]+,[0-9]+,[0-9-]+\\).*").matcher("");
	Matcher match_stlJUSTPOSXY = Pattern.compile(" [0-9]+,[0-9]+").matcher("");
	Matcher match_stlJUSTPOSXYZ = Pattern.compile(" [0-9]+,[0-9]+,[0-9-]+").matcher("");
	Matcher match_stlJUSTUID = Pattern.compile("\\(0[0-9a-f]+\\).*").matcher("");
	Matcher match_stlJUSTACC = Pattern.compile("\\([^)]+\\).*").matcher("");
	Matcher match_stlJUSTUIDANDACC = Pattern.compile("[^(0:-]+ 0.*").matcher("");
	Matcher match_stlUIDANDACC = Pattern.compile("0[0-9a-f]+ \\([^)]+\\).*").matcher("");
	Matcher match_stlCASTSPECIAL = Pattern.compile(".+ - CastSpecial .*").matcher("");
	@Override
	public void parseLine() {
		if(log.wl.length() == 0){
			// TODO
		}else if(match_stlCOMMON.reset(log.wl).matches()){
			if(match_stlMAJITEL.reset(log.wl).matches()){
				log.wl.replaceFirst("\\(majitel\\) ", ""); // TODO: WA
			}
			String name = log.wl.getUntil(" (");
			if(match_stlPOSXY.reset(log.wl).matches()){
				log.wl.substring(1);
				String uid = log.wl.getUntil("/");
				String posX = log.wl.getUntil(",");
				String posY = log.wl.getUntil(")");
				parseStandartLinePreparsed(name, uid, posX, posY, "0", null);
			}else if(match_stlPOSXYZ.reset(log.wl).matches()){
				log.wl.substring(1);
				String uid = log.wl.getUntil("/");
				String posX = log.wl.getUntil(",");
				String posY = log.wl.getUntil(",");
				String posZ = log.wl.getUntil(")");
				parseStandartLinePreparsed(name, uid, posX, posY, posZ, null);
			}else if(match_stlJUSTUID.reset(log.wl).matches()){
				log.wl.substring(1);
				String uid = log.wl.getUntil(")");
				parseStandartLinePreparsed(name, uid, null, null, null, null);
			}else if(match_stlJUSTACC.reset(log.wl).matches()){
				log.wl.substring(1);
				String acc = log.wl.getUntil(")");
				String posX = null, posY = null, posZ = null;
				if(match_stlJUSTPOSXY.reset(log.wl).matches()){
					log.wl.substring(1);
					posX = log.wl.getUntil(",");
					posY = log.wl.rest();
				}else if(match_stlJUSTPOSXYZ.reset(log.wl).matches()){
					log.wl.substring(1);
					posX = log.wl.getUntil(",");
					posY = log.wl.getUntil(",");
					posZ = log.wl.rest();
				}
				parseStandartLinePreparsed(name, null, posX, posY, posZ, acc);
			}else{
				log.unknownLine("parseStandartLine Standart");
			}
		}else if(match_stlJUSTUIDANDACC.reset(log.wl).matches()){
			String name = log.wl.getUntil(" 0");
			if(match_stlUIDANDACC.reset(log.wl).matches()){
				String uid = log.wl.getUntil(" ");
				log.wl.substring(1);
				String acc = log.wl.getUntil(") ");
				parseStandartLinePreparsed(name, uid, null, null, null, acc);
			}else{
				log.unknownLine("parseStandartLine Uid");
			}
		}else if(match_stlCASTSPECIAL.reset(log.wl).matches()){
			String name = log.wl.getUntil(" - Cast");
			log.wl.substring(14);
			String spelnum;
			if(log.wl.matches("[0-9a-f]+")){
				spelnum = log.wl.toString();
			}else if(log.wl.equals("s_lightning_new")){
				spelnum = "s_lightning_new";
			}else if(log.wl.equals("s_flamestrike_new")){
				spelnum = "s_flamestrike_new";
			}else if(log.wl.equals("s_ice_storm_new")){
				spelnum = "s_ice_storm_new";
			}else if(log.wl.equals("s_magic_arrow_new")){
				spelnum = "s_magic_arrow_new";
			}else if(log.wl.matches("[0-9]{3}_kouzla")){
				spelnum = "719_kouzla";
			}else{
				log.unknownLine("parseStandartLine CastSpecial");
			}// TODO
			//System.out.println(line + " --- " + name + " CS " + spelnum);
		}else if(log.wl.startsWith("Banka - ")){
			// TODO
		}else if(log.wl.startsWith("** Casove scripty")){
			// TODO
		}else if(log.wl.startsWith("### Mnozeni koni - ")){
			// TODO
		}else if(log.wl.contains("pouziva Spojovac na")){
			String name = log.wl.getUntil("(");
			// TODO
		}else if(log.wl.startsWith("AUTORESTOCK: ")){
			// TODO
		}else if(log.wl.startsWith("GM Chat: ")){
			// TODO
		}else if(log.wl.startsWith("GALEJE: ")){
			// TODO
		}else if(log.wl.startsWith("UTOK NA NPC: ")){
			// TODO
		}else if(log.wl.startsWith("QUESTY: Postava ")){
			// TODO
		}else if(log.wl.startsWith("SOGURKIN - NAKUP SOSEK: ")){
			// TODO
		}else if(log.wl.startsWith("Konske pripusteni: ")){
			// TODO
		}else if(log.wl.equals("Zacina svitat")){
			// TODO
		}else if(log.wl.equals("Slunce zacina zapadat")){
			// TODO
		}else if(log.wl.contains("Global message")){
			// TODO
		}else{
			log.unknownLine("parseStandartLine");
		}

	}



	private void parseStandartLinePreparsed(String name, String u,
			String pX, String pY, String pZ, String acc) {
		Integer uid = null;
		if(u != null){
			uid = Integer.parseInt(u,16);
		}
		Integer posX = null, posY=null, posZ =null;
		if(pX != null && pY != null && pZ != null){
			posX = Integer.parseInt(pX,10);
			posY = Integer.parseInt(pY,10);
			posZ = Integer.parseInt(pZ,10);
		}

		if(log.wl.charAt(0) == ':') log.wl.substring(1);
		if(log.wl.charAt(0) != ' '){
			log.unknownLine("parseStandartLinePreparsed notspace"); return;
		}
		log.wl.substring(1);

		if(log.wl.startsWith("Drp_Itm ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Drp_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Pck_Pack ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Pck_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("DEKORATER: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("KOS: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Use ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("- spravne heslo - ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("- Zmena hesla na ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("otevrel zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("pokus otevrit zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("prevedl vlastnictvi zabezpecene nadoby ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("EqTest ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("UnEq ")){
			if(log.wl.startsWith("UnEq Pamet promen ") || log.wl.startsWith("UnEq metamorfoza ")){
				sender.rename(uid,name);
			}

			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Mount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("DisMount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("SpEff_New ")){
			// TODO
		}else if(log.wl.startsWith("SpEff ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("+ ")){
			// TODO
		}else if(log.wl.startsWith("Hit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("GHit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Killed ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("U_CntMnu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Ability ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.equals("pouziva HELP ROOM")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("skillgain ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.matches("Sk[SF][tua] .*")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.equals("-> OdLogZona")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,0,0,0,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("LogIn ")){
			sender.sendMessage(name, uid, acc, new Message(timeParser.getCalendar(),log.wl,MessageType.AT));
			// TODO
		}else if(log.wl.startsWith("LogOut ")){
			sender.sendMessage(name, uid, acc, new Message(timeParser.getCalendar(),log.wl,MessageType.AT));
			// TODO
		}else if(log.wl.contains("AAFKK")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("check_num ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Bankovni ucet ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("Incognito: ")){
			log.wl.substring(11);
			MessageType t;
			if(log.wl.charAt(0) == 'z' && log.wl.charAt(1) == ' ' ){
				log.wl.substring(2);
				t = MessageType.IncognitoReset;
			}else if(log.wl.charAt(0) == 'n' && log.wl.charAt(1) == 'a' && log.wl.charAt(2) == ' ' ){
				log.wl.substring(3);
				t = MessageType.IncognitoSet;
			}else{
				log.unknownLine("parseStandartLinePreparsed Incognito"); return;
			}
			if(log.wl.length() == 0){
				throwAway(); return;
			}
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,t));
		}else if(log.wl.startsWith("Opravuje ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("GuildSystem: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(log.wl.startsWith("prelevani ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ,MessageType.Located));
			// TODO
		}else{
			log.unknownLine("parseStandartLinePreparsed"); return;
		}



	}



	private void throwAway() {
		// TODO Auto-generated method stub

	}


}

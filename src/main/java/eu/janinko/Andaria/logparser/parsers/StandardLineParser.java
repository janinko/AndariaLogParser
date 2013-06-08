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

	private NMParser nmParser;

	public StandardLineParser(Log log, MessageSender sender, TimeParser timeParser) {
		this.log = log;
		this.sender = sender;
		this.timeParser = timeParser;
		
		this.nmParser = new NMParser(log, sender, timeParser);
	}

	private Matcher match_COMMON = Pattern.compile("[^(0:-]+ \\(.*").matcher("");
	private Matcher match_JUSTUIDANDACC = Pattern.compile("[^(0:-]+ 0.*").matcher("");
	private Matcher match_CASTSPECIAL = Pattern.compile(".+ - CastSpecial .*").matcher("");
	@Override
	public void parseLine() {
		if(log.wl.length() == 0){
			log.unknownLine();
		}else if(match_COMMON.reset(log.wl).matches()){
			parseCommon();
		}else if(match_JUSTUIDANDACC.reset(log.wl).matches()){
			parseJustUidAndAcc();
		}else if(match_CASTSPECIAL.reset(log.wl).matches()){
			nmParser.parseLine();
		}else if(log.wl.startsWith("Banka - ")){
			// TODO
		}else if(log.wl.startsWith("** Casove scripty")){
			// TODO
		}else if(log.wl.startsWith("### Mnozeni koni - ")){
			// TODO
		}else if(log.wl.contains("pouziva Spojovac na")){
			String name = log.wl.getUntilAndKeep("(");
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
		}else if(log.wl.startsWith("LodniSystem: ")){
			// TODO
		}else if(log.wl.startsWith("ShipSystem: ")){
			// TODO
		}else if(log.wl.startsWith("PageResp: ")){
			// TODO
		}else if(log.wl.startsWith("SHRINK: ")){
			// TODO
		}else if(log.wl.startsWith("Zaciatok vycviku kona: ")){
			// TODO
		}else if(log.wl.equals("Zacina svitat")){
			// TODO
		}else if(log.wl.equals("Slunce zacina zapadat")){
			// TODO
		}else if(log.wl.contains("Global message")){
			// TODO
		}else if(log.wl.contains("vola straze")){
			// TODO
		}else if(log.wl.matches("[0-9]+ - .*")){
			// TODO
		}else{
			log.unknownLine("parseStandartLine");
		}
	}

	private void parsePreparsed(String name, String u, String pX, String pY,
	                            String pZ, String acc) {
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
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Drp_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Pck_Pack ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Pck_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("DEKORATER: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("KOS: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Use ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("- spravne heslo - ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("- Zmena hesla na ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("otevrel zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("pokus otevrit zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("prevedl vlastnictvi zabezpecene nadoby ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("prevedl vlastnictvi zabezpecene nadoby ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("splatil dluh lecitelum ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("EqTest ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("UnEq ")){
			if(log.wl.startsWith("UnEq Pamet promen ") || log.wl.startsWith("UnEq metamorfoza ")){
				sender.rename(uid,name);
			}

			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Mount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("DisMount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("SpEff_New ")){
			// TODO
		}else if(log.wl.startsWith("SpEff ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("+ ")){
			// TODO
		}else if(log.wl.startsWith("Hit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("GHit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Killed ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("U_CntMnu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Ability ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.equals("pouziva HELP ROOM")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("skillgain ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.matches("Sk[SF][tua] .*")){
				sender.sendMessage(name,uid,acc,new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.equals("-> OdLogZona")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,0,0,0));
			// TODO
		}else if(log.wl.startsWith("LogIn ")){
			sender.sendMessage(name, uid, acc, new Message(timeParser.getCalendar(),log.wl,MessageType.AT));
			// TODO
		}else if(log.wl.startsWith("LogOut ")){
			sender.sendMessage(name, uid, acc, new Message(timeParser.getCalendar(),log.wl,MessageType.AT));
			// TODO
		}else if(log.wl.contains("AAFKK")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("check_num ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("Bankovni ucet ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
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
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("GuildSystem: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else if(log.wl.startsWith("prelevani ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(timeParser.getCalendar(),log.wl,posX,posY,posZ));
			// TODO
		}else{
			log.unknownLine("parseStandartLinePreparsed");
		}



	}



	private void throwAway() {
		// TODO Auto-generated method stub

	}

	private Matcher match_MAJITEL = Pattern.compile("[^(0:-]+ \\(majitel\\) \\(.*").matcher("");
	private Matcher match_POSXY = Pattern.compile("0[0-9a-f]+/[0-9]+,[0-9]+\\).*").matcher("");
	private Matcher match_POSXYZ = Pattern.compile("0[0-9a-f]+/[0-9]+,[0-9]+,[0-9-]+\\).*").matcher("");
	private Matcher match_JUSTUID = Pattern.compile("0[0-9a-f]+\\).*").matcher("");
	private Matcher match_JUSTACC = Pattern.compile("[^)]+\\).*").matcher("");
	private Matcher match_JUSTPOSXY = Pattern.compile(" [0-9]+,[0-9]+").matcher("");
	private Matcher match_JUSTPOSXYZ = Pattern.compile(" [0-9]+,[0-9]+,[0-9-]+").matcher("");
	private void parseCommon() {
		if (match_MAJITEL.reset(log.wl).matches()) {
			log.wl.replaceFirst("\\(majitel\\) ", ""); // TODO: WA
		}
		String name = log.wl.getUntil(" (");
		if (match_POSXY.reset(log.wl).matches()) {
			String uid = log.wl.getUntil("/");
			String posX = log.wl.getUntil(",");
			String posY = log.wl.getUntil(")");
			parsePreparsed(name, uid, posX, posY, "0", null);
		} else if (match_POSXYZ.reset(log.wl).matches()) {
			String uid = log.wl.getUntil("/");
			String posX = log.wl.getUntil(",");
			String posY = log.wl.getUntil(",");
			String posZ = log.wl.getUntil(")");
			parsePreparsed(name, uid, posX, posY, posZ, null);
		} else if (match_JUSTUID.reset(log.wl).matches()) {
			String uid = log.wl.getUntil(")");
			parsePreparsed(name, uid, null, null, null, null);
		} else if (match_JUSTACC.reset(log.wl).matches()) {
			String acc = log.wl.getUntil(")");
			String posX = null, posY = null, posZ = null;
			if (match_JUSTPOSXY.reset(log.wl).matches()) {
				log.wl.substring(1);
				posX = log.wl.getUntil(",");
				posY = log.wl.rest();
			} else if (match_JUSTPOSXYZ.reset(log.wl).matches()) {
				log.wl.substring(1);
				posX = log.wl.getUntil(",");
				posY = log.wl.getUntil(",");
				posZ = log.wl.rest();
			}
			parsePreparsed(name, null, posX, posY, posZ, acc);
		} else {
			log.unknownLine("parseStandartLine Standart");
		}
	}

	private Matcher match_UIDANDACC = Pattern.compile("0[0-9a-f]+ \\([^)]+\\).*").matcher("");
	private void parseJustUidAndAcc() {
		String name = log.wl.getUntilAndKeep(" 0");
		log.wl.substring(1);
		if (match_UIDANDACC.reset(log.wl).matches()) {
			String uid = log.wl.getUntil(" ");
			log.wl.substring(1);
			String acc = log.wl.getUntilAndKeep(") ");
			log.wl.substring(1);
			parsePreparsed(name, uid, null, null, null, acc);
		} else {
			log.unknownLine("parseStandartLine Uid");
		}
	}

}

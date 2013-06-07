package eu.janinko.Andaria.logparser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.janinko.Andaria.logparser.messages.LocatedMessage;
import eu.janinko.Andaria.logparser.messages.Message;
import eu.janinko.Andaria.logparser.messages.TargetedCommand;


public class Parser {
	BufferedReader in;
	MessageSender sender;
	String oline;
	String line;
	int lineNumber;
	
	Calendar c;
	
	public static void main(String[] args){		
		try {
			Parser p = new Parser("sphere2012-03-19.log");
			p.parse();
			
			p.sender.print();
			p.stop();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Parser(String filename) throws FileNotFoundException{
		if(filename.matches(".*20[0-9]{2}-[0-9]{2}-[0-9]{2}.*")){
			//TODO
		}
		in = new BufferedReader(new FileReader(filename));
		sender = new MessageSender();

		Thread t = new Thread(sender);
		t.start();
	}
	
	public void stop(){
		sender.running = false;
	}
	
	
	public void parse() throws IOException{
		String line;
		lineNumber=0;
		do{
			line = in.readLine();
			if(line != null){
				parseLine(line);
			}
			if(lineNumber % 10000 == 0){
				System.out.print('.');
				if(lineNumber % 100000 == 0){
					System.out.print(' ');
				}
			}
		}while(line != null);
	}
	
	Matcher match_lSAVE = Pattern.compile("^(GC:|(World|Player|Multi|Context) data saved|World save completed).*").matcher("");
	public void parseLine(String l){
		oline = l;
		line = l;
		lineNumber++;
		try{
			parseTime();
			
			if(line.startsWith(" POZOR ")){
				line = line.substring(7);
				parseMessageLine("POZOR");
			}else if(line.startsWith(" ")){
				line = line.substring(1);
				parseStandartLine();
			}else if(line.startsWith(":")){
				line = line.substring(1);
				parseSphereLine();
			}else if(line.startsWith("ERROR:")){	//check for ERROR line
				line = line.substring(6);
				parseMessageLine("ERROR");
			}else if(line.startsWith("DEBUG:")){	//check for DEBUG line
				line = line.substring(6);
				parseMessageLine("DEBUG");
			}else if(line.startsWith("WARNING:")){	//check for WARNING line
				line = line.substring(8);
				parseMessageLine("WARNING");
			}else if(line.startsWith("CRITICAL:")){	//check for CRITICAL line
				line = line.substring(9);
				parseMessageLine("CRITICAL");
			}else if(line.startsWith("GM Page")){	//check for GM Page line
				line = line.substring(7);
				parsePageLine();
			}else if(line.startsWith("'")){	//check for ' line
				parseSphereLine();
			}else if(line.startsWith("P'")){	//check for Player line
				parsePlayerLine();
			}else if(line.startsWith("A'")){	//check for Account line
				parseAccountLine();
			}else if(match_lSAVE.reset(line).matches()){	//check for SAVE line
				return;
			}else{
				unknownLine();
			}
		}catch(StringIndexOutOfBoundsException ex){
			unknownLine("Too short!");
		}catch(InvalidPlayersState ex){
			unknownLine("InvalidPlayersState: " + ex.getMessage());
		}
	}
	

	Matcher match_tFIRSTLINE = Pattern.compile("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.*").matcher("");
	Matcher match_tHOURMINUTE = Pattern.compile("^[0-9][0-9]:[0-9][0-9]:.*").matcher("");
	Matcher match_tSCRIPTNAME = Pattern.compile("\\([a-zA-Z_-]+\\.scp,[0-9]+\\).*").matcher("");
	Matcher match_tSECONDS = Pattern.compile("^[0-5]{0,1}[0-9] .*").matcher("");
	Matcher match_tWEIRDSTUFF = Pattern.compile("^[0-9a-f]{3}:.*").matcher("");
	private void parseTime() {
		
		// firstline date
		if(match_tFIRSTLINE.reset(line).matches()){
			int year=line.codePointAt(0)*1000 + line.codePointAt(1)*100 + line.codePointAt(2)*10 + line.codePointAt(3) - '0'*1111 ;
			int month=line.codePointAt(5)*10+line.codePointAt(6) - '0'*11;
			int day=line.codePointAt(8)*10+line.codePointAt(9) - '0'*11;
			
			if(c == null){
				c = new GregorianCalendar(year, month-1,day, 0, 0);
			}else if(c.get(Calendar.YEAR) != year || c.get(Calendar.MONTH)+1 != month || c.get(Calendar.DAY_OF_MONTH) != day){
				unknownLine("Wrong date: " + year + "-" + month + "-" + day + " x "
			                 + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH));
			}
			line = line.substring(19);
		}
		
		int hour = -1;
		int minute = -1;
		int second = -1;
	
		// chceck hour and minute
		if(match_tHOURMINUTE.reset(line).matches()){	//check time format
			hour=line.codePointAt(0)*10+line.codePointAt(1) - '0'*11;
			minute=line.codePointAt(3)*10+line.codePointAt(4) - '0'*11;
			line = line.substring(6);
		}else{
			unknownLine("Wrong time format");
		}

		// check for (*.scp,123) substr and remove if present
		if(match_tSCRIPTNAME.reset(line).matches()){
			line = line.replaceFirst("\\([a-zA-Z_-]+\\.scp,[0-9]+\\)", "");
		}
		
		// check for seconds
		if(match_tSECONDS.reset(line).matches()){
			second=line.charAt(0) - '0';
			
			if(line.charAt(1) >= '0' && line.charAt(1) <= '9'){
				second*=10;
				second+=line.charAt(1) - '0';
				line = line.substring(3);
			}else{
				line = line.substring(2);
			}
		} //check if there are the weird stuff
		else if(match_tWEIRDSTUFF.reset(line).matches()){
			line = line.substring(3);
		}
		
		setTime(hour, minute, second);
	}
	
	private void setTime(int hour, int minute, int second){
		if(hour <0 || hour >= 60 || minute <0 || minute >= 60 || second < -1 || second >= 60 ){
			throw new IllegalArgumentException();
		}
		if(second == -1){
			setTime(hour, minute);
		}else{
			if(hour <= c.get(Calendar.HOUR_OF_DAY) &&  minute <= c.get(Calendar.MINUTE) && second < c.get(Calendar.SECOND) ){
				unknownLine("Wrong time: " + hour + ":" + minute + ":" + second + " x "
			                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
			}else{
				setTime(hour, minute);
				c.set(Calendar.SECOND, second);
			}
		}
	}
	
	private void setTime(int hour, int minute){
		
		if(hour < c.get(Calendar.HOUR_OF_DAY)){
			unknownLine("Wrong time: " + hour + ":" + minute + " x "
		                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
		}else if(hour == c.get(Calendar.HOUR_OF_DAY) && minute < c.get(Calendar.MINUTE)){
			unknownLine("Wrong time: " + hour + ":" + minute + " x "
	                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
		}else{
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, 0);
		}
	}

	
	Matcher match_alDISCONNECTED = Pattern.compile("^A'.*DISCONNECTed by 'Rafael'$").matcher("");
	private void parseAccountLine() {
		if(!match_alDISCONNECTED.reset(line).matches()){
			unknownLine("parseAccountLine");
		}
		//TODO
	}


	Matcher match_plKILLEDBY = Pattern.compile("^P'.*' was killed by .*").matcher("");
	private void parsePlayerLine() {
		if(!match_plKILLEDBY.reset(line).matches()){
			unknownLine("parsePlayerLine");
		}		
		//TODO
	}



	private void parsePageLine() {
		// TODO Auto-generated method stub
		
	}



	private void parseMessageLine(String message) {
		// TODO Auto-generated method stub
		
	}


	Matcher match_slCLIENTCONNECTION = Pattern.compile("Client (dis){0,1}connected.*").matcher("");
	Matcher match_slBADPASS = Pattern.compile(".*bad password$").matcher("");
	private void parseSphereLine() {
		if(line.contains(" Says ")){
			parseSaysLine();
		}else if(line.contains(" commands ")){
			parseCommandsLine();
		}else if(line.contains(" tweak ")){
			parseCommandsLine();
		}else if(line.contains(" KILLed ")){
			//parseCommandsLine(); //TODO
		}else if(match_slCLIENTCONNECTION.reset(line).matches()){
			parseConnectLine();
		}else if(line.startsWith("Login ")){
			parseConnectLine();
		}else if(line.startsWith("Setup_")){
			parseConnectLine();
		}else if(line.startsWith("ERR ")){
			//
		}else if(match_slBADPASS.reset(line).matches()){
			parseConnectLine();
		}else{
			unknownLine("parseSphereLine");
		}
	}



	private void parseConnectLine() {
		// TODO Auto-generated method stub
		
	}


	Matcher match_clSIMPLECOMMAND = Pattern.compile("'.*'=[01]").matcher("");
	Matcher match_clUIDMMAND = Pattern.compile("uid=0[0-9a-f]+ \\([^)]*\\) to '.*'=[01]").matcher("");
	Matcher match_clAMOUNTCOMMAND = Pattern.compile("uid=0[0-9a-f]+ \\([^)]*\\) \\[amount=[0-9]+\\] to '.*'=[01]").matcher("");
	private void parseCommandsLine() {
		if(line.charAt(0) == '\''){
			line = line.substring(1);
		}else{
			unknownLine("parseSaysLine"); return;
		}
		String acc;
		MessageType type;
		boolean typeb;
		if(line.contains("commands")){
			acc = getUntil("' commands ");
			line = line.substring(10);
			type = MessageType.Command;
			typeb=false;
		}else{
			acc = getUntil("' tweak ");//TODO odlisit command a tweak?
			line = line.substring(7);
			type = MessageType.Tweak;
			typeb=true;
		}
		String command;
		if(match_clSIMPLECOMMAND.reset(line).matches()){
			command = line.substring(1).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new Message(c,command,type));
		}else if(match_clUIDMMAND.reset(line).matches()){
			line=line.substring(4);
			String uid = getUntil(" (");
			line=line.substring(1);
			String name = getUntil(") ");
			command=line.substring(5).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new TargetedCommand(c,command,Integer.parseInt(uid,16),name,0,typeb));
		}else if(match_clAMOUNTCOMMAND.reset(line).matches()){
			line=line.substring(4);
			String uid = getUntil(" (");
			line=line.substring(1);
			String name = getUntil(") ");
			line=line.substring(9);
			String amount = getUntil("] ");
			command=line.substring(5).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new TargetedCommand(c,command,Integer.parseInt(uid,16),name,Integer.parseInt(amount),typeb));
		}else{
			unknownLine("parseSaysLine"); return;
		}
	}



	private void parseSaysLine() {
		StringBuilder sb;
		boolean ok;
		int pos=0;
		
		if(line.charAt(pos) != '\''){
			unknownLine("parseSaysLine"); return;
		}pos++;
		
		sb = new StringBuilder();
		ok=true;
		while(ok){
			if(line.charAt(pos) == '\''){
				if(line.charAt(pos+1) == ' ' && line.charAt(pos+2) == 'S'
					&& line.charAt(pos+3) == 'a' && line.charAt(pos+4) == 'y'
					&& line.charAt(pos+5) == 's' && line.charAt(pos+6) == ' '){
					ok = false;
				}else{
					sb.append(line.charAt(pos));
				}
			}else{
				sb.append(line.charAt(pos));
			}
			pos++;
		}
		String name = sb.toString();
		line = line.substring(pos);pos = 0;
		if(line.matches(" Says (UNICODE ('[A-Z]{3}'|'') ){0,1}'.*")){
			line = line.replaceFirst(" Says (UNICODE '[A-Z]{3}' ){0,1}'", "");
		}else{
			unknownLine("parseSaysLine"); return;
		}
		
		sb = new StringBuilder();
		ok = true;
		while(ok){
			if(line.charAt(pos) == '\''){
				if(line.charAt(pos+1) == ' ' && line.charAt(pos+2) == 'm'
					&& line.charAt(pos+3) == 'o' && line.charAt(pos+4) == 'd'
					&& line.charAt(pos+5) == 'e' && line.charAt(pos+6) == '='){
					ok = false;
				}else if(line.charAt(pos+1) == ' ' && line.charAt(pos+2) == 'i'
				    && line.charAt(pos+3) == 'n' && line.charAt(pos+4) == ' '
					&& line.charAt(pos+5) == 'p' && line.charAt(pos+6) == 'a'
					&& line.charAt(pos+7) == 'r' && line.charAt(pos+8) == 't'
					&& line.charAt(pos+9) == 'y' && line.charAt(pos+10) == ' '){
					ok = false;
				}
			}else{
				sb.append(line.charAt(pos));
			}
			pos++;
		}
		String message = sb.toString();
		line = line.substring(pos);pos = 0;

		if(line.matches(" mode=[0-9]")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSays));
		}else if(line.matches(" mode=[0-9] \\(muted\\)")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSaysMuted));
		}else if(line.matches(" mode=[0-9].*")){
			unknownLine("parseSaysLine"); return;	
		}else if(line.matches(" in party to 'all'")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSaysParty));
		}else if(line.matches(" in party to '.*")){
			//TODO
		}else{
			unknownLine("parseSaysLine"); return;
		}

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
	private void parseStandartLine() {
		if(line.length() == 0){
			// TODO
		}else if(match_stlCOMMON.reset(line).matches()){
			if(match_stlMAJITEL.reset(line).matches()){
				line = line.replaceFirst("\\(majitel\\) ", ""); // TODO: WA
			}
			String name = getUntil(" (");
			if(match_stlPOSXY.reset(line).matches()){
				line = line.substring(1);
				String uid = getUntil("/");
				String posX = getUntil(",");
				String posY = getUntil(")");
				parseStandartLinePreparsed(name, uid, posX, posY, "0", null);
			}else if(match_stlPOSXYZ.reset(line).matches()){
				line = line.substring(1);
				String uid = getUntil("/");
				String posX = getUntil(",");
				String posY = getUntil(",");
				String posZ = getUntil(")");
				parseStandartLinePreparsed(name, uid, posX, posY, posZ, null);
			}else if(match_stlJUSTUID.reset(line).matches()){
				line = line.substring(1);
				String uid = getUntil(")");
				parseStandartLinePreparsed(name, uid, null, null, null, null);
			}else if(match_stlJUSTACC.reset(line).matches()){
				line = line.substring(1);
				String acc = getUntil(")");
				String posX = null, posY = null, posZ = null;
				if(match_stlJUSTPOSXY.reset(line).matches()){
					line = line.substring(1);
					posX = getUntil(",");
					posY = line;
					line = "";
				}else if(match_stlJUSTPOSXYZ.reset(line).matches()){
					line = line.substring(1);
					posX = getUntil(",");
					posY = getUntil(",");
					posZ = line;
					line = "";
				}
				parseStandartLinePreparsed(name, null, posX, posY, posZ, acc);
			}else{
				unknownLine("parseStandartLine Standart");
			}
		}else if(match_stlJUSTUIDANDACC.reset(line).matches()){
			String name = getUntil(" 0");
			if(match_stlUIDANDACC.reset(line).matches()){
				String uid = getUntil(" ");
				line = line.substring(1);
				String acc = getUntil(") ");
				parseStandartLinePreparsed(name, uid, null, null, null, acc);
			}else{
				unknownLine("parseStandartLine Uid");
			}
		}else if(match_stlCASTSPECIAL.reset(line).matches()){
			String name = getUntil(" - Cast");
			line = line.substring(14);
			String spelnum;
			if(line.matches("[0-9a-f]+")){
				spelnum = line;
			}else if(line.equals("s_lightning_new")){
				spelnum = "s_lightning_new";
			}else if(line.equals("s_flamestrike_new")){
				spelnum = "s_flamestrike_new";
			}else if(line.equals("s_ice_storm_new")){
				spelnum = "s_ice_storm_new";
			}else if(line.equals("s_magic_arrow_new")){
				spelnum = "s_magic_arrow_new";
			}else if(line.matches("[0-9]{3}_kouzla")){
				spelnum = "719_kouzla";
			}else{
				unknownLine("parseStandartLine CastSpecial");
			}// TODO
			//System.out.println(line + " --- " + name + " CS " + spelnum);
		}else if(line.startsWith("Banka - ")){
			// TODO
		}else if(line.startsWith("** Casove scripty")){
			// TODO
		}else if(line.startsWith("### Mnozeni koni - ")){
			// TODO
		}else if(line.contains("pouziva Spojovac na")){
			String name = getUntil("(");
			// TODO
		}else if(line.startsWith("AUTORESTOCK: ")){
			// TODO
		}else if(line.startsWith("GM Chat: ")){
			// TODO
		}else if(line.startsWith("GALEJE: ")){
			// TODO
		}else if(line.startsWith("UTOK NA NPC: ")){
			// TODO
		}else if(line.startsWith("QUESTY: Postava ")){
			// TODO
		}else if(line.startsWith("SOGURKIN - NAKUP SOSEK: ")){
			// TODO
		}else if(line.startsWith("Konske pripusteni: ")){
			// TODO
		}else if(line.equals("Zacina svitat")){
			// TODO
		}else if(line.equals("Slunce zacina zapadat")){
			// TODO
		}else if(line.contains("Global message")){
			// TODO
		}else{
			unknownLine("parseStandartLine");
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

		if(line.charAt(0) == ':') line = line.substring(1);
		if(line.charAt(0) != ' '){
			unknownLine("parseStandartLinePreparsed notspace"); return;
		}
		line = line.substring(1);
		
		if(line.startsWith("Drp_Itm ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Drp_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Pck_Pack ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Pck_Gr ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("DEKORATER: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("KOS: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Use ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("- spravne heslo - ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("- Zmena hesla na ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("otevrel zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("pokus otevrit zabezpecenou nadobu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("prevedl vlastnictvi zabezpecene nadoby ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("EqTest ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("UnEq ")){
			if(line.startsWith("UnEq Pamet promen ") || line.startsWith("UnEq metamorfoza ")){
				sender.rename(uid,name);
			}
			
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Mount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("DisMount ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("SpEff_New ")){
			// TODO
		}else if(line.startsWith("SpEff ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("+ ")){
			// TODO
		}else if(line.startsWith("Hit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("GHit ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Killed ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("U_CntMnu ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Ability ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.equals("pouziva HELP ROOM")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("skillgain ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.matches("Sk[SF][tua] .*")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.equals("-> OdLogZona")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,0,0,0,MessageType.Located));
			// TODO
		}else if(line.startsWith("LogIn ")){
			sender.sendMessage(name, uid, acc, new Message(c,line,MessageType.AT));
			// TODO
		}else if(line.startsWith("LogOut ")){
			sender.sendMessage(name, uid, acc, new Message(c,line,MessageType.AT));
			// TODO
		}else if(line.contains("AAFKK")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("check_num ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Bankovni ucet ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("Incognito: ")){
			line = line.substring(11);
			MessageType t;
			if(line.charAt(0) == 'z' && line.charAt(1) == ' ' ){
				line = line.substring(2);
				t = MessageType.IncognitoReset;
			}else if(line.charAt(0) == 'n' && line.charAt(1) == 'a' && line.charAt(2) == ' ' ){
				line = line.substring(3);
				t = MessageType.IncognitoSet;
			}else{
				unknownLine("parseStandartLinePreparsed Incognito"); return;
			}
			if(line.length() == 0){
				throwAway(); return;
			}
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,t));
		}else if(line.startsWith("Opravuje ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("GuildSystem: ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else if(line.startsWith("prelevani ")){
			sender.sendMessage(name, uid, acc, new LocatedMessage(c,line,posX,posY,posZ,MessageType.Located));
			// TODO
		}else{
			unknownLine("parseStandartLinePreparsed"); return;
		}
		
	
		
	}

	private void throwAway() {
		// TODO Auto-generated method stub
		
	}

	public void unknownLine(){
		unknownLine("");
	}
	
	public void unknownLine(String message){
		if(message.equals("Too short!")) return;
		System.err.println("Unknown input, line " + lineNumber +":  " + message);
		System.err.println(oline);
		for(int i=line.length(), j=oline.length(); i<j; i++){
			System.err.print(' ');
		}
		System.err.println("^");
	}
	
	
	public String getUntil(String s){
		StringBuilder sb = new StringBuilder();
		boolean ok = true;
		int pos = 0;
		char zn = s.charAt(0);
		int slen = s.length();
		while(ok){
			if(line.charAt(pos) == zn){
				ok = false;
				for(int i=1; i<slen; i++){
					if(line.charAt(pos+i) != s.charAt(i)){
						sb.append(zn);
						ok=true;
						break;
					}
				}
			}else{
				sb.append(line.charAt(pos));
			}
			pos++;
		}
		line = line.substring(pos);
		return sb.toString();
	}

	public MessageSender getSender() {
		return sender;
	}
}
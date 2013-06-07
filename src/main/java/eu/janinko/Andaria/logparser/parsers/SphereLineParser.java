package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;
import eu.janinko.Andaria.logparser.MessageType;
import eu.janinko.Andaria.logparser.messages.Message;
import eu.janinko.Andaria.logparser.messages.TargetedCommand;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author janinko
 */
public class SphereLineParser implements Parser{
	private Log log;
	private MessageSender sender;

	private Calendar c; // REMOVE!!

	
	public SphereLineParser(Log log, MessageSender sender) {
		this.log = log;
		this.sender = sender;
	}
	
	Matcher match_slCLIENTCONNECTION = Pattern.compile("Client (dis){0,1}connected.*").matcher("");
	Matcher match_slBADPASS = Pattern.compile(".*bad password$").matcher("");
	@Override
	public void parseLine() {
		if (log.wl.contains(" Says ")) {
			parseSaysLine();
		} else if (log.wl.contains(" commands ")) {
			parseCommandsLine();
		} else if (log.wl.contains(" tweak ")) {
			parseCommandsLine();
		} else if (log.wl.contains(" KILLed ")) {
			//parseCommandsLine(); //TODO
		} else if (match_slCLIENTCONNECTION.reset(log.wl).matches()) {
			parseConnectLine();
		} else if (log.wl.startsWith("Login ")) {
			parseConnectLine();
		} else if (log.wl.startsWith("Setup_")) {
			parseConnectLine();
		} else if (log.wl.startsWith("ERR ")) {
			//
		} else if (match_slBADPASS.reset(log.wl).matches()) {
			parseConnectLine();
		} else {
			log.unknownLine("parseSphereLine");
		}
	}

	private void parseSaysLine() {
		StringBuilder sb;
		boolean ok;
		int pos=0;

		if(log.wl.charAt(pos) != '\''){
			log.unknownLine("parseSaysLine"); return;
		}pos++;

		sb = new StringBuilder();
		ok=true;
		while(ok){
			if(log.wl.charAt(pos) == '\''){
				if(log.wl.startsWith(" Says ",pos+1)){
					ok = false;
				}else{
					sb.append(log.wl.charAt(pos));
				}
			}else{
				sb.append(log.wl.charAt(pos));
			}
			pos++;
		}
		String name = sb.toString();
		log.wl = log.wl.substring(pos);pos = 0;
		if(log.wl.matches(" Says (UNICODE ('[A-Z]{3}'|'') ){0,1}'.*")){
			log.wl = log.wl.replaceFirst(" Says (UNICODE '[A-Z]{3}' ){0,1}'", "");
		}else{
			log.unknownLine("parseSaysLine"); return;
		}

		sb = new StringBuilder();
		ok = true;
		while(ok){
			if(log.wl.charAt(pos) == '\''){
				if(log.wl.startsWith(" mode=",pos+1)){
					ok = false;
				}else if(log.wl.startsWith(" in party ", pos+1)){
					ok = false;
				}
			}else{
				sb.append(log.wl.charAt(pos));
			}
			pos++;
		}
		String message = sb.toString();
		log.wl = log.wl.substring(pos);pos = 0;

		if(log.wl.matches(" mode=[0-9]")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSays));
		}else if(log.wl.matches(" mode=[0-9] \\(muted\\)")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSaysMuted));
		}else if(log.wl.matches(" mode=[0-9].*")){
			log.unknownLine("parseSaysLine"); return;
		}else if(log.wl.matches(" in party to 'all'")){
			sender.sendMessage(name, null,null, new Message(c,message,MessageType.PlayerSaysParty));
		}else if(log.wl.matches(" in party to '.*")){
			//TODO
		}else{
			log.unknownLine("parseSaysLine"); return;
		}

	}




	Matcher match_clSIMPLECOMMAND = Pattern.compile("'.*'=[01]").matcher("");
	Matcher match_clUIDMMAND = Pattern.compile("uid=0[0-9a-f]+ \\([^)]*\\) to '.*'=[01]").matcher("");
	Matcher match_clAMOUNTCOMMAND = Pattern.compile("uid=0[0-9a-f]+ \\([^)]*\\) \\[amount=[0-9]+\\] to '.*'=[01]").matcher("");
	private void parseCommandsLine() {
		if(log.wl.charAt(0) == '\''){
			log.wl = log.wl.substring(1);
		}else{
			log.unknownLine("parseSaysLine"); return;
		}
		String acc;
		MessageType type;
		boolean typeb;
		if(log.wl.contains("commands")){
			acc = log.getUntil("' commands ");
			log.wl = log.wl.substring(10);
			type = MessageType.Command;
			typeb=false;
		}else{
			acc = log.getUntil("' tweak ");//TODO odlisit command a tweak?
			log.wl = log.wl.substring(7);
			type = MessageType.Tweak;
			typeb=true;
		}
		String command;
		if(match_clSIMPLECOMMAND.reset(log.wl).matches()){
			command = log.wl.substring(1).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new Message(c,command,type));
		}else if(match_clUIDMMAND.reset(log.wl).matches()){
			log.wl=log.wl.substring(4);
			String uid = log.getUntil(" (");
			log.wl=log.wl.substring(1);
			String name = log.getUntil(") ");
			command=log.wl.substring(5).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new TargetedCommand(c,command,Integer.parseInt(uid,16),name,0,typeb));
		}else if(match_clAMOUNTCOMMAND.reset(log.wl).matches()){
			log.wl=log.wl.substring(4);
			String uid = log.getUntil(" (");
			log.wl=log.wl.substring(1);
			String name = log.getUntil(") ");
			log.wl=log.wl.substring(9);
			String amount = log.getUntil("] ");
			command=log.wl.substring(5).replaceFirst("'=[01]$", "");
			sender.sendMessage(null, null, acc, new TargetedCommand(c,command,Integer.parseInt(uid,16),name,Integer.parseInt(amount),typeb));
		}else{
			log.unknownLine("parseSaysLine"); return;
		}
	}



	private void parseConnectLine() {
		// TODO Auto-generated method stub

	}



}

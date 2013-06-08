package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author janinko
 */
public class PlayerLineParser implements Parser{

	private Log log;
	private MessageSender sender;

	public PlayerLineParser(Log log, MessageSender sender) {
		this.log = log;
		this.sender = sender;
	}


	Matcher match_plKILLEDBY = Pattern.compile("^P'.*' was killed by .*").matcher("");
	@Override
	public void parseLine() {
		if(!match_plKILLEDBY.reset(log.wl).matches()){
			log.unknownLine("parsePlayerLine");
		}
		//TODO
	}


}

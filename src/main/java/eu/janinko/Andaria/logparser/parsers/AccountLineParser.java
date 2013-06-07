package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author janinko
 */
public class AccountLineParser implements Parser{

	private Log log;

	private Calendar c; // REMOVE!!

	public AccountLineParser(Log log) {
		this.log = log;
	}

	Matcher match_alDISCONNECTED = Pattern.compile("^A'.*DISCONNECTed by 'Rafael'$").matcher("");
	@Override
	public void parseLine() {
		if(!match_alDISCONNECTED.reset(log.wl).matches()){
			log.unknownLine("parseAccountLine");
		}
		//TODO
	}
}

package eu.janinko.Andaria.logparser.parsers;

import eu.janinko.Andaria.logparser.MessageSender;
import java.util.Calendar;

/**
 *
 * @author janinko
 */
public class PageParser implements Parser{
	private Log log;
	private MessageSender sender;

	private Calendar c; // REMOVE!!


	public PageParser(Log log, MessageSender sender) {
		this.log = log;
		this.sender = sender;
	}

	@Override
	public void parseLine() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

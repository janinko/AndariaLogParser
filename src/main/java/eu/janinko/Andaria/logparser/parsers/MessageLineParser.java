package eu.janinko.Andaria.logparser.parsers;

/**
 *
 * @author janinko
 */
public class MessageLineParser implements Parser{
	private Log log;

	public MessageLineParser(Log log) {
		this.log = log;
	}

	@Override
	public void parseLine() {
	}

	public void parseLine(String s) {
	}

}

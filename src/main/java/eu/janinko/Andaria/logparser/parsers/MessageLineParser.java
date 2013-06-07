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
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void parseLine(String s) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

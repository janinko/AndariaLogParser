package eu.janinko.Andaria.logparser;

public class InvalidPlayersState extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public InvalidPlayersState() {
		super();
	}

	public InvalidPlayersState(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPlayersState(String s) {
		super(s);
	}

	public InvalidPlayersState(Throwable cause) {
		super(cause);
	}
}

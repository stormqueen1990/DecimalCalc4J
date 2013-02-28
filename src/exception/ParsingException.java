package exception;

public class ParsingException extends Exception {
	private static final long serialVersionUID = 1L;

	public ParsingException() {
		super();
	}

	public ParsingException(String message) {
		super(message);
	}
}

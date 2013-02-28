package exception;

public class BinaryOperandException extends Exception {
	private static final long serialVersionUID = 2128033787287507503L;

	public BinaryOperandException() {
		super();
	}

	public BinaryOperandException(String message) {
		super(message);
	}
}

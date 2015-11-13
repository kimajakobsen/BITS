package da.aau.kah.bits.exceptions;

public class InvalidFieldName extends Exception {
	public InvalidFieldName() { super(); }
	public InvalidFieldName(String message) { super(message); }
	public InvalidFieldName(String message, Throwable cause) { super(message, cause); }
	public InvalidFieldName(Throwable cause) { super(cause); }
}

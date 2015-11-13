package da.aau.kah.bits.exceptions;

public class InvalidFieldDatatype extends Exception {
  public InvalidFieldDatatype() { super(); }
  public InvalidFieldDatatype(String message) { super(message); }
  public InvalidFieldDatatype(String message, Throwable cause) { super(message, cause); }
  public InvalidFieldDatatype(Throwable cause) { super(cause); }
}

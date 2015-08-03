package da.aau.kah.bits.exceptions;

public class InvalidDatabaseConfig extends Exception {
  public InvalidDatabaseConfig() { super(); }
  public InvalidDatabaseConfig(String message) { super(message); }
  public InvalidDatabaseConfig(String message, Throwable cause) { super(message, cause); }
  public InvalidDatabaseConfig(Throwable cause) { super(cause); }
}

package edu.neu.ccs.cs5010.assignment5r.csvparser;

public class IncompleteRecordException extends Exception {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message.
   */
  public IncompleteRecordException(String message) {
    super(message);
  }
}

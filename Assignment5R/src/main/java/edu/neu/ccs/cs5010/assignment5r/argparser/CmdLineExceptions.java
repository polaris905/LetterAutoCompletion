package edu.neu.ccs.cs5010.assignment5r.argparser;

/**
 * Class to handle all Command Line Arguments related exceptions.
 */
public abstract class CmdLineExceptions extends RuntimeException {

  /**
   * The abstract class constructor..
   *
   * @param msg Error message
   */
  public CmdLineExceptions(String msg) {
    super(msg);
  }

  /**
   * This constructor prints the error message on comma Class to handle errors related to missing
   * command line arguments.
   */
  public static class MissingCommandException extends CmdLineExceptions {

    /**
     * This constructor passes the error message to its super class.
     *
     * @param msg Error message
     */
    public MissingCommandException(String msg) {
      super(msg);
    }
  }

  /**
   * Class to handle errors related to unnecessary arguments passed in command line arguments.
   */
  public static class ExtraArgumentException extends CmdLineExceptions {

    public ExtraArgumentException(String msg) {
      super(msg);
    }
  }


  public static class IllegalValueException extends CmdLineExceptions {

    public IllegalValueException(String msg) {
      super(msg);
    }
  }
}
package edu.neu.ccs.cs5010.assignment5r.argparser;

import edu.neu.ccs.cs5010.assignment5r.argparser.CmdLineExceptions.ExtraArgumentException;
import edu.neu.ccs.cs5010.assignment5r.argparser.CmdLineExceptions.IllegalValueException;
import edu.neu.ccs.cs5010.assignment5r.argparser.CmdLineExceptions.MissingCommandException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

/**
 * The type Arg parser. It will compare the user's input args to the rule of the given Options, and
 * then check if the user's input args is valid. If valid, initialized user's args. Otherwise,
 * throws a type of CmdLineExceptions.
 */
public class ArgParser {

  private static final String ENDS_REGEX = "[^\\w]*$";
  private static final String DIR_SEPARATOR_REGEX = "[\\|/]+";
  private Map<String, Option> result;
  private String[] args;
  private Options options;

  /**
   * Instantiates a new Arg parser.
   *
   * @param args the args
   * @param options the options
   */
  public ArgParser(String[] args, Options options) {
    this.result = new HashMap<>();
    this.args = args.clone();
    this.options = options;
  }

  /**
   * Parse user's input arguments according to the Options object. This method separates many check
   * algorithms into some helper private method, which makes it easy to modify some of the special
   * check algorithms.
   *
   * @return the parsed option arguments
   * @throws CmdLineExceptions the cmd line exceptions
   */
  public Map<String, Option> parse() throws CmdLineExceptions {
    for (int i = 0; i < args.length; i++) {
      this.checkValid(args, i);
      Option option = this.options.getTotal().get(args[i]);
      this.handleDuplicate(option);
      if (option.hasSubOption()) {
        this.handleSubOptions(option, args, i);
        this.result.put(args[i++], option);
      } else {
        this.result.put(args[i], option);
      }
    }
    this.checkRequired();
    this.checkOneOrMore();
    this.checkDependent();
    this.checkExclusive();
    return this.result;
  }

  /**
   * Helper method that checks if the user's input option exist in the Options object.
   *
   * @param args the user's input args
   * @param index the index of the args
   * @throws IllegalValueException the exception
   */
  private void checkValid(String[] args, int index) throws IllegalValueException {
    if (!this.options.getTotal().containsKey(args[index])) {
      throw new IllegalValueException(args[index] + " was not a valid option.");
    }
  }

  /**
   * Helper method that checks if the user's input required option is repeated.
   *
   * @param option the option of the user's input args
   * @throws ExtraArgumentException the exception
   */
  private void handleDuplicate(Option option) throws ExtraArgumentException {
    if (!option.isMultiple() && this.result.containsKey(option.getName())) {
      throw new ExtraArgumentException("\"" + option.getName() + "\" can not be repeated.");
    }
  }

  /**
   * Helper method that checks if the user's input option has sub-options.
   *
   * @param option the option of the user's input args
   * @param args the user's input args
   * @param index the index of the args
   * @throws MissingCommandException the exception
   * @throws IllegalValueException the exception
   */
  private void handleSubOptions(Option option, String[] args, int index)
      throws MissingCommandException, IllegalValueException {
    if (index + 1 == args.length) {
      throw new MissingCommandException(args[index] + " provided but sub-option missed.");
    }
    if (!args[index + 1].matches(option.getSubOptionRegex())) {
      throw new IllegalValueException(
          args[index] + " provided but sub-option missed or format was wrong.");
    }
    args[index + 1] = args[index + 1].replaceAll(ENDS_REGEX, "")
        .replaceAll(DIR_SEPARATOR_REGEX, Matcher.quoteReplacement(File.separator));
    option.getSubOptions().add(args[index + 1]);
  }

  /**
   * Helper method that checks if the user provides all the required options.
   *
   * @throws MissingCommandException the exception
   */
  private void checkRequired() throws MissingCommandException {
    StringBuilder builder = new StringBuilder();
    for (String name : this.options.getRequired().keySet()) {
      if (!this.result.containsKey(name)) {
        builder.append(" ").append(name);
      }
    }
    if (builder.length() != 0) {
      throw new MissingCommandException("Required options missed:" + builder.toString() + ".");
    }
  }

  /**
   * Helper method that checks if the user provides at least one of the one or more optional
   * options.
   *
   * @throws MissingCommandException the exception
   */
  private void checkOneOrMore() throws MissingCommandException {
    for (List<String> oneOrMore : this.options.getOneOrMoreList()) {
      boolean exist = false;
      for (String arg : oneOrMore) {
        if (this.result.containsKey(arg)) {
          exist = true;
          break;
        }
      }
      if (!exist) {
        throw new MissingCommandException("One or more optional options missed.");
      }
    }
  }

  /**
   * Helper method that checks if the user provides all the dependent options of all the options.
   *
   * @throws MissingCommandException the exception
   */
  private void checkDependent() throws MissingCommandException {
    for (Entry<String, Option> entry : this.result.entrySet()) {
      if (entry.getValue().getDependent() != null) {
        for (String name : entry.getValue().getDependent()) {
          if (!this.result.containsKey(name)) {
            throw new MissingCommandException(
                entry.getKey() + " was provided but " + name + " missed.");
          }
        }
      }
    }
  }

  /**
   * Helper method that checks if the user provides any exclusive options of any given options..
   *
   * @throws ExtraArgumentException the exception
   */
  private void checkExclusive() throws ExtraArgumentException {
    for (Entry<String, Option> entry : this.result.entrySet()) {
      if (entry.getValue().getExclusive() != null) {
        for (String name : entry.getValue().getExclusive()) {
          if (this.result.containsKey(name)) {
            throw new ExtraArgumentException(
                entry.getKey() + " and " + name + " are exclusive options.");
          }
        }
      }
    }
  }
}
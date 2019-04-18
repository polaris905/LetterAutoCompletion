package edu.neu.ccs.cs5010.assignment5r.argparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Options that holds and manages all Option objects. This class represents the rule of the
 * whole Option objects.
 */
public class Options {

  private Map<String, Option> total;
  private Map<String, Option> required;
  private List<List<String>> oneOrMoreList;
  private List<String> example;
  private String usage;

  /**
   * Instantiates a new Options.
   */
  public Options() {
    this.total = new HashMap<>();
    this.required = new HashMap<>();
    this.oneOrMoreList = new ArrayList<>();
    this.example = new ArrayList<>();
    this.usage = "";
  }

  /**
   * Add an Option object.
   *
   * @param option the option
   */
  public void addOption(Option option) {
    this.total.put(option.getName(), option);
    if (option.isRequired()) {
      this.required.put(option.getName(), option);
    }
  }

  /**
   * Gets total Option objects.
   *
   * @return the total
   */
  public Map<String, Option> getTotal() {
    return this.total;
  }

  /**
   * Gets required Option objects.
   *
   * @return the required
   */
  public Map<String, Option> getRequired() {
    return this.required;
  }

  /**
   * Gets at least one or more Option objects list.
   *
   * @return the at least one or more Option objects list
   */
  public List<List<String>> getOneOrMoreList() {
    return this.oneOrMoreList;
  }

  /**
   * Add a bunch of at least one or more Option objects.
   *
   * @param oneOrMore the bunch of at least one or more Option objects
   */
  public void addOneOrMoreList(List<String> oneOrMore) {
    this.oneOrMoreList.add(oneOrMore);
  }

  /**
   * Add usage example of the Options.
   *
   * @param example the example
   */
  public void addExample(String example) {
    this.example.add(example);
  }

  /**
   * Gets usage example of the Options.
   *
   * @return the example
   */
  public List<String> getExample() {
    return this.example;
  }

  /**
   * Gets usage message of the Options.
   *
   * @return the usage message
   */
  public String getUsage() {
    return this.usage;
  }

  /**
   * Generate usage message of the Options.
   */
  public void generateUsage() {
    List<String> desc = new ArrayList<>();
    for (Option option : this.total.values()) {
      desc.add("  " + option.getName() + " " + option.getDesc());
    }
    Collections.sort(desc);
    StringBuilder builder = new StringBuilder();
    builder.append("Usage:\n");
    for (String str : desc) {
      builder.append(str + "\n");
    }
    builder.append("Example:\n");
    for (String example : this.example) {
      builder.append("  " + example + "\n");
    }
    this.usage = builder.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Options options = (Options) obj;
    return Objects.equals(total, options.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total);
  }

  @Override
  public String toString() {
    return "Options{" + "total=" + total.toString() + '}';
  }
}

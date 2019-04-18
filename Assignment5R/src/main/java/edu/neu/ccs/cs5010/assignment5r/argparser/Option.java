package edu.neu.ccs.cs5010.assignment5r.argparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type Option represents an argument option with some attributes. This class uses "Builder
 * Pattern" to build the Option object.
 */
public class Option {

  private String name;
  private String desc;
  private boolean required;
  private boolean multiple;
  private List<String> dependent;
  private List<String> exclusive;
  private boolean subOption;
  private String subOptionRegex;
  private List<String> subOptions;

  private Option(OptionBuilder builder) {
    this.name = builder.name;
    this.desc = builder.desc;
    this.required = builder.required;
    this.multiple = builder.multiple;
    this.dependent = builder.dependent;
    this.exclusive = builder.exclusive;
    this.subOption = builder.subOption;
    this.subOptionRegex = builder.subOptionRegex;
    this.subOptions = new ArrayList<>();
  }

  /**
   * Gets the option's name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the option's description.
   *
   * @return the description
   */
  public String getDesc() {
    return this.desc;
  }

  /**
   * Gets if the option is required.
   *
   * @return required or not
   */
  public boolean isRequired() {
    return this.required;
  }

  /**
   * Gets if the option can be multiple.
   *
   * @return multiple or not
   */
  public boolean isMultiple() {
    return this.multiple;
  }

  /**
   * Gets the option's dependent list.
   *
   * @return the dependent list
   */
  public List<String> getDependent() {
    return this.dependent;
  }

  /**
   * Gets the first dependent.
   *
   * @return the first dependent
   */
  public String getFirstDependent() {
    if (this.dependent != null || !this.dependent.isEmpty()) {
      return this.dependent.get(0);
    }
    return "";
  }

  /**
   * Gets the option's exclusive list.
   *
   * @return the exclusive list
   */
  public List<String> getExclusive() {
    return this.exclusive;
  }

  /**
   * Gets if the option has sub-option.
   *
   * @return true or false
   */
  public boolean hasSubOption() {
    return this.subOption;
  }

  /**
   * Gets the sub-option regex.
   *
   * @return the sub-option regex
   */
  public String getSubOptionRegex() {
    return this.subOptionRegex;
  }

  /**
   * Gets the sub-options list.
   *
   * @return the sub-options list
   */
  public List<String> getSubOptions() {
    return this.subOptions;
  }

  /**
   * Gets the first sub-option.
   *
   * @return the first sub-option
   */
  public String getFirstSubOption() {
    if (this.hasSubOption()) {
      return this.subOptions.get(0);
    }
    return "";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Option option = (Option) obj;
    return Objects.equals(this.toString(), option.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return "Option{" + "name='" + name + '\'' + ", desc='" + desc + '\'' + ", required=" + required
        + ", multiple=" + multiple + ", dependent=" + dependent + ", exclusive=" + exclusive
        + ", subOption=" + subOption + ", subOptionRegex='" + subOptionRegex + '\''
        + ", subOptions=" + subOptions + '}';
  }

  /**
   * The class of Option builder that used to build an Option object.
   */
  public static class OptionBuilder {

    private String name;
    private String desc;
    private boolean required;
    private boolean multiple;
    private List<String> dependent;
    private List<String> exclusive;
    private boolean subOption;
    private String subOptionRegex;

    /**
     * Instantiates a new Option builder.
     *
     * @param name the option's name
     */
    public OptionBuilder(String name) {
      this.name = name;
    }

    /**
     * Sets description.
     *
     * @param desc the description
     * @return the Option builder
     */
    public OptionBuilder setDesc(String desc) {
      this.desc = desc;
      return this;
    }

    /**
     * Sets if required.
     *
     * @return the Option builder
     */
    public OptionBuilder setRequired() {
      this.required = true;
      return this;
    }

    /**
     * Sets if multiple.
     *
     * @return the Option builder
     */
    public OptionBuilder setMultiple() {
      this.multiple = true;
      return this;
    }

    /**
     * Sets dependent list.
     *
     * @param dependent the dependent array
     * @return the Option builder
     */
    public OptionBuilder setDependent(String[] dependent) {
      this.dependent = Arrays.asList(dependent);
      return this;
    }

    /**
     * Sets exclusive list.
     *
     * @param exclusive the exclusive array
     * @return the Option builder
     */
    public OptionBuilder setExclusive(String[] exclusive) {
      this.exclusive = Arrays.asList(exclusive);
      return this;
    }

    /**
     * Sets if has sub-option.
     *
     * @return the Option builder
     */
    public OptionBuilder hasSubOption() {
      this.subOption = true;
      return this;
    }

    /**
     * Sets sub-option regex.
     *
     * @param regex the sub-option regex
     * @return the Option builder
     */
    public OptionBuilder setSubOptionRegex(String regex) {
      this.subOptionRegex = regex;
      return this;
    }

    /**
     * Build an Option object according to the attributes assigned.
     *
     * @return the completed Option object
     */
    public Option build() {
      return new Option(this);
    }
  }
}

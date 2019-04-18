package edu.neu.ccs.cs5010.assignment5r;

import edu.neu.ccs.cs5010.assignment5r.argparser.ArgParser;
import edu.neu.ccs.cs5010.assignment5r.argparser.CmdLineExceptions;
import edu.neu.ccs.cs5010.assignment5r.argparser.Option;
import edu.neu.ccs.cs5010.assignment5r.argparser.Option.OptionBuilder;
import edu.neu.ccs.cs5010.assignment5r.argparser.Options;
import edu.neu.ccs.cs5010.assignment5r.csvparser.CsvParser;
import edu.neu.ccs.cs5010.assignment5r.template.EmailAction;
import edu.neu.ccs.cs5010.assignment5r.template.LetterAction;
import edu.neu.ccs.cs5010.assignment5r.template.Template;

import edu.neu.ccs.cs5010.assignment5r.threadpool.ThreadPool;
import edu.neu.ccs.cs5010.assignment5r.timer.Timer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The type Mail generator that represents the main class of Assignment-5R. It will utilize the
 * classes from argparser, csvparser, template package to combine some objects together. The user
 * can customize Option rules in the static block and can free to modified the placeholder format,
 * dir format, file format, etc.
 */
public class MailGenerator {

  /**
   * The constant PLACEHOLDER_REGEX represents the format of the placeholder. e.g. "[[" and "]]".
   */
  private static final String PLACEHOLDER_REGEX = "\\[{2}[\\w-]+\\]{2}";
  /**
   * The constant MARK_LENGTH represents the left and right mark's length of the placeholder. e.g.
   * "[[" = 2, "]]" = 2.
   */
  private static final int[] MARK_LENGTH = {2, 2};
  /**
   * The constant DIR_REGEX represents the format of the dir name.
   */
  private static final String DIR_REGEX = "^[\\\\|\\/]?([\\w-]+[\\\\|\\/])*[\\w-]+[\\\\|\\/]?$|^[\\\\|/]$";
  /**
   * The constant CSV_FILE_REGEX represents the format of the CSV file name.
   */
  private static final String CSV_FILE_REGEX = "^(\\\\|\\/)?([\\w-]+(\\\\|\\/))*[\\w-]+\\.csv$";
  /**
   * The constant TXT_FILE_REGEX represents the format of the TXT file name.
   */
  private static final String TXT_FILE_REGEX = "^(\\\\|\\/)?([\\w-]+(\\\\|\\/))*[\\w-]+\\.txt$";
  /**
   * The constant ARG_NAME_REGEX represents the name of the argument after deleted some non-word
   * symbols. e.g. transfer "--email" to "email".
   */
  private static final String ARG_NAME_REGEX = "^[^\\w]*|[^\\w]*$";
  /**
   * The constant SUB_DIR_REGEX represents the name of the file name after deleted some non-word
   * symbols. e.g. transfer "dir1\dir2\filename.txt" to "filename".
   */
  private static final String SUB_DIR_REGEX = "^.*[\\\\|/]|\\..*$";
  /**
   * The constant NONE.
   */
  private static final String NONE = "";
  /**
   * The constant CSV_FILE_ARG.
   */
  private static final String CSV_FILE_ARG = "--csv-file";
  /**
   * The constant OUTPUT_DIR_ARG.
   */
  private static final String OUTPUT_DIR_ARG = "--output-dir";
  /**
   * The constant TEMPLATE_ARGS.
   */
  private static final String TEMPLATE_ARGS = "--email|--letter";
  /**
   * The constant EMAIL_ARG.
   */
  private static final String EMAIL_ARG = "--email";
  private static Options options;
  private static final int CAPACITY = 700000;

  static {
    options = new Options();
    options.addOption(new OptionBuilder("--email").setDependent(new String[]{"--email-template"})
        .setDesc("Generate email messages. --letter and this must be at least one.").build());
    options.addOption(new OptionBuilder("--letter").setDependent(new String[]{"--letter-template"})
        .setDesc("Generate letters. --email and this must be at least one.").build());
    options.addOption(
        new OptionBuilder("--email-template").setMultiple().setDependent(new String[]{"--email"})
            .hasSubOption().setSubOptionRegex(TXT_FILE_REGEX).setDesc(
            "<file> Accept a filename that holds the email template. Required if --email is used.")
            .build());
    options.addOption(
        new OptionBuilder("--letter-template").setMultiple().setDependent(new String[]{"--letter"})
            .hasSubOption().setSubOptionRegex(TXT_FILE_REGEX).setDesc(
            "<file> Accept a filename that holds the email template. Required if --letter is used.")
            .build());
    options.addOption(new OptionBuilder("--csv-file").setRequired().hasSubOption()
        .setSubOptionRegex(CSV_FILE_REGEX)
        .setDesc("<path> Accept the name of the csv file to process. Required.").build());
    options.addOption(new OptionBuilder("--output-dir").hasSubOption()
        .setDesc("<path> Accept the name of the output folder. Default is current folder.")
        .setSubOptionRegex(DIR_REGEX).build());
    options.addOneOrMoreList(new ArrayList<>(Arrays.asList("--email", "--letter")));
    options.addExample(
        "--email --email-template email-template.txt --output-dir emails --csv-file customer.csv");
    options.addExample("--letter --letter-template letter-template.txt "
        + "--output-dir letters --csv-file customer.csv");
    options.generateUsage();
  }

  /**
   * The main method of the application.
   *
   * @param args the input command line arguments
   */
  public static void main(String[] args) {
    try {
      ArgParser argParser = new ArgParser(args, options);
      Map<String, Option> argMap = argParser.parse();
      String csvFileName = argMap.get(CSV_FILE_ARG).getFirstSubOption();
      String outputDir = NONE;
      if (argMap.containsKey(OUTPUT_DIR_ARG)) {
        outputDir = argMap.get(OUTPUT_DIR_ARG).getFirstSubOption() + File.separator;
      }
      BlockingQueue<Map<String, String>> queue = new ArrayBlockingQueue<>(CAPACITY);
      ThreadPool.poolReset();
      ThreadPool.latchReset();
      Timer.reset();
      ThreadPool.addThread(new CsvParser(csvFileName, queue));
      for (Option option : argMap.values()) {
        if (option.getName().matches(TEMPLATE_ARGS)) {
          for (String fileName : argMap.get(option.getFirstDependent()).getSubOptions()) {
            String firstDir = option.getName().replaceAll(ARG_NAME_REGEX, NONE) + File.separator;
            String secondDir = fileName.replaceAll(SUB_DIR_REGEX, NONE) + File.separator;
            if (!new File(outputDir + firstDir + secondDir).mkdirs()) {
              System.out.println("Dir " + outputDir + firstDir + secondDir + " already existed.");
            }
            createTemplate(fileName, outputDir + firstDir + secondDir, option.getName(), queue, 1);
//            parser.addObserver(template);
          }
        }
      }
      ThreadPool.stop();
      ThreadPool.sleep();
      Timer.stop();
    } catch (CmdLineExceptions ex) {
      System.out.println("ERROR: " + ex.getMessage() + System.lineSeparator() + options.getUsage());
    }
  }

  /**
   * The helper method that create a template and specify a special Action strategy by the given
   * action name.
   *
   * @param fileName the file name
   * @param outputDir the output dir
   * @param actionName the name of the Action strategy
   * @return the Template object
   */
  private static Template createTemplate(String fileName, String outputDir, String actionName,
      BlockingQueue<Map<String, String>> queue, int numOfWorkers) {
    if (actionName.equals(EMAIL_ARG)) {
      return new Template(fileName, PLACEHOLDER_REGEX, MARK_LENGTH, outputDir, new EmailAction(),
          queue, numOfWorkers);
    } else {
      return new Template(fileName, PLACEHOLDER_REGEX, MARK_LENGTH, outputDir, new LetterAction(),
          queue, numOfWorkers);
    }
  }
}

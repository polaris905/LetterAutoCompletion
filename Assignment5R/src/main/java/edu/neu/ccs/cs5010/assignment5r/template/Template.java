package edu.neu.ccs.cs5010.assignment5r.template;

import edu.neu.ccs.cs5010.assignment5r.threadpool.ThreadPool;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Template that can read a TXT file and remember the positions of all placeholders once.
 * It implements the Observer interface in order to receive each of lines from the observable
 * object. When a line of information received, it will substitutes the placeholders by the given
 * values and then delegate the Action object to take the special action strategy.
 */
public class Template {

  private static final String FILE_NAME_FROM = "email";
  private static final String TXT_POSTFIX_REGEX = ".txt";
  private Actions action;
  private String original;
  private String regex;
  private int[] markLength;
  private String outputDir;
  private List<String> texts;
  private List<String> placeholders;
  private StringBuilder builder;
//  private String outputText;

  /**
   * Instantiates a new Template.
   *
   * @param fileName the original template file name
   * @param regex the regex used to split placeholders
   * @param markLength the two ends mark length of placeholders
   * @param outputDir the output dir
   * @param action the action strategy
   */
  public Template(String fileName, String regex, int[] markLength, String outputDir, Actions action,
      BlockingQueue<Map<String, String>> queue, int numOfWorkers) {
    this.regex = regex;
    this.markLength = markLength.clone();
    this.outputDir = outputDir;
    this.action = action;
    this.builder = new StringBuilder();
//    this.outputText = "";
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line).append(System.lineSeparator());
      }
      this.original = builder.toString();
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
    this.initial(queue, numOfWorkers);
  }

  /**
   * The helper method that iterates the whole text and remember the positions of all placeholders.
   * It means no matter how many lines of information were read, the process of finding placeholders
   * just execute once.
   */
  private void initial(BlockingQueue<Map<String, String>> queue, int numOfWorkers) {
    this.texts = new ArrayList<>();
    this.placeholders = new ArrayList<>();
    Matcher matcher = Pattern.compile(regex).matcher(this.original);
    int start = 0;
    while (matcher.find()) {
      String matchStr = matcher.group();
      String holderStr = matchStr.substring(markLength[0], matchStr.length() - markLength[1]);
      this.texts.add(this.original.substring(start, matcher.start()));
      this.placeholders.add(holderStr);
      start = matcher.end();
    }
    if (start < this.original.length()) {
      this.texts.add(this.original.substring(start));
    }
    for (int i = 0; i < numOfWorkers; i++) {
      ThreadPool.addThread(new TemplateWorker(queue));
    }
  }
//
//  /**
//   * Gets output text.
//   *
//   * @return the output text
//   */
//  public String getOutputText() {
//    return this.outputText;
//  }
//
//  /**
//   * This method is called whenever the observed object is changed.
//   *
//   * @param obj the observable object.
//   * @param arg an argparser passed to the notifyObservers
//   */
//  @SuppressWarnings("unchecked")
//  @Override
//  public void update(Observable obj, Object arg) {
//    if (obj instanceof CsvParser) {
//      this.generate((Map<String, String>) arg);
//    }
//  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Template template = (Template) o;
    return Objects.equals(original, template.original);
  }

  @Override
  public int hashCode() {
    return Objects.hash(original);
  }

  @Override
  public String toString() {
    return "Template{" + "original='" + original + '\'' + '}';
  }

  private class TemplateWorker implements Runnable {

    private BlockingQueue<Map<String, String>> queue;
    private int numOfTasks;

    public TemplateWorker(BlockingQueue<Map<String, String>> queue) {
      this.queue = queue;
    }

    /**
     * Generate text that substitutes placeholders by given values.
     *
     * @param headerValue the header-value map
     */
    public void generate(Map<String, String> headerValue) {
      builder.setLength(0);
      int indexT = 0;
      int indexP = 0;
      while (indexT < texts.size() && indexP < placeholders.size()) {
        builder.append(texts.get(indexT++));
        if (headerValue.containsKey(placeholders.get(indexP))) {
          builder.append(headerValue.get(placeholders.get(indexP++)));
        } else {
          builder.append(placeholders.get(indexP++));
        }
      }
      if (indexT < texts.size()) {
        builder.append(texts.get(indexT));
      }
      numOfTasks++;
//      action
//          .takeAction(outputDir + headerValue.get(FILE_NAME_FROM) + TXT_POSTFIX_REGEX, builder.toString());
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread,
     * starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
      System.out.println(this + " start");
      while (true) {
        if (queue.isEmpty() && ThreadPool.getProducerLatchCount() == 0) {
          break;
        } else {
          Map<String, String> line = queue.poll();
          if (line == null) {
            continue;
          }
          generate(line);
//          System.out.println(this + " generate line " + numOfTasks);
        }
      }
      ThreadPool.consumerLatchCountDown();
      System.out.println(this + " stop. Finishes " + numOfTasks + " files.");
    }
  }
}

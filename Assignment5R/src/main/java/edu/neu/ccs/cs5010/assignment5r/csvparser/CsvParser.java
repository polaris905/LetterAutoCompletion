package edu.neu.ccs.cs5010.assignment5r.csvparser;

import edu.neu.ccs.cs5010.assignment5r.threadpool.ThreadPool;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;

/**
 * The type Csv parser that can read properties and correspond values from a given CSV file. It
 * extends the Observable class in order to notify the observers when it is parsing every lines of
 * the CSV file.
 */
public class CsvParser implements Runnable {

  private static final String SPLIT_REGEX = "(?<=\"),(?=\")";
  private static final String QUOTE_REGEX = "\"";
  private static final String NONE = "";
  private BlockingQueue<Map<String, String>> queue;
  private List<String> headers;
  private Map<String, String> headerValue;
  private List<String> incompleteList;
  private String fileName;

  /**
   * Instantiates a new Csv parser.
   */
  public CsvParser(String fileName, BlockingQueue<Map<String, String>> queue) {
    this.fileName = fileName;
    this.queue = queue;
    this.headerValue = new HashMap<>();
    this.incompleteList = new ArrayList<>();
  }

  /**
   * Start parse the given CSV file.
   *
   */
  public void startParse() {
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
      String line;
      int num = 0;
      while ((line = reader.readLine()) != null) {
        if (num == 0) {
          this.parseHeader(line);
        } else {
          this.parseValue(line);
          queue.put(this.headerValue);
//          setChanged();
//          notifyObservers(this.headerValue);
        }
        num++;
      }
      System.out.println(
          "Read " + (num - 1) + " lines from the CSV file and " + (num - 1 - this.incompleteList
              .size()) + " lines are valid.");
      for (String incompleteLine : this.incompleteList) {
        System.out.println("Incomplete Line: " + incompleteLine);
      }
    } catch (InterruptedException | IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Parse the header of the CSV file.
   *
   * @param line the header line
   */
  private void parseHeader(String line) {
    String[] splitLine = line.split(SPLIT_REGEX);
    this.headers = new ArrayList<>(splitLine.length);
    for (String header : splitLine) {
      this.headers.add(header.replaceAll(QUOTE_REGEX, NONE));
    }
  }

  /**
   * Parse the values of a line of the CSV file.
   *
   * @param line the value line
   */
  private void parseValue(String line) {
    String[] splitLine = line.split(SPLIT_REGEX);
    if (splitLine.length != this.headers.size()) {
      System.out.println("The number of headers and values does not match.");
      this.incompleteList.add(line);
    }
    for (int i = 0; i < splitLine.length; i++) {
      this.headerValue.put(this.headers.get(i), splitLine[i].replaceAll(QUOTE_REGEX, NONE));
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    System.out.println(this + " start");
    this.startParse();
    ThreadPool.producerLatchCountDown();
    System.out.println(this + " end");
  }
}

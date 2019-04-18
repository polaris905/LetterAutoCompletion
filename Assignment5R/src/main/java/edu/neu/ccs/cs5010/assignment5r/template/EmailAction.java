package edu.neu.ccs.cs5010.assignment5r.template;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * The type Email action that implements the Actions interface. In this case, we assume that we can
 * put the commands of sending an email after writes the file.
 */
public class EmailAction implements Actions {

  /**
   * Take action method.
   *
   * @param fileName the file name
   * @param text the text
   */
  @Override
  public void takeAction(String fileName, String text) {
    try (BufferedWriter outputFile = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(fileName, false), "UTF-8"))) {
      outputFile.write(text);
//      System.out.println("Generate: " + fileName);
      // Assume that the commands of sending the email can put here.
    } catch (IOException ex) {
      System.out.println("ERROR: something went wrong: " + ex.getMessage());
    }
  }
}
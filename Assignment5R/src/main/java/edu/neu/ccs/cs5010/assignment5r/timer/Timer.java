package edu.neu.ccs.cs5010.assignment5r.timer;

/**
 * The type Timer.
 */
public class Timer {

  private static long start;
  private static long elapsed;
  private static boolean isPause;

  /**
   * Reset the timer.
   */
  public static void reset() {
    start = System.currentTimeMillis();
    elapsed = 0;
  }

  /**
   * Pause the timer.
   */
  public static void pause() {
    if (!isPause) {
      elapsed += (System.currentTimeMillis() - start);
      isPause = true;
    }
  }

  /**
   * Resume the timer.
   */
  public static void resume() {
    if (isPause) {
      start = System.currentTimeMillis();
      isPause = false;
    }
  }

  /**
   * Stop the timer.
   */
  public static void stop() {
    if (!isPause) {
      elapsed += (System.currentTimeMillis() - start);
    }
    System.out.println(elapsed / 1000.0 + "s");
  }
}

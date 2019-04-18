package edu.neu.ccs.cs5010.assignment5r.threadpool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Thread pool that manages the multiple thread process.
 */
public class ThreadPool {

  private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
  private static int producerThreads = 1;
  private static ExecutorService executor;
  private static CountDownLatch producerLatch;
  private static CountDownLatch consumerLatch;

  /**
   * Gets max threads.
   *
   * @return the max threads
   */
  public static int getMaxThreads() {
    return MAX_THREADS;
  }

  /**
   * Thread pool reset.
   */
  public static void poolReset() {
    executor = Executors.newFixedThreadPool(MAX_THREADS * 2);
  }

  /**
   * Get producer threads.
   *
   * @return the producer threads
   */
  public static int getProducerThreads() {
    return producerThreads;
  }

  /**
   * CountDownLatch reset.
   */
  public static void latchReset() {
    producerLatch = new CountDownLatch(producerThreads);
    consumerLatch = new CountDownLatch(MAX_THREADS);
  }

  /**
   * Set producer threads.
   *
   * @param producerThreads the producer threads
   */
  public static void setProducerThreads(int producerThreads) {
    ThreadPool.producerThreads = producerThreads;
  }

  /**
   * Get producer latch.
   *
   * @return the producer latch
   */
  public static CountDownLatch getProducerLatch() {
    return producerLatch;
  }

  /**
   * Get consumer latch.
   *
   * @return the consumer latch
   */
  public static CountDownLatch getConsumerLatch() {
    return consumerLatch;
  }

  /**
   * Get the count of the producer latch.
   *
   * @return the count of the producer latch
   */
  public static long getProducerLatchCount() {
    return producerLatch.getCount();
  }

  /**
   * Count down the consumer latch.
   */
  public static void consumerLatchCountDown() {
    consumerLatch.countDown();
  }

  /**
   * Count down the producer latch.
   */
  public static void producerLatchCountDown() {
    producerLatch.countDown();
  }

  /**
   * Set the consumer latch await.
   */
  public static void consumerLatchAwait() throws InterruptedException {
    consumerLatch.await();
  }

  /**
   * Add a new thread.
   *
   * @param runnable the runnable
   */
  public static void addThread(Runnable runnable) {
    executor.execute(runnable);
  }

  /**
   * Stop the executor.
   */
  public static void stop() {
    executor.shutdown();
  }

  /**
   * Sleep until all threads stop.
   */
  public static void sleep() {
    try {
      while (!executor.isTerminated()) {
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
      }
    } catch (InterruptedException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
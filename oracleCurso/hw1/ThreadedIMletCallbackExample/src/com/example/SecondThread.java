/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package com.example;

/**
 * This thread runs for just five minutes - and then stops
 *
 * @author Tom McGinn
 */
public class SecondThread extends Thread {

  private volatile boolean shouldRun = true;
  private final int delayTime;
  private final int FIVEMINS = 300000;
  private final int numRuns;

  public SecondThread(int delayTime) {
    this.delayTime = delayTime;
    // Calculate the number of "run" cycles by dividing five minutes (300000 ms)
    // by the delayTime interval
    numRuns = FIVEMINS / delayTime;
  }

  @Override
  public void run() {
    for (int i = 0; shouldRun && i < numRuns; i++) {
      System.out.println("Listening...");
      try {
        sleep(delayTime);
      } catch (InterruptedException ex) {
      }
    }
  }

  public void stop() {
    shouldRun = false;
    interrupt();
  }
}

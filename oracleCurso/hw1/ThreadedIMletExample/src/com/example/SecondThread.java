/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package com.example;

/**
 *
 * @author Tom McGinn
 */
public class SecondThread extends Thread {

  private volatile boolean shouldRun = true;
  private final int delayTime;

  public SecondThread(int delayTime) {
    this.delayTime = delayTime;
  }

  @Override
  public void run() {
    while (shouldRun) {
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

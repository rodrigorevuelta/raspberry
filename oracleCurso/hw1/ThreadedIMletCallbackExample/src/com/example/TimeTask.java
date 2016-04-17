/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package com.example;

import java.util.Date;
import java.util.TimerTask;

/**
 * One example of an IMlet that runs two threads - this example uses a callback
 * model to have the main task invoke a callback method on another thread to
 * start the '5' interval thread.
 *
 * @author Tom McGinn
 */
public class TimeTask extends TimerTask {

  private final int secondThreadDelayTime;
  private AtFiveMinuteIntervalListener listener;

  public TimeTask(int secondThreadDelayTime) {
    this.secondThreadDelayTime = secondThreadDelayTime;
  }

  // Return the number of milliseconds until the 5 after event
  public long init() {
    Date now = new Date();
    int mins = Integer.parseInt(now.toString().substring(14, 16));
    int dif = 4 - (mins % 5);
    int secs = Integer.parseInt(now.toString().substring(17, 19));
    return (dif * 60000 + ((60 - secs) * 1000));
  }

  // Add a listener for the a '5' minute event
  public void addFiveMinuteListener(AtFiveMinuteIntervalListener listener) {
    // The last listener registered wins
    this.listener = listener;
  }

  @Override
  public void run() {
    String time = new Date().toString().substring(11, 19);
    System.out.println("Time: " + time);
    char min_digit = time.charAt(4);
    if (min_digit == '5') {
      listener.fiveMinuteIntervalEvent();
    }
  }

  @Override
  public boolean cancel() {
    return super.cancel();
  }

}

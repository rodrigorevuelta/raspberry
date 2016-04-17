/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package com.example;

import java.util.Date;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 *
 * @author Tom McGinn
 */
public class ThreadedIMlet extends MIDlet implements AtFiveMinuteIntervalListener {

  private TimeTask task;
  private final int fiveMinutes = 5 * 60 * 1000;  // 5 minutes in milliseconds
  private int secondThreadDelayTime = 30 * 1000; // Default to 30 seconds
  private SecondThread secondThread;

  @Override
  public void startApp() {
    /* Read a property from the JAD and use it to determine the
     * delay time if the string is a valid integer, else use the
     * default.
     */
    String delayTimeStr;
    if ((delayTimeStr = getAppProperty("Delay-Time")) != null) {
      try {
        secondThreadDelayTime = Integer.parseInt(delayTimeStr) * 1000;
      } catch (NumberFormatException ex) {
        System.out.println("Delay-time prop: " + ex);
      }
    }
    System.out.println("Delay time at 5 min intervals: " + secondThreadDelayTime / 1000 + " secs");
    System.out.println("Starting Time: " + new Date().toString().substring(11, 19));
    Timer timer = new Timer();
    task = new TimeTask(secondThreadDelayTime);
    // Add myself as a listener for the '5' events
    task.addFiveMinuteListener(this);
    long delay = task.init();
    timer.scheduleAtFixedRate(task, delay, fiveMinutes);
  }

  @Override
  public void fiveMinuteIntervalEvent() {
    // Start the second thread to count off during the 5 minute interval
    secondThread = new SecondThread(secondThreadDelayTime);
    secondThread.start();
  }
  
    @Override
  public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    System.out.println("Destroying...");
    task.cancel();
    if (secondThread != null) {
      secondThread.stop();
    }
  }
}

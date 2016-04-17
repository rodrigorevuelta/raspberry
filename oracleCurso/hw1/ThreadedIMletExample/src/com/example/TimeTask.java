/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package com.example;

import java.util.Date;
import java.util.TimerTask;

/**
 * One example of an IMlet that runs two threads - one start inside another when a
 * specific event is triggered - in this case, when the minute of the hour ends in '5'.
 * 
 * @author Tom McGinn
 */
public class TimeTask extends TimerTask {

    private SecondThread secondThread;
    private final int secondThreadDelayTime;

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

    @Override
    public void run() {
        String time = new Date().toString().substring(11, 19);
        System.out.println("Time: " + time);
        char min_digit = time.charAt(4);
        if (min_digit == '5') {
            secondThread = new SecondThread(secondThreadDelayTime);
            secondThread.start();
        } else {
            if (secondThread != null) {
                secondThread.stop();
            }
        }
    }

    @Override
    public boolean cancel() {
        if (secondThread != null) {
            secondThread.stop();
        }
        return super.cancel(); 
    }

}

package com.example;

import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Tom McGinn
 */
public class TestIMlet extends MIDlet implements CountListener {
    private CountingDevice device;

    @Override
    public void startApp() {
        System.out.println("Starting...");
        device = new CountingDevice(5);
        device.addCountListener(this);
        device.start();
    }

    @Override
    public void destroyApp(boolean unconditional) {
        System.out.println("Destroying...");
        device.stop();
    }

    @Override
    public void countReached(int count) {
        System.out.println("In TestIMlet: countReached: count: " + count);
    }
}

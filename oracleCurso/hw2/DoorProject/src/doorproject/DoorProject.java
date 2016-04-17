/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doorproject;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import jdk.dio.DeviceNotFoundException;

/**
 *
 * @author Angela Caicedo
 */
public class DoorProject extends MIDlet {

  private DoorSensor doorSensor;

  @Override
  public void startApp() {

    doorSensor = new DoorSensor(0, 17, 24, 23);

    try {
      doorSensor.start();
    } catch (DeviceNotFoundException ex) {
      System.out.println("DeviceException: " + ex.getMessage());
      notifyDestroyed();
    } catch (IOException ex) {
      System.out.println("IOException: " + ex);
      notifyDestroyed();
    }
  }

  /**
   * Imlet lifecycle termination method
   *
   * @param unconditional If the imlet should be terminated whatever
   */
  @Override
  public void destroyApp(boolean unconditional) {
    try {
      doorSensor.close();
    } catch (IOException ex) {
      System.out.println("IOException closing DoorSensor: " + ex);
    }
  }
}

/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempbarosensor;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Angela, Tom McGinn
 */
public class TempBaroSensorTest extends MIDlet {

  private BMP180TemperatureBarometerSensor myDev;

  @Override
  public void startApp() {
    myDev = new BMP180TemperatureBarometerSensor();
    new Thread(new Runnable() {

      @Override
      public void run() {
        double[] result;
        try {
          int i = 0;
          while (true) {
            Thread.sleep(1000);
            result = myDev.getMetricTemperatureBarometricPressure();
            double tempC = result[0];
            double tempF = myDev.celsiusToFahrenheit(tempC);
            double pressureHPa = result[1];
            double pressureInHg = myDev.pascalToInchesMercury(pressureHPa);
            System.out.format("Temp: %d %.2f C / %.2f F\n", i, tempC, tempF);
            System.out.format("Pressure: %.2f hPa / %.2f inHg\n", pressureHPa, pressureInHg);
            i++;
          }
        } catch (IOException | InterruptedException ex) {
          System.out.println("Exception in startApp: " + ex);
        }
      }
    }).start();
  }

  @Override
  public void destroyApp(boolean unconditional) {
    try {
      myDev.close();
    } catch (IOException ex) {
      System.out.println("Exception closing device: " + ex);
    }
  }
}

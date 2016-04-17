/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempbarosensor;

import java.io.IOException;
import java.nio.ByteBuffer;
import mooc.data.TemperatureData;
import mooc.sensor.PressureSensor;
import mooc.sensor.TemperatureSensor;

/**
 *
 * @author Angela, Tom McGinn
 */
public class BMP180TemperatureBarometerSensor extends BMP180Sensor implements TemperatureSensor, PressureSensor {

  // Temperature read address
  private static final int tempAddr = 0xF6;
  // Read temperature command
  private static final byte getTempCmd = (byte) 0x2E;

  //Uncompensated Temperature data
  private int UT;

  // Temperature read address
  private static final int pressAddr = 0xF6;

  // Uncompensated pressure
  private int UP;

  //Barometer configuration
  private byte pressureCmd;
  private int delay;
  private int oss;

  // Shared ByteBuffers
  private ByteBuffer uncompTemp;

  /**
   * Temperature/Barometric sensor constructor. Just invoke the parent
   * constructor
   */
  public BMP180TemperatureBarometerSensor() {
    super();
    uncompTemp = ByteBuffer.allocateDirect(2);
    initCommands(BarometerModes.STANDARD);
  }

  /**
   * Barometer sensor constructor. this method invoke the parent constructor and
   * perform some barometer initializations
   *
   * @param mode Barometer mode
   */
  public BMP180TemperatureBarometerSensor(BarometerModes mode) {
    super();
    uncompTemp = ByteBuffer.allocateDirect(2);
    initCommands(mode);
  }

  /**
   * Temperature Sensor constructor. It just invoke the parent's constructor
   *
   * @param i2cBus
   * @param address
   * @param addressSizeBits
   * @param serialClock
   */
  public BMP180TemperatureBarometerSensor(int i2cBus, int address, int addressSizeBits, int serialClock) {
    super(i2cBus, address, addressSizeBits, serialClock);
    uncompTemp = ByteBuffer.allocateDirect(2);
    initCommands(BarometerModes.STANDARD);
  }

  /**
   * Set the pressure command and delays depending on the selected barometer
   * mode
   *
   * @param mode Barometer mode
   */
  private void initCommands(BarometerModes mode) {
    pressureCmd = mode.getCommand();
    delay = mode.getDelay();
    oss = mode.getOSS();
  }

  /**
   * Method for reading the temperature and barometric pressure from the BMP
   * device as metric values
   *
   * @return A double array containing the current temperature in Celsius and
   * the barometric pressure in hPa
   * @throws IOException
   */
  public double[] getMetricTemperatureBarometricPressure() throws IOException {
    double[] result = new double[2];
    result[0] = getTemperatureInCelsius();
    result[1] = getPressureInHPa();
    return result;
  }

  /**
   * Method for reading the temperature and barometric pressure from the BMP
   * device as English values
   *
   * @return A double array containing the current temperature in Celsius and
   * the barometric pressure in hPa
   * @throws IOException
   */
  public double[] getEnglishTemperatureBarometricPressure() throws IOException {
    double[] result = new double[2];
    result[0] = getTemperatureInFahrenheit();
    result[1] = getPressureInInchesMercury();
    return result;
  }

  /**
   * Method for reading the temperature. Remember the sensor will provide us
   * with raw data, and we need to transform in some analyzed value to make
   * sense. All the calculations are normally provided by the manufacturer. In
   * our case we use the calibration data collected at construction time.
   *
   * @return Temperature in Celsius as a double
   * @throws IOException If there is an IO error reading the sensor
   */
  @Override
  public double getTemperatureInCelsius() throws IOException {
    // Write the read temperature command to the command register
    writeOneByte(controlRegister, getTempCmd);

    // Delay before reading the temperature
    try {
      Thread.sleep(100);
    } catch (InterruptedException ex) {
    }

    //Read uncompressed data
    uncompTemp.clear();
    int result = myDevice.read(tempAddr, subAddressSize, uncompTemp);
    if (result < 2) {
      System.out.println("Not enough data for temperature read");
    }

    // Get the uncompensated temperature as a signed two byte word
    uncompTemp.rewind();
    byte[] data = new byte[2];
    uncompTemp.get(data);
    UT = ((data[0] << 8) & 0xFF00) + (data[1] & 0xFF);

    // Calculate the actual temperature
    int X1 = ((UT - AC6) * AC5) >> 15;
    int X2 = (MC << 11) / (X1 + MD);
    B5 = X1 + X2;
    float celsius = (float) ((B5 + 8) >> 4) / 10;

    return celsius;
  }

  /* 
   * Read the temperature value as a Celsius value and the convert the value to Farenheit.
   *
   * @return Temperature in Celsius as a double
   * @throws IOException If there is an IO error reading the sensor
   */
  @Override
  public double getTemperatureInFahrenheit() throws IOException {
    return celsiusToFahrenheit(getTemperatureInCelsius());
  }

  /*
   * Calculate temperature in Fahrenheit based on a celsius temp
   * 
   * @param temp - The temperature in degrees Celsius to convert to Fahrenheit
   * @return double - Temperature in degrees Fahrenheit, converted from Celsius
   */
  public double celsiusToFahrenheit(double temp) {
    return ((temp * 1.8) + 32);
  }

  /**
   * Read the barometric pressure (in hPa) from the device.
   *
   * @return double Pressure measurement in hPa
   */
  @Override
  public double getPressureInHPa() throws IOException {

    // Write the read pressure command to the command register
    writeOneByte(controlRegister, pressureCmd);

    // Delay before reading the pressure - use the value determined by the oversampling setting (mode)
    try {
      Thread.sleep(delay);
    } catch (InterruptedException ex) {
    }

    // Read the uncompensated pressure value
    ByteBuffer uncompPress = ByteBuffer.allocateDirect(3);
    int result = myDevice.read(pressAddr, subAddressSize, uncompPress);
    if (result < 3) {
      System.out.println("Couldn't read all bytes, only read = " + result);
      return 0;
    }

    // Get the uncompensated pressure as a three byte word
    uncompPress.rewind();

    byte[] data = new byte[3];
    uncompPress.get(data);

    UP = ((((data[0] << 16) & 0xFF0000) + ((data[1] << 8) & 0xFF00) + (data[2] & 0xFF)) >> (8 - oss));

    // Calculate the true pressure
    int B6 = B5 - 4000;
    int X1 = (B2 * (B6 * B6) >> 12) >> 11;
    int X2 = AC2 * B6 >> 11;
    int X3 = X1 + X2;
    int B3 = ((((AC1 * 4) + X3) << oss) + 2) / 4;
    X1 = AC3 * B6 >> 13;
    X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
    X3 = ((X1 + X2) + 2) >> 2;
    int B4 = (AC4 * (X3 + 32768)) >> 15;
    int B7 = (UP - B3) * (50000 >> oss);

    int Pa;
    if (B7 < 0x80000000) {
      Pa = (B7 * 2) / B4;
    } else {
      Pa = (B7 / B4) * 2;
    }

    X1 = (Pa >> 8) * (Pa >> 8);
    X1 = (X1 * 3038) >> 16;
    X2 = (-7357 * Pa) >> 16;

    Pa += ((X1 + X2 + 3791) >> 4);

    return (Pa) / 100;
  }

  /**
   * Read the barometric pressure (in inches mercury) from the device.
   *
   * @return Current pressure in inches mercury
   * @throws IOException
   */
  @Override
  public double getPressureInInchesMercury() throws IOException {
    return pascalToInchesMercury(getPressureInHPa());
  }

  /**
   * Calculate pressure in inches of mercury (inHg) - uses a constant to convert
   * between the two
   *
   * @param pressure - The pressure in hPa
   * @return float - Pressure converted to inches Mercury (inHg)
   */
  public double pascalToInchesMercury(double pressure) {
    return (pressure * 0.0296);
  }

  @Override
  public TemperatureData getTemperatureData() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Gracefully close the open I2CDevice
   *
   * @throws IOException
   */
  public void close() throws IOException {
    myDevice.close();
  }
}

/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempbarosensor;

public enum BarometerModes {
    // Relationship between Oversampling Setting and conversion delay (in ms) for each Oversampling Setting contstant
  // Ultra low power:        4.5 ms minimum conversion delay
  // Standard:               7.5 ms 
  // High Resolution:       13.5 ms
  // Ultra high Resolution: 25.5 ms

  ULTRA_LOW_POWER(0, 5), STANDARD(1, 8), HIGH_RESOLUTION(2, 14), ULTRA_HIGH_RESOLUTION(3, 26);

  private final int oss;                                      // Oversample setting value
  private final int delay;                                    // Minimum conversion time in ms
  private static final byte getPressCmd = (byte) 0x34;        // Read pressure command
  private final byte cmd;                                     // Command byte to read pressure

  /**
   * Barometer Modes constructor.
   *
   * @param ossOversample value
   * @param delay Delay
   */
  BarometerModes(int oss, int delay) {
    this.oss = oss;
    this.delay = delay;
    this.cmd = (byte) (getPressCmd + ((oss << 6) & 0xC0));
  }

  /**
   * Return the conversion delay (in ms) associated with this oversampling
   * setting
   *
   * @return delay
   */
  public int getDelay() {
    return delay;
  }

  /**
   * Return the command to the control register for this oversampling setting
   *
   * @return command
   */
  public byte getCommand() {
    return cmd;
  }

  /**
   * Return this oversampling setting
   *
   * @return Oversampling setting
   */
  public int getOSS() {
    return oss;
  }
}

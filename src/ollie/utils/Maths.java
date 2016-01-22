package ollie.utils;

import java.util.Random;

public class Maths {

	private Maths(){}
	
	/**
	 * Generates a random number between the given min and max values.
	 * @param min - the lowest value for the random number.
	 * @param max - the highest value for the random number.
	 * @return A random number with in the given range. 
	 */
	public static int randomNumber(int min, int max) {
		Random r = new Random();
		return (r.nextInt(max - min) + min);
	}
	
	/**
	 * Converts the input number of the input range to the out put range.
	 * @param intput - the number to convert.
	 */
	public static double convertRange(double input, double inputHigh, double inputLow, double outputHigh, double outputLow) {
		// result = ((input - inputLow) / (inputHigh - inputLow)) * (outputHigh - outputLow) + outputLow
		double outputRange = outputHigh - outputLow;
		double inputRange = inputHigh - inputLow;
		return (((input - inputLow) / inputRange) * outputRange) + outputLow;
	}
}

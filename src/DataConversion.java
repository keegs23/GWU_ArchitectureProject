/**
 * Data Format Conversion Utility Class
 */
public class DataConversion {
	
	/**
	 * Converts binary string to corresponding ASCII character as string
	 */
	public static String binaryToText(String inputString) {
		
		int outputInt = Integer.parseInt(inputString, 2);
		
		return new Character((char) outputInt).toString();
	}
	
	/**
	 * Converts single ASCII character to 16-bit binary string
	 */
	public static String textToBinary(char inputChar) {
		
		int outputInt = (int) inputChar;
		String outputString = Integer.toBinaryString(outputInt);
		
		return ArithmeticLogicUnit.padZeros(outputString);
	}

}

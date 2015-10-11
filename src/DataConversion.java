/**
 * Data Format Conversion Utility Class
 */
public class DataConversion {
	
	/**
	 * Converts binary string to corresponding ASCII character as string
	 */
	public static String binaryToText(String inputString) {
		
		int inputInt = Integer.parseInt(inputString, 2);
		
		return new Character((char) inputInt).toString();
	}
	
	/**
	 * Converts single ASCII character string to 16-bit binary string
	 */
	public static String textToBinary(String inputString) {
		
		char outputChar = inputString.charAt(0);
		int outputInt = (int) outputChar;
		String outputString = Integer.toBinaryString(outputInt);
		
		return ArithmeticLogicUnit.padZeros(outputString);
	}

}

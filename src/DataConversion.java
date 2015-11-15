/**
 * Data Format Conversion Utility Class
 */
public class DataConversion {
	
	/**
	 * Converts binary string to corresponding ASCII character as string
	 */
	public static String binaryToText(String inputString) {
		
		int outputInt = Integer.parseInt(inputString, 2);
		Character outputChar = new Character((char) outputInt);
		
		return outputChar.toString();
	}
	
	/**
	 * Converts single ASCII character to 16-bit binary string
	 */
	public static String textToBinary(char inputChar) {
		
		int outputInt = (int) inputChar;
		String outputString = Integer.toBinaryString(outputInt);
		
		return ArithmeticLogicUnit.padZeros16(outputString);
	}
	
	/**
	 * Converts int to four-bit binary string
	 */
	public static String intToFourBitString(int inputInt) {
		
		int outputInt = Integer.parseInt(Integer.toBinaryString(inputInt));
		
		return String.format("%04d", outputInt);
	}

}

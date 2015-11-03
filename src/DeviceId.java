/**
 * 
 * ID codes for IN and OUT instructions; 5-bits
 *
 */
public class DeviceId {
	
	public static final String CONSOLE_KEYBOARD = "00000"; // 0: Input number or character
	
	public static final String CONSOLE_PRINTER = "00001"; // 1: Output binary as integers
	
	public static final String CARD_READER = "00010"; // 2: Not used
	
	public static final String FILE_READER_ASCII = "00011"; // 3: Input characters from file
	
	public static final String CONSOLE_PRINTER_ASCII = "00100"; // 4: Output binary as ASCII text

}

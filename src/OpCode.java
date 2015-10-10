/**
 * Constants are binary values
 * Comments are octal values
 */
public class OpCode
{
	/**
	 * Stops the machine
	 */
	public static final String HLT  = "000000"; //0
	
	/**
	 * Traps to memory address 0
	 */
	public static final String TRAP = "011110"; //36
	
	/**
	 * Load register from memory
	 */
	public static final String LDR  = "000001"; //1
	
	/**
	 * Store register to memory
	 */
	public static final String STR  = "000010"; //2
	
	/**
	 * Load register with address
	 */
	public static final String LDA  = "000011"; //3
	
	/**
	 * Load index register from memory
	 */
	public static final String LDX  = "100001"; //41
	
	/**
	 * Store index register to memory
	 */
	public static final String STX  = "100010"; //42
	
	/**
	 * Add memory to register
	 */
	public static final String AMR  = "000100"; //4
	
	/**
	 * Substract memory from register
	 */
	public static final String SMR  = "000101"; //5
	
	/**
	 * Add immediate to register
	 */
	public static final String AIR  = "000110"; //6
	
	/**
	 * Substract immediate from register
	 */
	public static final String SIR  = "000111"; //7
	
	/**
	 * Input character to register from device
	 */
	public static final String IN  = "110001"; //61
	
	/**
	 * Output character to device from register
	 */
	public static final String OUT  = "110010"; //62

	/**
	 * Jump If Zero
	 */
	public static final String JZ = "001000";	//10
	
	/**
	 * Jump If Not Equal
	 */
	public static final String JNE = "001001";	//11
	
	/**
	 * Jump If Condition Code
	 */
	public static final String JCC = "001010";	//12
	
	/**
	 * Unconditional Jump To Address
	 */
	public static final String JMA = "001011";	//13
	
	/**
	 * Jump and Save Return Address
	 */
	public static final String JSR = "001100";	//14
	
	/**
	 * Return From Subroutine
	 */
	public static final String RFS = "001101";	//15
	
	/**
	 * Subtract One and Branch
	 */
	public static final String SOB = "001110";	//16
	
	/**
	 * Jump If Greater Than or Equal To
	 */
	public static final String JGE = "001111";	//17
}
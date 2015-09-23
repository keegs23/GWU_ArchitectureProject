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

}
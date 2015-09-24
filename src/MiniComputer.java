public class MiniComputer
{
	public static final String MAX_MEMORY_ADDRESS = "1111111111111111";
	
	/**
	 * Program Counter
	 */
	private Register PC;
	/**
	 * Condition Code Register
	 */
	private Register CC;
	/**
	 * Instruction Register
	 */
	private Register IR;
	/**
	 * Memory Address Register
	 */
	private Register MAR;
	/**
	 * Memory Buffer Register
	 */
	private Register MBR;
	/**
	 * Machine Status Register
	 */
	private Register MSR;
	/**
	 * Machine Fault Register
	 */
	private Register MFR;
	
	// General Purpose Registers
	private Register R0;
	private Register R1;
	private Register R2;
	private Register R3;
	
	// Index Registers
	private Register X1;
	private Register X2;
	private Register X3;
	
	public MiniComputer()
	{
		// TODO: Initialize Registers
		PC = new Register(12); //PC register size is 12 bits
		CC = new Register(4); //PC register size is 4 bits
		IR = new Register(16); //PC register size is 16 bits
		MAR = new Register(16); //PC register size is 16 bits
		MBR = new Register(16); //PC register size is 16 bits
		MSR = new Register(16); //PC register size is 16 bits
		MFR = new Register(4); //PC register size is 4 bits
	}
	
	/**
	 * Retrieves the General Purpose Register indicated by r.
	 * @param r
	 * @return R0, R1, R2, R3, or null
	 */
	public Register R(int r)
	{
		switch(r) {
			case 0:
				return R0;
			case 1:
				return R1;
			case 2:
				return R2;
			case 3:
				return R3;
			default:
				return null;
		}
	}
	
	/**
	 * Retrieves the Index Register indicated by x.
	 * @param x
	 * @return X1, X2, X3, or null
	 */
	public Register X(int x)
	{
		switch(x) {
			case 1:
				return X1;
			case 2:
				return X2;
			case 3:
				return X3;
			default:
				return null;
		}
	}
	
	public BitWord calculateEffectiveAddress(int indexRegister, boolean isIndirectAddress, BitWord address)
	{
		// TODO
		return new BitWord();	//dummy value
	}
	
	public static void main(String[] args)
	{
		// TODO: mimic flowchart from Lecture 1
	}
}
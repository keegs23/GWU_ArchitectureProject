import java.util.HashMap;
import java.util.Map;

public class MiniComputer
{
	public static final String MAX_MEMORY_ADDRESS = "1111111111111111";	//16 one bits
	
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
	
	/**
	 * Key = String Address, Value = MemoryLocation object
	 */
	Map<String, MemoryLocation> memory;
	
	public MiniComputer()
	{
		// Initialize Registers
		PC = new Register(12); //register size is 12 bits
		CC = new Register(4); //register size is 4 bits
		IR = new Register(16); //register size is 16 bits
		MAR = new Register(16); //register size is 16 bits
		MBR = new Register(16); //register size is 16 bits
		MSR = new Register(16); //register size is 16 bits
		MFR = new Register(4); //register size is 4 bits
		R0 = new Register(16);
		R1 = new Register(16);
		R2 = new Register(16);
		R3 = new Register(16);
		X1 = new Register(16);
		X2 = new Register(16);
		X3 = new Register(16);
		
		// Initialize Memory
		memory = new HashMap<String, MemoryLocation>();
	}
	
	public Register getPC()
	{
		return PC;
	}

	public Register getCC()
	{
		return CC;
	}
	public Register getIR()
	{
		return IR;
	}
	public Register getMAR()
	{
		return MAR;
	}
	public Register getMBR()
	{
		return MBR;
	}
	public Register getMSR()
	{
		return MSR;
	}
	public Register getMFR()
	{
		return MFR;
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
		if(!isIndirectAddress)
		{
			if(indexRegister == 0)
			{
				return address;
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = X(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
						
				String ea = addHelper(indexValue, toAdd);
						
				return new BitWord(ea);
			}
			else
			{
				// Should never reach here, but just in case
				return address;	//does it make sense to return this?
			}
		}
		else
		{
			String addr = "";
			
			if(indexRegister == 0)
			{
				addr = address.getValue();
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = X(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
				
				addr = addHelper(indexValue, toAdd);
			}
			
			if(memory.containsKey(addr))	//MemoryLocation
			{
				return memory.get(addr).getValue();
			}
			else
			{
				// TODO: Check that addr is not beyond 2048 (decimal)
				
				return new BitWord();	//value is 0000000000000000 (16 zero bits)
			}
		}
	}
	
	public static void main(String[] args)
	{
		// TODO: mimic flowchart from Lecture 1
	}
	
	/**
	 * Adds the 2 binary bit Strings
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary sum of bitStr1 and bitStr2
	 */
	private String addHelper(String bitStr1, String bitStr2)
	{
		// How closely were we supposed to mimic how the computer actually does bit addition?
		
		String sum = "";
		
		// Make sure both are 16 bits
		String bits1 = padZeros(bitStr1);
		String bits2 = padZeros(bitStr2);
		
		int carryIn = 0;
		int carryOut = 0;
		
		for(int k = 15; k <= 0; k--)
		{
			// Retrieve the next lowest bit
			int a = Integer.parseInt(bits1.substring(k, k+1));
			int b = Integer.parseInt(bits2.substring(k, k+1));
			
			// Calculate the next bit for the sum and the carryOut
			int s = a + b + carryIn;
			sum = Integer.toString(s % 2) + sum;
			carryOut = s / 2;
			
			// Prepare for the next bit
			carryIn = carryOut;
		}
		
		// Check for overflow
		if (carryOut > 0)
		{
			// TODO: Handle overflow
		}
		
		return sum;
	}
	
	/**
	 * Pads with leading zeros until length is 16
	 * @param value bit String with length < 16
	 * @return 16-bit String
	 */
	private static String padZeros(String value)
	{
		return String.format("%016d", Integer.parseInt(value));
	}
}
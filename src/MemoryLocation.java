import java.util.Arrays;
import java.util.List;

public class MemoryLocation
{
	public static final String RESERVED_ADDRESS_TRAP =  "0000000000000000";   	//Memory Address 0 (zero)
	public static final String RESERVED_ADDRESS_FAULT =  "0000000000000001";   	//Memory Address 1
	public static final String RESERVED_ADDRESS_TRAP_PC =  "0000000000000010";   //Memory Address 2
	public static final String RESERVED_ADDRESS_3 =  "0000000000000011";   		//Memory Address 3
	public static final String RESERVED_ADDRESS_FAULT_PC =  "0000000000000100";   //Memory Address 4
	public static final String RESERVED_ADDRESS_5 =  "0000000000000101";   		//Memory Address 5
	public static final String RESERVED_ADDRESS_TOGGLE_INSTRUCTION = "0000000000000110";	//Memory Address 6
	//public static final List<String> RESERVED_ADDRESSES = new ArrayList<String>() [ ADDRESS_TRAP, ADDRESS_FAULT, ADDRESS_TRAP_PC, ADDRESS_3, ADDRESS_FAULT_PC, ADDRESS_5];
	public static final String RESERVED_ADDRESS_TRAP_FIRST = "0000011111100110";	//Memory Address 2022 (octal 3746)
	public static final List<String> RESERVED_TRAP_TABLE = Arrays.asList(		//Memory Addresses 2022 - 2037 (octal 3746 - 3765)
			RESERVED_ADDRESS_TRAP_FIRST,
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000001").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000010").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000011").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000100").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000101").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000110").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000000111").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001000").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001001").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001010").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001011").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001100").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001101").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001110").get(ArithmeticLogicUnit.KEY_SUM),
			(String) ArithmeticLogicUnit.add(RESERVED_ADDRESS_TRAP_FIRST, "0000000000001111").get(ArithmeticLogicUnit.KEY_SUM));
	
	public static final String ADDRESS_BOOT_PRGM_START = "0000000000001000";	//Memory Address 8 (octal 10)
	public static final String ADDRESS_PRGM_ONE_START = "0000000000011110";		//Memory Address 30 (octal 36)
	
	private BitWord value;
	private BitWord address;
	
	public MemoryLocation(String tempAddress, String val)
	{		
		address = new BitWord(tempAddress);
		value = new BitWord(val);
	}
	
	public MemoryLocation(BitWord tempAddress, BitWord val)
	{
		address = tempAddress;
		value = val;
	}
	
	public BitWord getValue()
	{
		return value;
	}
	
	public void setValue(String val)
	{
		setValue(new BitWord(val));
	}
	
	public void setValue(BitWord val)
	{
		value = val;
	}
	
	public BitWord getAddress()
	{
		return address;
	}
	
	public void setAddress(String tempAddress)
	{
		address = new BitWord(tempAddress);
	}
	
	public static boolean isAddressReserved(String addr)
	{
		if (addr == null)
		{
			return false;
		}
		addr = ArithmeticLogicUnit.padZeros16(addr);
		
		return addr.equals(RESERVED_ADDRESS_TRAP) || 
				addr.equals(RESERVED_ADDRESS_FAULT) || 
				addr.equals(RESERVED_ADDRESS_TRAP_PC) || 
				addr.equals(RESERVED_ADDRESS_3) || 
				addr.equals(RESERVED_ADDRESS_FAULT_PC) || 
				addr.equals(RESERVED_ADDRESS_5) ||
				addr.equals(RESERVED_ADDRESS_TOGGLE_INSTRUCTION) ||
				RESERVED_TRAP_TABLE.contains(addr);
	}
	
}
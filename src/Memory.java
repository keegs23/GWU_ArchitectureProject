public class Memory
{
	private static final String ADDRESS_TRAP =  "000000000000000";   	//Memory Address 0 (zero)
	private static final String ADDRESS_FAULT =  "000000000000001";   	//Memory Address 1
	private static final String ADDRESS_TRAP_PC =  "000000000000010";   //Memory Address 2
	private static final String ADDRESS_3 =  "000000000000011";   		//Memory Address 3
	private static final String ADDRESS_FAULT_PC =  "000000000000100";   //Memory Address 4
	private static final String ADDRESS_5 =  "000000000000101";   		//Memory Address 5
	//Question: Address is 5 bits with load instructions 2^5=32. 
	//			MAR has 16 bits and holds the address 2^15= 32,768
	//			2048 * 16 = 32,768 for all of Memory, but only 2048 addresses which is 2^11 or 12 bits needed (they use indexing to get around this)
	// SHould the MAR be 12-13 bits? So if we keep
	//private static final List<String> RESERVED_ADDRESSES = new ArrayList<String>() [ ADDRESS_TRAP, ADDRESS_FAULT, ADDRESS_TRAP_PC, ADDRESS_3, ADDRESS_FAULT_PC, ADDRESS_5];
	
	private BitWord value;
	private BitWord address;
	
	public Memory(String tempAddress, String val) throws Exception
	{
		if(ADDRESS_TRAP == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		if(ADDRESS_FAULT == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		if(ADDRESS_TRAP_PC == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		if(ADDRESS_3 == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		if(ADDRESS_FAULT_PC == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		if(ADDRESS_5 == tempAddress)
		{
			throw new Exception("That address is reserved!");
		}
		
		value = new BitWord(val);
		address = new BitWord(tempAddress);
	}
	
	public BitWord getValue()
	{
		return value;
	}
	
	public void setValue(String val)
	{
		value = new BitWord(val);
	}
	public BitWord getAddress()
	{
		return address;
	}
	
	public void setAddress(String tempAddress)
	{
		address = new BitWord(tempAddress);
	}
	
}
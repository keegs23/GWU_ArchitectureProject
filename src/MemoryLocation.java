public class MemoryLocation
{
	private static final String RESERVED_ADDRESS_TRAP =  "000000000000000";   	//Memory Address 0 (zero)
	private static final String RESERVED_ADDRESS_FAULT =  "000000000000001";   	//Memory Address 1
	private static final String RESERVED_ADDRESS_TRAP_PC =  "000000000000010";   //Memory Address 2
	private static final String RESERVED_ADDRESS_3 =  "000000000000011";   		//Memory Address 3
	private static final String RESERVED_ADDRESS_FAULT_PC =  "000000000000100";   //Memory Address 4
	private static final String RESERVED_ADDRESS_5 =  "000000000000101";   		//Memory Address 5
	private static final String RESERVED_ADDRESS_TOGGLE_INSTRUCTION = "000000000000110";	//Memory Address 6
	//private static final List<String> RESERVED_ADDRESSES = new ArrayList<String>() [ ADDRESS_TRAP, ADDRESS_FAULT, ADDRESS_TRAP_PC, ADDRESS_3, ADDRESS_FAULT_PC, ADDRESS_5];
	
	private BitWord value;
	private BitWord address;
	
	public MemoryLocation(String tempAddress, String val)
	{		
		value = new BitWord(val);
		address = new BitWord(tempAddress);
	}
	
	public MemoryLocation(BitWord tempAddress, BitWord val)
	{
		value = tempAddress;
		address = val;
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
	
	public boolean isAddressReserved()
	{
		String addr = address.getValue();
		
		return addr == RESERVED_ADDRESS_TRAP || 
				addr == RESERVED_ADDRESS_FAULT || 
				addr == RESERVED_ADDRESS_TRAP_PC || 
				addr == RESERVED_ADDRESS_3 || 
				addr == RESERVED_ADDRESS_FAULT_PC || 
				addr == RESERVED_ADDRESS_5 ||
				addr == RESERVED_ADDRESS_TOGGLE_INSTRUCTION;
	}
	
}
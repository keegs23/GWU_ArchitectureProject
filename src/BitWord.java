public class BitWord {
	public static final String VALUE_ZERO = "0000000000000000";	//16 zero bits
	public static final String VALUE_ONE = "0000000000000001";	//15 zeros then a one
	public static final String VALUE_ENTER = "1111111111111111"; //Represents value for Enter character
	public static final String VALUE_NEWLINE = "1010101010101010"; //Represents value for New line character
	public static final String VALUE_DEFAULT = VALUE_ZERO;
	public static final String VALUE_INVALID_OPCODE = "111111"; //Unused Opcode value. Represents invalid opcode
	public static final String VALUE_HALT = "0000000000000000"; //16 zeros
	
	protected final String value;
	
	public BitWord()
	{
		value = VALUE_DEFAULT;
	}
	
	public BitWord(String val)
	{
		value = val;
	}
	
	public String getValue()
	{
		return value;
	}
	
	
	public int getSize()
	{
		return value.length();
	}
	
	/**
	 * Returns true if the value is equal, ignoring leading zeros
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof BitWord)
		{
			// If the value is the same, ignoring leading zeros
			return Integer.parseInt(value) == Integer.parseInt(((BitWord) other).value);
		}
		else
		{
			return false;
		}
	}
}

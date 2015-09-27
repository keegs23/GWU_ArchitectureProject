public class BitWord {
	public static final String DEFAULT_VALUE = "0000000000000000";	//16 zero bits
	
	protected final String value;
	
	public BitWord()
	{
		value = DEFAULT_VALUE;
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

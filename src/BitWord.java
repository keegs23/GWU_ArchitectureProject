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
}

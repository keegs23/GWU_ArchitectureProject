public class BitWord {
	private static final String DEFAULT_VALUE = "0000000000000000";
	
	private String value;
	
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
	
	public void setValue(String val)
	{
		value = val;
	}
	
	public int getSize()
	{
		return value.length();
	}
}

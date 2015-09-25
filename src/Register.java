public class Register
{
    public final int BitSize;
    public BitWord BitValue;
    
    public Register(int bitSize) 
    {        
        BitSize = bitSize;
        String tempBitValue = "";
        for (int i = 0; i < BitSize; i++)
        {
            tempBitValue += "0";
        }
        BitValue = new BitWord(tempBitValue);
    }
    
    public int getBitSize()
    {
    	return BitSize;
    }
    
    public BitWord getBitValue()
    {
    	return BitValue;
    }
    
    public void setBitValue(String value)
    {
    	if(value.length() == BitSize)
    	{
    		BitValue = new BitWord(value);
    	}
    	else
    	{
    		// TODO: Handle value of wrong size
    	}
    }
}
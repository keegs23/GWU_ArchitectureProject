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
}
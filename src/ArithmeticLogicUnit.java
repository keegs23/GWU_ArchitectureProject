import java.util.HashMap;
import java.util.Map;

/**
 * Arithmetic utility class
 */
public final class ArithmeticLogicUnit {
		
        public static final String KEY_DIFFERENCE = "difference";
        public static final String KEY_QUOTIENT = "quotient";
        public static final String KEY_REMAINDER = "remainder";
        public static final String KEY_ISDIVZERO = "isDivZero"; 
        public static final String KEY_ISUNDERFLOW = "isUnderflow";

	/**
	 * Adds the 2 binary bit Strings
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary sum of bitStr1 and bitStr2 as a 16-bit String
	 */
	public static String add(String bitStr1, String bitStr2)
	{
		// How closely were we supposed to mimic how the computer actually does bit addition?
		
		String sum = "";
		
		// Make sure both are 16 bits
		String bits1 = padZeros(bitStr1);
		String bits2 = padZeros(bitStr2);
		
		int carryIn = 0;
		int carryOut = 0;
		
		for(int k = 15; k >= 0; k--)
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
	 * Checks if underflow flag must be raised
	 * @param bitStr1
	 * @param bitStr2
	 * @return true if bitStr2 > bitStr1
	 */
	public static boolean checkUnderflow(String bitStr1, String bitStr2)
	{
		int firstString = Integer.parseInt(bitStr1);
		int secondString = Integer.parseInt(bitStr2);
		
		return secondString > firstString;
	}
	
	/**
	 * Subtracts the 2 binary bit Strings
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary difference of bitStr1 and bitStr2 as a 16-bit String
	 */
	public static Map<String, Object> subtract(String bitStr1, String bitStr2)
	{
		// Currently only works when both are non-negative numbers
		
		String difference = "";
		boolean borrow = false;
		boolean isUnderflow = checkUnderflow(bitStr1, bitStr2);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		// Make sure both are 16 bits
		String bits1;
		String bits2;
		
		if (isUnderflow)
		{
			bits1 = padZeros(bitStr2);
			bits2 = padZeros(bitStr1);
		}
		else
		{
			bits1 = padZeros(bitStr1);
			bits2 = padZeros(bitStr2);
		}
		
		if (Integer.parseInt(bits1) == Integer.parseInt(bits2))
		{
			difference = BitWord.VALUE_ZERO;
		}
		else if (Integer.parseInt(bits2) == 0)
		{
			difference = bits1;
		}
		else
		{
			for (int k = 15; k >= 0; k--)
			{
				// Retrieve the next lowest bit
				int a = Integer.parseInt(bits1.substring(k, k+1));
				int b = Integer.parseInt(bits2.substring(k, k+1));
				
				if (borrow == true)
				{
					if (a == 1)
					{
						a = 0;
						borrow = false;
					}
					else
					{
						a = 1;
					}
				}
				
				if (b > a)
				{
					a = a + 1;
					borrow = true;
				}
				
				difference = (a - b) + difference;
			}
		}
		
		returnMap.put(KEY_DIFFERENCE, difference);
		returnMap.put(KEY_ISUNDERFLOW, isUnderflow);
		
		return returnMap;
	}
        
	  /**
	 * Multiplies Register by Register
	 * @param multiplier bit String with length <= 16
         * @param multiplicand bit String with length <= 16
	 * @return Up to 32 bit string
	 */
        public static String multiply(String multiplier, String multiplicand) {
            String product = "0";
            String zeroSuffix = "";
            char currentBit;
            //Make sure both strings are 16 bits
            multiplier = padZeros(multiplier);
            multiplicand = padZeros(multiplicand);
            //Loop on the length of the second string
            for (int i = multiplicand.length() - 1; i >= 0; i--) {   
                currentBit = multiplicand.charAt(i);
                if (currentBit == '1') {
                    product = add(product, multiplier + zeroSuffix);
                }                     
                zeroSuffix += "0";
            }
            
            return product;
        }
        
         /**
	 * Divides Register by Register
	 * @param dividend bit String with length <= 16
         * @param divisor bit String with length <= 16
	 * @return Map with Quotient, Remainder, and IsDivideByZero bit
	 */
        public static Map<String, String> divide(String dividend, String divisor) {              
            Map<String, String> divisionMap = new HashMap<String, String>();
            int dividendDecimal = Integer.parseInt(dividend, 2);
            int divisorDecimal = Integer.parseInt(divisor, 2);
            int isDivByZero = 0; //default to false
            
            //Check if dividing by zero
            if (padZeros(divisor).equals(BitWord.VALUE_ZERO))
                isDivByZero = 1;     
            
            //Default quotient and remainder to zero
            String quotient = BitWord.VALUE_ZERO;
            String remainder = BitWord.VALUE_ZERO;
            //Don't divide if divisor is zero
            if (isDivByZero == 0) {
                quotient = Integer.toBinaryString(dividendDecimal/divisorDecimal);
                remainder = Integer.toBinaryString(dividendDecimal%divisorDecimal);
            }

            divisionMap.put(KEY_QUOTIENT, quotient);
            divisionMap.put(KEY_REMAINDER, remainder);
            divisionMap.put(KEY_ISDIVZERO, Integer.toString(isDivByZero));

            return divisionMap;
        }         
        	
	/**
	 * Pads with leading zeros until length is 16
	 * @param value bit String with length <= 16
	 * @return 16-bit String
	 */
	public static String padZeros(String value)
	{
		return String.format("%016d", Long.parseLong(value));
	}
	/**
	 * Logical AND of RegisterP and RegisterQ
	 * @param p : which is a bitString
	 * @param q : which is a bitString
	 * @return c(p) <- c(p) AND c(q)
	 */		
	public static void and(String p, String q)
	{
	//register will be of length 16 bits
		int n = 16;
		for(int i = 0;i<n;i++)
		{
			String pbit = p.substring(i, i+1);
			String qbit = q.substring(i, i+1);

			String r = pbit + qbit;
			if (i==0)
			{
				//edge case: first bit
				if (r == "00" || r == "01" || r == "10")
				{
					p= "0" + p.substring(i+1, i+2);
				}
				else if (r == "11")
				{
					p= "1" + p.substring(i+1, i+2);
				}

			}
			else if (i==n)
			{// edge case:  last bit
				if (r == "00" || r == "01" || r == "10")
				{
					p= p.substring(i-1, i)+"0";
				}
				else if (r == "11")
				{
					p= p.substring(i-1, i)+"1";
				}

			}
			else
			{//general case
				if (r == "00" || r == "01" || r == "10")
				{
					p= p.substring(i-1, i)+"0"+ p.substring(i+1, i+2);
				}
				else if (r == "11")
				{
					p= p.substring(i-1, i)+"1"+ p.substring(i+1, i+2);
				}
			}
		}
	}
	
	
	/**
	 * Logical OR of RegisterP and RegisterQ
	 * @param p : which is a bitString
	 * @param q : which is a bitString
	 * @return c(p) <- c(p) OR c(q)
	 */	
	public static void or(String p, String q)
	{
	//register will be of length 16 bits
		int n = 16;
		for(int i = 0;i<n;i++)
		{
			String pbit = p.substring(i, i+1);
			String qbit = q.substring(i, i+1);

			String r = pbit + qbit;
			if (i==0)
			{
				//edge case: first bit
				if (r == "11" || r == "01" || r == "10")
				{
					p= "1" + p.substring(i+1, n);
				}
				else if (r == "00")
				{
					p= "0" + p.substring(i+1, n);
				}

			}
			else if (i==n)
			{// edge case:  last bit
				if (r == "11" || r == "01" || r == "10")
				{
					p= p.substring(0, i)+"1";
				}
				else if (r == "00")
				{
					p= p.substring(0, i)+"0";
				}

			}
			else
			{//general case
				if (r == "11" || r == "01" || r == "10")
				{
					p= p.substring(0, i)+"1"+ p.substring(i+1, n);
				}
				else if (r == "00")
				{
					p= p.substring(0, i)+"0"+ p.substring(i+1, n);
				}
			}
		}
	}
	/**
	 * Logical NOT of RegisterP ; i.e. switch "1's & 0's"
	 * @param p : which is a bitString
	 * @return c(p) <- NOT c(p) 
	 */		
	public static void not(String p)
	{
            //register will be of length 16 bits
            int n = 16;
            for(int i = 0;i<n;i++)
            {
                String r = p.substring(i, i+1);
                if (i==0)
                {
                        //edge case: first bit
                        if (r == "1")
                        {
                                p= "0" + p.substring(i+1, n); //switch the first bit, save the rest
                        }
                        else if (r == "0")
                        {
                                p= "1" + p.substring(i+1, n);
                        }

                }
                else if (i==n)
                {// edge case:  last bit
                        if (r == "0")
                        {
                                p= p.substring(0, i)+"1"; //save everything, but switch the last bit
                        }
                        else if (r == "1")
                        {
                                p= p.substring(0, i)+"0";
                        }

                }
                else
                {//general case
                        if (r == "0" )
                        {
                                p= p.substring(0, i)+"1"+ p.substring(i+1, n); //change the ith bit
                        }
                        else if (r == "1")
                        {
                                p= p.substring(0, i)+"0"+ p.substring(i+1, n);
                        }
                }
            }
	}
	/**
	 * SHIFT Register Command
	 * @param bitword : which is a bitString
	 * @return The register will shift left/right, logic/arithmetic, 1-15 units 
	 */	
	public static void src(String bitword)
	{
	//Parse bitword
	// this parse may have to occur in the main program and the Register passed in??
	String Opcode = bitword.substring(0, 6);
	Register r = bitword.substring(6, 8); //register
	String ArithmeticOrLogic = bitword.substring(8, 9);  //this is a flag to adjust for a sign bit; 0 = arithmetic and 1 = logic;
	String LeftOrRight = bitword.substring(9, 10);  // left = 1; right = 0;
	String sCount = bitword.substring(12, 16);
	//convert sCount from string to number, this will be the loop counter
	int n = Integer.parseInt(sCount);
	String buffer;
	String keeper;
	String shifted;

	String	Registervalue = r.getBitValue(); //getBitValue(r);

	// please note that the mechanics of the simple machine would in fact shift one bit at a time.
	// and then loop through the ALU again to perform additional shifts to keep the real estate on 
	// the chip small.  i.e. a 'two shift' isn't build into the hardware.
	
	/////we can use this same code for rotation by setting the Buffer = substring(0,1) or substring(15,16)////

	for(int i = 0;i<n;i++)
		{
			//shift values left
			if(LeftOrRight == "1")
			{
				keeper = Registervalue.substring(1, 16);
				buffer = "0";
				shifted = keeper + buffer;  ///shifted to the left
				if(ArithmeticOrLogic == "0")  //i.e. arithmetic shift
				{
					String overflow = Registervalue.substring(1, 2);
					if (overflow == "1" ) {BitWord SetOverflow = "1";}   /////////////do we have an ALU overflow flag set yet?????????????????????????
				}
			}
			else
			{
				//shift values right  
				keeper = Registervalue.substring(0, 15);
				if(ArithmeticOrLogic == "0")  //i.e. arithmetic shift
				{//if you are arithmetic shifting to the right, then you insert the sign bit
					buffer = Registervalue.substring(0, 1); 
				}
				else
				{
					buffer = "0"; //if you are logic shifting you insert a zero
				}
				shifted = buffer + keeper;  ///shifted to the right
			}
			Registervalue = shifted;  // this is to get ready to loop through one more time 
		}
	r.setBitValue(Registervalue);   // this is to exit with final answer
	}
	/**
	 * ROTATE Register Command
	 * @param bitword : which is a bitString
	 * @return The register will rotate left/right, 1-15 units 
	 */	
	public static void rrc(String bitword)
	{
	//Parse bitword
	// this parse may have to occur in the main program and the Register passed in??
	String Opcode = bitword.substring(0, 6);
	Register r = bitword.substring(6, 8); //register
	String ArithmeticOrLogic = bitword.substring(8, 9);  //this is a flag to adjust for a sign bit; 0 = arithmetic and 1 = logic;
	String LeftOrRight = bitword.substring(9, 10);  // left = 1; right = 0;
	String sCount = bitword.substring(12, 16);
	//convert sCount from string to number, this will be the loop counter
	int n = Integer.parseInt(sCount);
	String buffer;
	String keeper;
	String shifted;

	String	Registervalue = r.getBitValue(); //getBitValue(r);

	// please note that the mechanics of the simple machine would in fact shift one bit at a time.
	// and then loop through the ALU again to perform additional shifts to keep the real estate on 
	// the chip small.  i.e. a 'two shift' isn't build into the hardware.

	for(int i = 0;i<n;i++)
		{
			//shift values left
			if(LeftOrRight == "1")
			{
				keeper = Registervalue.substring(1, 16);
				buffer = Registervalue.substring(0, 1);
				shifted = keeper + buffer;  ///shifted to the left
			}
			else
			{
				//shift values right  
				keeper = Registervalue.substring(0, 15);
				buffer = Registervalue.substring(15, 16);
				shifted = buffer + keeper;  ///shifted to the right
			}
			Registervalue = shifted;  // this is to get ready to loop through one more time 
		}
	r.setBitValue(Registervalue);   // this is to exit with final answer
	}			
	
}

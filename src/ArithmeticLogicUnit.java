import java.util.HashMap;
import java.util.Map;

/**
 * Arithmetic utility class
 */
public final class ArithmeticLogicUnit {
		
        public static final String KEY_SUM = "sum";
		public static final String KEY_DIFFERENCE = "difference";
		public static final String KEY_PRODUCT = "product";
        public static final String KEY_QUOTIENT = "quotient";
        public static final String KEY_REMAINDER = "remainder";
        public static final String KEY_ISDIVZERO = "isDivZero"; 
        public static final String KEY_ISUNDERFLOW = "isUnderflow";
        public static final String KEY_ISOVERFLOW = "isOverflow";
        public static final String KEY_REGISTERVALUE = "RegisterValue";

	/**
	 * Adds the 2 binary bit Strings
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary sum of bitStr1 and bitStr2 as a 16-bit String
	 */
	public static Map<String, Object> add(String bitStr1, String bitStr2)
	{
		// How closely were we supposed to mimic how the computer actually does bit addition?
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String sum = "";
		
		// Make sure both are 16 bits
		String bits1 = padZeros16(bitStr1);
		String bits2 = padZeros16(bitStr2);
		
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
		
		result.put(KEY_SUM, sum);
		
		// Check for overflow
		boolean isOverflow = carryOut > 0;
		result.put(KEY_ISOVERFLOW, isOverflow);
		
		return result;
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
		
		final int base = 2; // 2 for binary
		
		String difference = "";
		boolean borrow = false;
		boolean isUnderflow = checkUnderflow(bitStr1, bitStr2);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		// Make sure both are 16 bits
		String bits1;
		String bits2;
		
		if (isUnderflow)
		{
			bits1 = padZeros16(bitStr2);
			bits2 = padZeros16(bitStr1);
		}
		else
		{
			bits1 = padZeros16(bitStr1);
			bits2 = padZeros16(bitStr2);
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
					if (a > 0)
					{
						a--;
						borrow = false;
					}
					else
					{
						a = base - 1;
					}
				}
				
				if (b > a)
				{
					a = a + base;
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
        public static Map<String, Object> multiply(String multiplier, String multiplicand) {
        	Map<String, Object> result = new HashMap<String, Object>();
            String product = "0";
            String zeroSuffix = "";
            char currentBit;
            boolean isOverflow = false;
            //Make sure both strings are 16 bits
            multiplier = padZeros16(multiplier);
            multiplicand = padZeros16(multiplicand);
            //Loop on the length of the second string
            for (int i = multiplicand.length() - 1; i >= 0; i--) {   
                currentBit = multiplicand.charAt(i);
                if (currentBit == '1') {
                	Map<String, Object> sumResult = add(product, multiplier + zeroSuffix);
                    product = (String) sumResult.get(KEY_SUM);
                    // Don't let isOverflow be changed from true back to false
                    if((boolean) sumResult.get(KEY_ISOVERFLOW)) {
                    	// Shouldn't ever get here...16 bits multiplied by 16 bits will always be within 32 bits
                    	isOverflow = true;
                    }
                }                     
                zeroSuffix += "0";
            }
            
            result.put(KEY_PRODUCT, padZeros32(product));
            result.put(KEY_ISOVERFLOW, isOverflow);
            
            return result;
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
            if (padZeros16(divisor).equals(BitWord.VALUE_ZERO))
                isDivByZero = 1;     
            
            //Default quotient and remainder to zero
            String quotient = BitWord.VALUE_ZERO;
            String remainder = BitWord.VALUE_ZERO;
            //Don't divide if divisor is zero
            if (isDivByZero == 0) {
                quotient = Integer.toBinaryString(dividendDecimal/divisorDecimal);
                remainder = Integer.toBinaryString(dividendDecimal%divisorDecimal);
            }

            divisionMap.put(KEY_QUOTIENT, padZeros16(quotient));
            divisionMap.put(KEY_REMAINDER, padZeros16(remainder));
            divisionMap.put(KEY_ISDIVZERO, Integer.toString(isDivByZero));

            return divisionMap;
        }         
        	
	/**
	 * Pads with leading zeros until length is 16
	 * @param value bit String with length <= 16
	 * @return 16-bit String
	 */
	public static String padZeros16(String value)
	{
            return String.format("%016d", Long.parseLong(value));
	}
        
        public static String padZeros32(String value)
        {              
            return String.format("%32s", value).replace(' ', '0');
        }        
        
	/**
	 * Logical AND of RegisterP and RegisterQ
	 * @param p : which is a bitString
	 * @param q : which is a bitString
	 * @return c(p) <- c(p) AND c(q)
	 */		
	public static String and(String p, String q)
	{
            //register will be of length 16 bits
            int n = 16;
            p = padZeros16(p);
            q = padZeros16(q);
            String pbuild = null;
            for(int i = 0;i<n;i++)
            {
                String pbit = p.substring(i, i+1);
                String qbit = q.substring(i, i+1);

                String r = pbit + qbit;
                if (i==0)
                {
                    //edge case: first bit
                    if (r.equals("00") || r.equals("01") || r.equals("10"))
                    {
                            pbuild = "0";
                    }
                    else if (r.equals("11"))
                    {
                            pbuild = "1";
                    }
                }
                else 
                {// general case: second through last bit
                        if (r.equals("00") || r.equals("01") || r.equals("10"))
                        {
                                pbuild = pbuild + "0";
                        }
                        else if (r.equals("11"))
                        {
                                pbuild = pbuild + "1";
                        }

                }
            }
            return pbuild;
	}		
		
        /**
         * Logical OR of RegisterP and RegisterQ
         * @param p : which is a bitString
         * @param q : which is a bitString
         * @return c(p) <- c(p) OR c(q)
         */	
        public static String orr(String p, String q)
        {
            //register will be of length 16 bits
            int n = 16;
            p = padZeros16(p);
            q = padZeros16(q);
            String pbuild = null;
            for(int i = 0;i<n;i++)
            {
                String pbit = p.substring(i, i+1);
                String qbit = q.substring(i, i+1);

                String r = pbit + qbit;
                if (i==0)
                {
                    //edge case: first bit
                    if (r.equals("11") || r.equals("01") || r.equals("10"))
                    {
                            pbuild = "1";
                    }
                    else if (r.equals("00"))
                    {
                            pbuild = "0";
                    }
                }
                else 
                {// general case: second through last bit
                        if (r.equals("11") || r.equals("01") || r.equals("10"))
                        {
                                pbuild = pbuild + "1";
                        }
                        else if (r.equals("00"))
                        {
                                pbuild = pbuild + "0";
                        }
                }
            }
            return pbuild;
        }
        /**
         * Logical NOT of RegisterP ; i.e. switch "1's & 0's"
         * @param p : which is a bitString
         * @return c(p) <- NOT c(p) 
         */		
        public static String not(String p)
        {
            //register will be of length 16 bits
            int n = 16;
            p = padZeros16(p);
            for(int i = 0;i<n;i++)
            {
                String r = p.substring(i, i+1);
                if (i==0)
                {
                    //edge case: first bit
                    if (r.equals("1"))
                    {
                        p= "0" + p.substring(i+1, n); //switch the first bit, save the rest
                    }
                    else if (r.equals("0"))
                    {
                        p= "1" + p.substring(i+1, n);
                    }
                }
                else if (i==n)
                {
                    // edge case:  last bit
                    if (r.equals("0"))
                    {
                        p= p.substring(0, i)+"1"; //save everything, but switch the last bit
                    }
                    else if (r.equals("1"))
                    {
                        p= p.substring(0, i)+"0";
                    }
                }
                else
                {
                    //general case
                    if (r.equals("0") )
                    {
                        p= p.substring(0, i)+"1"+ p.substring(i+1, n); //change the ith bit
                    }
                    else if (r.equals("1"))
                    {
                        p= p.substring(0, i)+"0"+ p.substring(i+1, n);
                    }
                }
            }
            return p;
        }
        /**
         * SHIFT Register Command
         * @param bitword : which is a bitString
         * @return The register will shift left/right, logic/arithmetic, 1-15 units 
         */	
        public static Map<String, Object> src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
        {
            //Parse bitword
            // this parse may have to occur in the main program and the Register passed in??
                    ///bitInstruction Class - ParseInstruction  --- call from minicomputer in SingleStep

            //String Opcode = bitword.substring(0, 6);
            //String r = bitword.substring(6, 8); //register
            //String ArithmeticOrLogic = bitword.substring(8, 9);  //this is a flag to adjust for a sign bit; 0 = arithmetic and 1 = logic;
            //String LeftOrRight = bitword.substring(9, 10);  // left = 1; right = 0;
            //String sCount = bitword.substring(12, 16);
            //convert sCount from string to number, this will be the loop counter
            int n = Integer.parseInt(sCount,2);
            String buffer;
            String keeper;
            String shifted;
            boolean isOverFlow = false;
            Map<String,Object> returnMap = new HashMap<String,Object>();

            // please note that the mechanics of the simple machine would in fact shift one bit at a time.
            // and then loop through the ALU again to perform additional shifts to keep the real estate on 
            // the chip small.  i.e. a 'two shift' isn't build into the hardware.

            /////we can use this same code for rotation by setting the Buffer = substring(0,1) or substring(15,16)////
            
            Registervalue = padZeros16(Registervalue);
            for(int i = 0;i<n;i++)
                    {
                            //shift values left
                            if(LeftOrRight.equals("1"))
                            {
                                    keeper = Registervalue.substring(1, 16);
                                    buffer = "0";
                                    shifted = keeper + buffer;  ///shifted to the left
                                    if(ArithmeticOrLogic.equals("0"))  //i.e. arithmetic shift
                                    {
                                            String overflow = Registervalue.substring(0, 1);
                                            if (overflow.equals("1")) {isOverFlow = true;}
                                    }
                            }
                            else
                            {
                                    //shift values right  
                                    keeper = Registervalue.substring(0, 15);
                                    if(ArithmeticOrLogic.equals("0"))  //i.e. arithmetic shift
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
            returnMap.put(KEY_REGISTERVALUE, Registervalue);
            returnMap.put(KEY_ISOVERFLOW, isOverFlow);
            // this is to exit with final answer
            return returnMap;
        }
        /**
         * ROTATE Register Command
         * @param bitword : which is a bitString
         * @return The register will rotate left/right, 1-15 units 
         */	
        public static String rrc(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
        {
            //Parse bitword
            // this parse may have to occur in the main program and the Register passed in??
            //String Opcode = bitword.substring(0, 6);
            //Register r = bitword.substring(6, 8); //register
            //String ArithmeticOrLogic = bitword.substring(8, 9);  //this is a flag to adjust for a sign bit; 0 = arithmetic and 1 = logic;
            //String LeftOrRight = bitword.substring(9, 10);  // left = 1; right = 0;
            //String sCount = bitword.substring(12, 16);
            //convert sCount from string to number, this will be the loop counter
            int n = Integer.parseInt(sCount,2);
            String buffer;
            String keeper;
            String shifted;


            // please note that the mechanics of the simple machine would in fact shift one bit at a time.
            // and then loop through the ALU again to perform additional shifts to keep the real estate on 
            // the chip small.  i.e. a 'two shift' isn't build into the hardware.

            Registervalue = padZeros16(Registervalue);
            for(int i = 0;i<n;i++)
                    {
                            //shift values left
                            if(LeftOrRight.equals("1"))
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
            return Registervalue;   // this is to exit with final answer
        }	
}

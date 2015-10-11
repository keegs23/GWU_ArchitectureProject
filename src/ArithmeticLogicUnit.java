/**
 * Arithmetic utility class
 */
public final class ArithmeticLogicUnit {

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
	 * Subtracts the 2 binary bit Strings
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary difference of bitStr1 and bitStr2 as a 16-bit String
	 */
	public static String subtract(String bitStr1, String bitStr2)
	{
		// Currently only works when bitStr1 >= bitStr2 and both are non-negative numbers
		
		String difference = "";
		boolean borrow = false;
		
		// Make sure both are 16 bits
		String bits1 = padZeros(bitStr1);
		String bits2 = padZeros(bitStr2);
		
		if (Integer.parseInt(bits1) == Integer.parseInt(bits2))
		{
			return BitWord.DEFAULT_VALUE;
		}
		if (Integer.parseInt(bits2) == 0)
		{
			return bits1;
		}
		
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
			
			if (a >= b)
			{
				difference = (a - b) + difference;
			}
			else
			{
				difference = "1" + difference;
				borrow = true;
			}
		}
		
		return difference;
	}
	
	/**
	 * Pads with leading zeros until length is 16
	 * @param value bit String with length <= 16
	 * @return 16-bit String
	 */
	public static String padZeros(String value)
	{
		return String.format("%016d", Integer.parseInt(value));
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
}

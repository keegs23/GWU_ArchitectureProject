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
			return BitWord.VALUE_ZERO;
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
}

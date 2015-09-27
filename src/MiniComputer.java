import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MiniComputer
{
	public static final String MAX_MEMORY_ADDRESS = "0000011111111111";	//11 one bits = 2048 (decimal)
	
	/**
	 * Program Counter
	 */
	private Register PC;
	/**
	 * Condition Code Register
	 */
	private Register CC;
	/**
	 * Instruction Register
	 */
	private Register IR;
	/**
	 * Memory Address Register
	 */
	private Register MAR;
	/**
	 * Memory Buffer Register
	 */
	private Register MBR;
	/**
	 * Machine Status Register
	 */
	private Register MSR;
	/**
	 * Machine Fault Register
	 */
	private Register MFR;
	/**
	 * Internal Address Register
	 */
	private Register IAR;
	/**
	 * Internal Result Register
	 */
	private Register IRR;
	
	// General Purpose Registers
	private Register R0;
	private Register R1;
	private Register R2;
	private Register R3;
	
	// Index Registers
	private Register X1;
	private Register X2;
	private Register X3;
	
	/**
	 * Key = String Address, Value = MemoryLocation object
	 */
	Map<String, MemoryLocation> memory;
	
	public MiniComputer()
	{
		// Initialize Registers (sizes specified by the table in the project description)
		PC = new Register(12); //register size is 12 bits
		CC = new Register(4); //register size is 4 bits
		IR = new Register(16); //register size is 16 bits
		MAR = new Register(16); //register size is 16 bits
		MBR = new Register(16); //register size is 16 bits
		MSR = new Register(16); //register size is 16 bits
		MFR = new Register(4); //register size is 4 bits
		IAR = new Register(16);
		IRR = new Register(16);
		R0 = new Register(16);
		R1 = new Register(16);
		R2 = new Register(16);
		R3 = new Register(16);
		X1 = new Register(16);
		X2 = new Register(16);
		X3 = new Register(16);
		
		// Initialize Memory
		memory = new HashMap<String, MemoryLocation>();
	}
	
	/* Register getters */
	
	public Register getPC()
	{
		return PC;
	}

	public Register getCC()
	{
		return CC;
	}
	public Register getIR()
	{
		return IR;
	}
	public Register getMAR()
	{
		return MAR;
	}
	public Register getMBR()
	{
		return MBR;
	}
	public Register getMSR()
	{
		return MSR;
	}
	public Register getMFR()
	{
		return MFR;
	}
	public Register getIAR()
	{
		return IAR;
	}
	public Register getIRR()
	{
		return IRR;
	}
	/**
	 * Retrieves the General Purpose Register indicated by r.
	 * @param r 0-3
	 * @return R0, R1, R2, R3, or null
	 */
	public Register getR(int r)
	{
		switch(r) {
			case 0:
				return R0;
			case 1:
				return R1;
			case 2:
				return R2;
			case 3:
				return R3;
			default:
				return null;
		}
	}
	
	/**
	 * Retrieves the Index Register indicated by x.
	 * @param x 1-3
	 * @return X1, X2, X3, or null
	 */
	public Register getX(int x)
	{
		switch(x) {
			case 1:
				return X1;
			case 2:
				return X2;
			case 3:
				return X3;
			default:
				return null;
		}
	}
	
	/* End Register Getters */
	
	public static void main(String[] args)
	{
		// TODO: mimic flowchart from Lecture 1
	}
	
	/**
	 * Loads the memory from rom.txt.
	 * Called when IPL button is pressed.
	 */
	public void loadROM()
	{
		// Read in and parse rom.txt 
		// rom.txt is formatted so that each instruction is on a separate line, first 16 characters of each line are the instruction, rest are comments
		// Make sure to only read the first 16 characters of each row (i.e. ignore the comments)
		File file = new File("rom.txt");
		try 
		{
			FileInputStream fileIn = new FileInputStream(file);
			
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
		 
			String line = null;
			String address = MemoryLocation.ADDRESS_BOOT_PRGM_START;
			while ((line = br.readLine()) != null) {
				// Read the 16-bit instruction
				String instruction = line.substring(0, 16);
				
				// Store the instruction in memory
				
				// Increment address
			}
		 
			br.close();
		}
		catch(Exception ex)
		{
			// TODO: Handle exceptions
			System.out.println(ex);
		}
	}
	
	public void loadToggleInstruction(String instruction)
	{
		// Store the toggle inputs into MemoryLocation.RESERVED_ADDRESS_TOGGLE_INSTRUCTION
		memory.put(MemoryLocation.RESERVED_ADDRESS_TOGGLE_INSTRUCTION, 
				new MemoryLocation(MemoryLocation.RESERVED_ADDRESS_TOGGLE_INSTRUCTION, instruction));
	}
	
	/**
	 * Executes the instruction located at the address indicated by the PC
	 * Assumes GUI has already stored the instruction address in PC
	 */
	public void singleStep()
	{///////////////////////////////////under CONSTRUCTION//////////////////////////
    	int register;
    	int index; 
    	boolean isIndirectAddress; 
    	BitWord address; 
		
		// Retrieve PC value
		BitWord ea = getPC().getBitValue();
		
		//Throw error checking on address value here!!!
				
		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Transfer address to MAR
		MAR.setBitValue(IAR.getBitValue());
		
		// Fetch word from memory located at address specified by MAR into MBR
		if(memory.containsKey(MAR.getBitValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue()).getValue());
		}
		else
		{			
			MBR.setBitValue(BitWord.DEFAULT_VALUE);
		}
		//
		
		BitInstruction instruction = new BitInstruction(MBR.getBitValue());
		// Parse instruction
		Map<String, BitWord> instructionParse = instruction.ParseInstruction();
		
		boolean isTransferInstruction = false;
		
		// Switch-case on opcode to call the appropriate instruction method
		BitWord opcode = instructionParse.get(BitInstruction.KEY_OPCODE);
        switch (opcode.getValue())
        {
            case OpCode.HLT:
                //TODO
                break;
            case OpCode.TRAP:
                //TODO
                break;
            case OpCode.LDR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue()); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue()); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	ldr(register, index, isIndirectAddress, address);
                break;
            case OpCode.STR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue()); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue()); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	str(register, index, isIndirectAddress, address);
                break;
            case OpCode.LDA:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue()); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue()); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	lda(register, index, isIndirectAddress, address);
                break;
            case OpCode.LDX:
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue()); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	ldx(index, isIndirectAddress, address);
                break;
            case OpCode.STX:
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue()); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	stx(index, isIndirectAddress, address);
                break;
            case OpCode.AMR:
                //TODO
                break;
            case OpCode.SMR:
                //TODO
                break;
            case OpCode.AIR:
                //TODO
                break;
            case OpCode.SIR:
                //TODO
                break;
            default:
                break;                        
        }
		
		// Update PC with address of next instruction (GUI will call getPC().getBitValue() when updating the text box
        if(isTransferInstruction)
        {
        	// TODO
        }
        else
        {
        	// Increment PC
        }
	}
	
	/* Instruction methods */
	
	/**
	 * Load Register From Memory
	 * Loads the value of the effective address into the specified register
	 * @param register 0-3
	 * @param index 0-3 (0 if no indexing)
	 * @param isIndirectAddress
	 * @param address
	 */
	public void ldr(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);

		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Move the contents of IAR to the MAR
		MAR.setBitValue(IAR.getBitValue());
		
		// TODO: Check that address specified by MAR is valid (not reserved, not larger than max)
		
		// Fetch the contents in memory at the address specified by MAR into the MBR
		if(memory.containsKey(MAR.getBitValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue()).getValue());
		}
		else
		{			
			MBR.setBitValue(BitWord.DEFAULT_VALUE);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR.setBitValue(MBR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR.getBitValue());
		}
	}
	
	/**
	 * Store Register To Memory
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void str(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)	
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Since this instruction does not retrieve from memory, MAR and MBR will not be used for this instruction
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Move the contents of the specified register to the IRR
		if(registerSelect1 != null)
		{
			IRR.setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR.setBitValue(BitWord.DEFAULT_VALUE);
		}
		
		// Move contents of IRR to memory at address specified by IAR		
		MemoryLocation memLoc = new MemoryLocation(IAR.getBitValue(), IRR.getBitValue());
		// HashMap.put() automatically replaces the value and adds a new key if necessary 
		memory.put(IAR.getBitValue().getValue(), memLoc);
	}
	
	/**
	 * Load Register With Address
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void lda(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified Index Register (IR a.k.a X)
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);

		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// MAR and MBR are not used because this instruction does not fetch from memory
		// Move the data from the IAR to an Internal Result Register (IRR)
		IRR.setBitValue(IAR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR.getBitValue());
		}
	}
	
	/**
	 * Load Index Register From Memory
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void ldx(int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified Index Register (IR a.k.a X)
		Register registerSelect1 = getX(index);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);

		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Move the contents of IAR to the MAR
		MAR.setBitValue(IAR.getBitValue());
		
		// TODO: Check that address specified by MAR is valid (not reserved, not larger than max)
		
		// Fetch the contents in memory at the address specified by MAR into the MBR
		if(memory.containsKey(MAR.getBitValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue()).getValue());
		}
		else
		{			
			MBR.setBitValue(BitWord.DEFAULT_VALUE);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR.setBitValue(MBR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR.getBitValue());
		}
	}
	
	/**
	 * Store Index Register To Memory
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void stx(int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified index register
		Register indexSelect1 = getX(index);
		
		// Calculate the effective address (EA)	
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Since this instruction is not fetching from memory, MAR and MBR will not be used
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Move the contents of the specified register to the IRR
		if (indexSelect1 != null) {
			IRR.setBitValue(indexSelect1.getBitValue());
		} else {
			// Should never reach here, but just in case
			IRR.setBitValue(new BitWord());
		}
		
		// Move contents of IRR to memory at address specified by IAR		
		MemoryLocation memLoc = new MemoryLocation(IAR.getBitValue(), IRR.getBitValue());
		// HashMap.put() automatically replaces the value and adds a new key if necessary 
		memory.put(IAR.getBitValue().getValue(), memLoc);
	}
	
	// TODO in later parts: other instructions
	
	/* End Instruction methods */
	
	/* Helpers */
	
	public BitWord calculateEffectiveAddress(int indexRegister, boolean isIndirectAddress, BitWord address)
	{		
		if(!isIndirectAddress)
		{
			if(indexRegister == 0)
			{
				return address;
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
						
				String ea = addHelper(indexValue, toAdd);
						
				return new BitWord(ea);
			}
			else
			{
				// Should never reach here, but just in case
				return address;	//does it make sense to return this?
			}
		}
		else
		{
			String addr = "";
			
			if(indexRegister == 0)
			{
				addr = address.getValue();
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
				
				addr = addHelper(indexValue, toAdd);
			}
			
			// TODO: Check that addr is valid (not reserved, not larger than max)
			
			if(memory.containsKey(addr))	//MemoryLocation
			{
				return memory.get(addr).getValue();
			}
			else
			{
				return new BitWord();	//value is 0000000000000000 (16 zero bits)
			}
		}
	}
	
	/**
	 * Adds the 2 binary bit Strings
	 * TODO: Probably move to a new Arithmetic Logic Unit (ALU) class
	 * @param bitStr1
	 * @param bitStr2
	 * @return the binary sum of bitStr1 and bitStr2
	 */
	private String addHelper(String bitStr1, String bitStr2)
	{
		// How closely were we supposed to mimic how the computer actually does bit addition?
		
		String sum = "";
		
		// Make sure both are 16 bits
		String bits1 = padZeros(bitStr1);
		String bits2 = padZeros(bitStr2);
		
		int carryIn = 0;
		int carryOut = 0;
		
		for(int k = 15; k <= 0; k--)
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
	 * Pads with leading zeros until length is 16
	 * @param value bit String with length <= 16
	 * @return 16-bit String
	 */
	private static String padZeros(String value)
	{
		return String.format("%016d", Integer.parseInt(value));
	}
	
	/* End Helpers */
}
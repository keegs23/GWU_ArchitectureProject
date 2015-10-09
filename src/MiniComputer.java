import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

public class MiniComputer extends Observable
{
	public static final String MAX_MEMORY_ADDRESS = "0000011111111111";	//11 one bits = 2048 (decimal)
	
	/**
	 * Program Counter (12 bits)
	 */
	private Register PC;
	/**
	 * Condition Code Register (4 bits)
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
	 * Machine Fault Register (4 bits)
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
	private Map<String, MemoryLocation> memory;
	
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
		memory = new TreeMap<String, MemoryLocation>();
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
	
	public Map<String, MemoryLocation> getMemory()
	{
		return memory;
	}
	
	
	/**
	 * Loads the ROM contents.
	 * Called when IPL button is pressed.
	 */
	public void loadROM()
	{
		// Read in and parse rom.txt 
		// rom.txt is formatted so that each instruction is on a separate line, first 16 characters of each line are the instruction, rest are comments
		// Make sure to only read the first 16 characters of each row (i.e. ignore the comments)
		File file = new File("BootProgram.txt");
		try 
		{
			FileInputStream fileIn = new FileInputStream(file);
			
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
		 
			int bootPrgmLength = 0;
			String line = null;
			String bootPrgmStart16Bits = MemoryLocation.ADDRESS_BOOT_PRGM_START;
			String address = bootPrgmStart16Bits;
			while ((line = br.readLine()) != null) {
				// Read the 16-bit instruction
				String instruction = line.substring(0, 16);
				bootPrgmLength++;
				
				// Store the instruction in memory
				// Since technically the ROM is already supposed to be in the computer,
				// we will input it directly into memory
				// instead of constructing instructions to input the boot program instructions into memory
				MemoryLocation memLoc = new MemoryLocation(address, instruction);
				memory.put(address, memLoc);
				
				// Increment address
				address = ArithmeticLogicUnit.addHelper(address, "1");
			}
			
			br.close();
			
			// Execute boot program (mostly load/store)
			// PC can only hold 12 bits, so chop off the leading zeros
			String bootPrgmStart12Bits = bootPrgmStart16Bits.substring(4, 16);
			PC.setBitValue(bootPrgmStart12Bits);
			for(int k = 1; k <= bootPrgmLength; k++)
			{
				singleStep();
			}
			
			// Set PC back to the start of the boot program
			// PC can only hold 12 bits, so chop off the leading zeros
			PC.setBitValue(bootPrgmStart12Bits);
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
		
		// Transfer PC value to MAR
		MAR.setBitValue(ArithmeticLogicUnit.padZeros(PC.getBitValue().getValue()));
		
		// TODO: Check if address is valid
		
		// Fetch word from memory located at address specified by MAR into MBR
		if(memory.containsKey(MAR.getBitValue().getValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue().getValue()).getValue());
		}
		else
		{			
			MBR.setBitValue(BitWord.DEFAULT_VALUE);
		}
		
		// Load instruction from MBR into IR
		IR.setBitValue(MBR.getBitValue());
		
		// Parse instruction
		BitInstruction instruction = new BitInstruction(IR.getBitValue());
		Map<String, BitWord> instructionParse = instruction.ParseInstruction();
		
		// Switch-case on opcode to call the appropriate instruction method
		boolean isTransferInstruction = false;
		BitWord opcode = instructionParse.get(BitInstruction.KEY_OPCODE);
        switch (opcode.getValue())
        {
            case OpCode.HLT:
                //TODO in Part II
                break;
            case OpCode.TRAP:
                //TODO in Part II
                break;
            case OpCode.LDR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	ldr(register, index, isIndirectAddress, address);
                break;
            case OpCode.STR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	str(register, index, isIndirectAddress, address);
                break;
            case OpCode.LDA:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	lda(register, index, isIndirectAddress, address);
                break;
            case OpCode.LDX:
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	ldx(index, isIndirectAddress, address);
                break;
            case OpCode.STX:
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	stx(index, isIndirectAddress, address);
                break;
            case OpCode.AMR:
                //TODO in Part II
                break;
            case OpCode.SMR:
                //TODO in Part II
                break;
            case OpCode.AIR:
                //TODO in Part II
                break;
            case OpCode.SIR:
                //TODO in Part II
                break;
            default:
                break;                        
        }
		
		// Update PC with address of next instruction (GUI will call getPC().getBitValue() when updating the text box
        if(isTransferInstruction)
        {
        	// TODO in Part II
        }
        else
        {
        	// Increment PC
        	String pc = ArithmeticLogicUnit.addHelper(PC.getBitValue().getValue(), "1");
        	// PC can only hold 12 bits, so chop off the leading zeros
        	pc = pc.substring(4, 16);
        	PC.setBitValue(pc);
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
		if(memory.containsKey(MAR.getBitValue().getValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue().getValue()).getValue());
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
		// TreeMap.put() automatically replaces the value and adds a new key if necessary 
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
		// Pass in 0 for index since it is used to specify the index register to load into instead of for addressing like usual
		BitWord ea = calculateEffectiveAddress(0, isIndirectAddress, address);

		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// Move the contents of IAR to the MAR
		MAR.setBitValue(IAR.getBitValue());
		
		// TODO: Check that address specified by MAR is valid (not reserved, not larger than max)
		
		// Fetch the contents in memory at the address specified by MAR into the MBR
		if(memory.containsKey(MAR.getBitValue().getValue()))
		{
			MBR.setBitValue(memory.get(MAR.getBitValue().getValue()).getValue());
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
		// Pass in 0 for index since it is used to specify the index register to store into instead of for addressing like usual
		BitWord ea = calculateEffectiveAddress(0, isIndirectAddress, address);
		
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
		// TreeMap.put() automatically replaces the value and adds a new key if necessary 
		memory.put(IAR.getBitValue().getValue(), memLoc);
	}
	
	/**
	 * Input character to register from device
	 * @param register
	 * @param devId
	 */
	public void in(int register, BitWord devId)
	{
		setChanged();
		notifyObservers(OpCode.IN);
		// TODO
	}
	
	/**
	 * Output character to device from register
	 * @param register
	 * @param devId
	 */
	public void out(int register, BitWord devId)
	{
		setChanged();
		notifyObservers(OpCode.OUT);
		// TODO
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
				return new BitWord(ArithmeticLogicUnit.padZeros(address.getValue()));
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
						
				String ea = ArithmeticLogicUnit.addHelper(indexValue, toAdd);
						
				return new BitWord(ea);
			}
			else
			{
				// Should never reach here, but just in case
				return new BitWord(ArithmeticLogicUnit.padZeros(address.getValue()));	//does it make sense to return this?
			}
		}
		else
		{
			String addr = "";
			
			if(indexRegister == 0)
			{
				addr = ArithmeticLogicUnit.padZeros(address.getValue());
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
				
				addr = ArithmeticLogicUnit.addHelper(indexValue, toAdd);
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
	
	/* End Helpers */
}
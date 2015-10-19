import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.Vector;

public class MiniComputer extends Observable implements Runnable
{
	public static final String MAX_MEMORY_ADDRESS = "0000011111111111";	//11 one bits = 2048 (decimal)
	public static final String LOG_FILE_NAME = "trace-file.txt";
	public static final String BOOT_PROGRAM_NAME = "BootProgram.txt";
	public static final String PROGRAM_ONE_NAME = "Program1.txt";
	public final PrintWriter logger;
	
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
	 * Internal Result Register Array
	 */
	private Register[] IRR;
	
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
	/**
	 * Size 16
	 */
	private Vector<CacheLine> cache;
	
	private IOObject inputObject;
	private IOObject outputObject;
	private boolean[] registerIsInt;
	private int bootPrgmLength;
	private int prgmOneLength;
	
	
	public MiniComputer() throws FileNotFoundException
	{
		// Initialize logger
		logger = new PrintWriter(new File(LOG_FILE_NAME));
		
		// Initialize Registers (sizes specified by the table in the project description)
		PC = new Register(12); //register size is 12 bits
		CC = new Register(4); //register size is 4 bits
		IR = new Register(16); //register size is 16 bits
		MAR = new Register(16); //register size is 16 bits
		MBR = new Register(16); //register size is 16 bits
		MSR = new Register(16); //register size is 16 bits
		MFR = new Register(4); //register size is 4 bits
		IAR = new Register(16);
		IRR = new Register[4];
		R0 = new Register(16);
		R1 = new Register(16);
		R2 = new Register(16);
		R3 = new Register(16);
		X1 = new Register(16);
		X2 = new Register(16);
		X3 = new Register(16);
		
		// Initialize Memory and Cache
		memory = new TreeMap<String, MemoryLocation>();
		cache = new Vector<CacheLine>();
		
		// Initialize I/O transfer objects
		inputObject = new IOObject();
		outputObject = new IOObject();
		
		// Initialize program lengths
		bootPrgmLength = 0;
		prgmOneLength = 0;
		
		// Initialize IRR
		for (int i = 0; i < IRR.length; i++)
		{
			IRR[i] = new Register(16);
		}
		
		// Initialize register flags
		// true for int and false for ascii
		registerIsInt = new boolean[4];
		for (int i = 0; i < registerIsInt.length; i++)
		{
			registerIsInt[i] = false;
		}
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
	public Register[] getIRR()
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
	
	public boolean[] getRegisterIsInt()
	{
		return registerIsInt;
	}
	
	public void setRegisterIsInt(int regId, boolean isInt)
	{
		registerIsInt[regId] = isInt;
	}
	
	/**
	 * Loads the ROM contents.
	 * Called when IPL button is pressed.
	 */
	public void loadROM()
	{
		Thread thread = new Thread(this);
		thread.start();
		
		
	}
	
	/**
	 * Loads the contents of the program.
	 * Called when Load File button is pressed.
	 */
	public void loadFromFile()
	{
		
	}
	
	private int loadToMemory(String fileName, String startAddress)
	{
		// Read in and parse rom.txt 
		// rom.txt is formatted so that each instruction is on a separate line, first 16 characters of each line are the instruction, rest are comments
		// Make sure to only read the first 16 characters of each row (i.e. ignore the comments)
		File file = new File(fileName);
		try 
		{
			FileInputStream fileIn = new FileInputStream(file);
			
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
		 
			int prgmLength = 0;
			String line = null;
			String bootPrgmStart16Bits = startAddress;
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
				address = ArithmeticLogicUnit.add(address, BitWord.VALUE_ONE);
			}
			
			br.close();
			
			// Execute boot program (mostly load/store)
			// PC can only hold 12 bits, so chop off the leading zeros
			String bootPrgmStart12Bits = bootPrgmStart16Bits.substring(4, 16);
			PC.setBitValue(bootPrgmStart12Bits);
			for(int k = 1; k <= bootPrgmLength; k++)
			{
				if (MiniComputerGui.haltButtonClicked)
				{
					break;
				}
				singleStep();
			}
			
			MiniComputerGui.haltButtonClicked = false;
			
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
	
	/*
	 * Overriden method from interface Runnable
	 * Called when thread.start() is called
	 * Used for creating a new thread in the program so the GUI doesn't freeze when waiting for user input
	 */
	@Override
	public void run() {
		
		
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
    	int register;	//in decimal
    	int index; 	//in decimal
    	boolean isIndirectAddress; 
    	BitWord address; 
    	BitWord immediate;
    	BitWord devId;
    	ConditionCode conditionCode;	//in decimal
        int rx; // in decimal
        int ry; // in decimal
        BitWord arithmeticOrLogic;
        BitWord leftOrRight;
        BitWord shiftCount;        
		
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
                MBR.setBitValue(BitWord.VALUE_DEFAULT);
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
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	amr(register, index, isIndirectAddress, address);
                break;
            case OpCode.SMR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	smr(register, index, isIndirectAddress, address);
                break;
            case OpCode.AIR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	immediate = instructionParse.get(BitInstruction.KEY_IMMEDIATE); 
            	air(register, immediate);
                break;
            case OpCode.SIR:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	immediate = instructionParse.get(BitInstruction.KEY_IMMEDIATE); 
            	sir(register, immediate);
                break;
            case OpCode.JZ:
            	isTransferInstruction = true;
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	jz(register, index, isIndirectAddress, address);
                break;
            case OpCode.JNE:
            	isTransferInstruction = true;
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	jne(register, index, isIndirectAddress, address);
            	break;
            case OpCode.JCC:
            	isTransferInstruction = true;
            	conditionCode = ConditionCode.values()[Integer.parseInt(instructionParse.get(BitInstruction.KEY_CONDITION_CODE).getValue(), 2)]; 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	jcc(conditionCode, index, isIndirectAddress, address);
            	break;
            case OpCode.JMA:
            	isTransferInstruction = true;
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	jma(index, isIndirectAddress, address);
            	break;
            case OpCode.JSR:
            	isTransferInstruction = true;
                index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2);
                isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
                address = instructionParse.get(BitInstruction.KEY_ADDRESS);
                jsr(index, isIndirectAddress, address);            	
            	break;
            case OpCode.RFS:
            	isTransferInstruction = true;
                immediate = instructionParse.get(BitInstruction.KEY_IMMEDIATE);
                rfs(immediate);            	
            	break;
            case OpCode.SOB:
            	isTransferInstruction = true;
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	sob(register, index, isIndirectAddress, address);
            	break;
            case OpCode.JGE:
            	isTransferInstruction = true;
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	index = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDEX).getValue(), 2); 
            	isIndirectAddress = Integer.parseInt(instructionParse.get(BitInstruction.KEY_INDIRECT_ADDR).getValue()) == 1; 
            	address = instructionParse.get(BitInstruction.KEY_ADDRESS); 
            	jge(register, index, isIndirectAddress, address);
            	break;
            case OpCode.IN:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	devId = instructionParse.get(BitInstruction.KEY_DEVID); 
            	in(register, devId);
                break;
            case OpCode.OUT:
            	register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
            	devId = instructionParse.get(BitInstruction.KEY_DEVID); 
            	out(register, devId);
                break;
            case OpCode.MLT:
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                mlt(rx, ry);
                break;
            case OpCode.DVD:
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                dvd(rx, ry);
                break;
            case OpCode.TRR:
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                trr(rx, ry);
            break;     
            case OpCode.SRC:
                isTransferInstruction = true;
                register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
                arithmeticOrLogic = instructionParse.get(BitInstruction.KEY_ARITHMETIC_OR_LOGIC);
                leftOrRight = instructionParse.get(BitInstruction.KEY_LEFT_OR_RIGHT);
                shiftCount = instructionParse.get(BitInstruction.KEY_SHIFT_COUNT); 
                src(register, arithmeticOrLogic, leftOrRight, shiftCount);            	
                break;
            case OpCode.RRC:
                isTransferInstruction = true;
                register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
                arithmeticOrLogic = instructionParse.get(BitInstruction.KEY_ARITHMETIC_OR_LOGIC);
                leftOrRight = instructionParse.get(BitInstruction.KEY_LEFT_OR_RIGHT);
                shiftCount = instructionParse.get(BitInstruction.KEY_SHIFT_COUNT); 
                rrc(register, arithmeticOrLogic, leftOrRight, shiftCount);            	
                break;
            case OpCode.AND:
                isTransferInstruction = true;
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                and(rx, ry);            	
                break;
            case OpCode.ORR: 
                isTransferInstruction = true;
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                orr(rx, ry);             	
                break;
            case OpCode.NOT: 
                isTransferInstruction = true;
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                not(rx);             	
                break;
            default:
                break;                        
        }
		
		// Update PC with address of next instruction (GUI will call getPC().getBitValue() when updating the text box
        if(!isTransferInstruction && !opcode.getValue().equals(OpCode.HLT))
        {
        	// Increment PC
        	String pc = ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE);
        	// PC can only hold 12 bits, so chop off the leading zeros
        	pc = pc.substring(4, 16);
        	PC.setBitValue(pc);
        }
        // For transfer instructions, PC is set when executing that instruction
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
			MBR.setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[0].setBitValue(MBR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR[0].getBitValue());
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
			IRR[0].setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR[0].setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move contents of IRR to memory at address specified by IAR		
		MemoryLocation memLoc = new MemoryLocation(IAR.getBitValue(), IRR[0].getBitValue());
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
		IRR[0].setBitValue(IAR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR[0].getBitValue());
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
			MBR.setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[0].setBitValue(MBR.getBitValue());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(IRR[0].getBitValue());
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
			IRR[0].setBitValue(indexSelect1.getBitValue());
		} else {
			// Should never reach here, but just in case
			IRR[0].setBitValue(new BitWord());
		}
		
		// Move contents of IRR to memory at address specified by IAR		
		MemoryLocation memLoc = new MemoryLocation(IAR.getBitValue(), IRR[0].getBitValue());
		// TreeMap.put() automatically replaces the value and adds a new key if necessary 
		memory.put(IAR.getBitValue().getValue(), memLoc);
	}
	
	/**
	 * Add Memory To Register
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void amr(int register, int index, boolean isIndirectAddress, BitWord address)
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
			MBR.setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the contents of the specified register to the IRR
		if(registerSelect1 != null)
		{
			IRR[0].setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR[0].setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[1].setBitValue(MBR.getBitValue());
				
		// Store sum of register and memory contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(ArithmeticLogicUnit.add(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue()));
		}
	}
	
	/** Subtract Memory From Register
	 * Load Register With Address
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void smr(int register, int index, boolean isIndirectAddress, BitWord address)
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
			MBR.setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the contents of the specified register to the IRR
		if(registerSelect1 != null)
		{
			IRR[0].setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR[0].setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[1].setBitValue(MBR.getBitValue());
				
		// Store difference of register and memory contents into the specified register
		if(registerSelect1 != null)
		{
			Map<String, Object> differenceMap = ArithmeticLogicUnit.subtract(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue());
			if (!(Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISUNDERFLOW))
			{
				registerSelect1.setBitValue(String.valueOf(differenceMap.get(ArithmeticLogicUnit.KEY_DIFFERENCE)));
			}
			setConditionCode(ConditionCode.UNDERFLOW, (Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISUNDERFLOW));
		}
	}
	
	/**
	 * Add Immediate To Register
	 * @param register
	 * @param immediate
	 */
	public void air(int register, BitWord immediate)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Move the contents of the specified register to the IRR
		if(registerSelect1 != null)
		{
			IRR[0].setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR[0].setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move immediate to MBR
		MBR.setBitValue(ArithmeticLogicUnit.padZeros(immediate.getValue()));
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[1].setBitValue(MBR.getBitValue());
		
		// Store sum of IRR contents and MBR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(ArithmeticLogicUnit.add(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue()));
		}
	}
	
	/**
	 * Subtract Immediate From Register
	 * @param register
	 * @param immediate
	 */
	public void sir(int register, BitWord immediate)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Move the contents of the specified register to the IRR
		if(registerSelect1 != null)
		{
			IRR[0].setBitValue(registerSelect1.getBitValue());
		}
		else
		{
			// Should never reach here, but just in case
			IRR[0].setBitValue(BitWord.VALUE_DEFAULT);
		}
		
		// Move immediate to MBR
		MBR.setBitValue(ArithmeticLogicUnit.padZeros(immediate.getValue()));
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[1].setBitValue(MBR.getBitValue());
		
		// Store difference of IRR contents and MBR contents into the specified register
		if(registerSelect1 != null)
		{
			Map<String, Object> differenceMap = ArithmeticLogicUnit.subtract(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue());
			if (!(Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISUNDERFLOW))
			{
				registerSelect1.setBitValue(String.valueOf(differenceMap.get(ArithmeticLogicUnit.KEY_DIFFERENCE)));
			}
			setConditionCode(ConditionCode.UNDERFLOW, (Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISUNDERFLOW));
			
		}
	}
	
	/**
	 * Notifies GUI that program is ready to receive text input
	 * @param register
	 * @param devId
	 */
	public void in(int register, BitWord devId)
	{
		inputObject.setOpCode(OpCode.IN);
		inputObject.setRegisterId(register);
		inputObject.setDevId(devId.getValue());
		
		setChanged();
		notifyObservers(inputObject);
		
		while (MiniComputerGui.validKeyClicked == false) {
    		try {
    			Thread.sleep(200);
    		}
    		catch (InterruptedException ie) {
    			System.out.println("Exception: " + ie.getMessage());
    		}
    	}
		
		MiniComputerGui.validKeyClicked = false;
	}
	
	/**
	 * Input character to register from device
	 * @param inputInt
	 *
	 */
	public void inProcessing(int inputInt)
	{
		// Retrieve the specified Index Register (IR a.k.a X)
		Register registerSelect1 = getR(inputObject.getRegisterId());
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(ArithmeticLogicUnit.padZeros(Integer.toBinaryString(inputInt)));
			
			// Value stored in this register is an integer
			registerIsInt[inputObject.getRegisterId()] = true;
		}
	}
	
	/**
	 * Output character to device from register
	 * @param register
	 * @param devId
	 */
	public void out(int register, BitWord devId)
	{
		outputObject.setOpCode(OpCode.OUT);
		outputObject.setRegisterId(register);
		outputObject.setDevId(devId.getValue());
		
		setChanged();
		notifyObservers(outputObject);
	}
	
	/**
	 * Jump If Zero
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jz(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is zero, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr == 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
	
	/**
	 * Jump If Not Equal To Zero
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jne(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is NOT zero, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr != 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
	
	/**
	 * Jump If Condition Code
	 * @param conditionCode 0-3
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jcc(ConditionCode conditionCode, int index, boolean isIndirectAddress, BitWord address)
	{		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Copy the specified bit from the CC register into the Internal Result Register (IRR)
		IRR[0].setBitValue(ArithmeticLogicUnit.padZeros(CC.getBitValue().getValue().substring(conditionCode.ordinal(), conditionCode.ordinal()+1)));

		// If the specified CC bit is 1, move the EA to the Internal Address Register (IAR)
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr == 1) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
	
	/**
	 * Unconditional Jump To Address
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jma(int index, boolean isIndirectAddress, BitWord address)
	{		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);

		// Move the EA to the Internal Address Register (IAR)
		IAR.setBitValue(ea);
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
	
	/**
	 * Jump and Save Return Address
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jsr(int index, boolean isIndirectAddress, BitWord address)
	{
            // Calculate the effective address (EA)
            BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
            
            // Set General Purpose Register R3 to the PC + 1
            R3.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));

            // Move the EA to the Internal Address Register (IAR)
            IAR.setBitValue(ea);
            
            // Store IAR contents into the PC
	    // PC can only hold 12 bits so chop off the leading zeros
            String pc = IAR.getBitValue().getValue().substring(4, 16);
            PC.setBitValue(pc);   
	}
	
	/**
	 * Return From Subroutine w/ return code as Immed
	 * @param immed
	 */
	public void rfs(BitWord immed)
	{
            // Set General Purpose Register 0 to Immed
            R0.setBitValue(immed);
            
            // Store IAR contents into the PC
	    // PC can only hold 12 bits so chop off the leading zeros
            String pc = R3.getBitValue().getValue().substring(4, 16);
            PC.setBitValue(pc);
	}
	
	/**
	 * Subtract One and Branch
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void sob(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Subtract one from the register contents
		Map<String, Object> differenceMap = ArithmeticLogicUnit.subtract(registerSelect1.getBitValue().getValue(), BitWord.VALUE_ONE);
		boolean isUnderflow = (Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISUNDERFLOW);
		// If underflow, leave the register contents alone instead of setting it to gibberish
		if(!isUnderflow) {
			registerSelect1.setBitValue(String.valueOf(differenceMap.get(ArithmeticLogicUnit.KEY_DIFFERENCE)));
		}
		
		// Set Underflow bit
		setConditionCode(ConditionCode.UNDERFLOW, isUnderflow);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is > 0, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(!isUnderflow && irr > 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
	
	/**
	 * Jump If Greater Than or Equal To Zero
	 * @param register
	 * @param index
	 * @param isIndirectAddress
	 * @param address
	 */
	public void jge(int register, int index, boolean isIndirectAddress, BitWord address)
	{
		// Retrieve the specified register
		Register registerSelect1 = getR(register);
		
		// Calculate the effective address (EA)
		BitWord ea = calculateEffectiveAddress(index, isIndirectAddress, address);
		
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr >= 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue(ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
        
        public void mlt(int rx, int ry) {
            Register register1 = getR(rx);
            Register register2 = getR(ry);
            
            //Registers must be either R0 or R2
            if (!register1.equals(R0) && !register1.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else if (!register2.equals(R0) && !register2.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else {
                String product = ArithmeticLogicUnit.multiply(register1.getBitValue().getValue(), register2.getBitValue().getValue());
                
                //Get high and low order bits from product
                BitWord highBits = new BitWord(product.substring(0, 15));
                BitWord lowBits = new BitWord(product.substring(16, 31));
                
                //rx contains high order bits
                register1.setBitValue(highBits);
                //rx + 1 contains low order bits
                Register registerPlusOne = getR(rx + 1);
                registerPlusOne.setBitValue(lowBits);
            }                
        } 
        
        public void dvd(int rx, int ry) {
            Register register1 = getR(rx);
            Register register2 = getR(ry);
            
            //Registers must be either R0 or R2
            if (!register1.equals(R0) && !register1.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else if (!register2.equals(R0) && !register2.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else {
                Map<String, String> divisionMap = ArithmeticLogicUnit.divide(register1.getBitValue().getValue(), register2.getBitValue().getValue());
                if (Integer.parseInt(divisionMap.get(ArithmeticLogicUnit.KEY_ISDIVZERO)) == 1) {
                    setConditionCode(ConditionCode.DIVZERO, true);
                }
                else {
                    setConditionCode(ConditionCode.DIVZERO, false);
                    //rx = quotient
                    register1.setBitValue(divisionMap.get(ArithmeticLogicUnit.KEY_QUOTIENT));
                    //rx + 1 = remainder
                    Register registerPlusOne = getR(rx + 1);
                    registerPlusOne.setBitValue(divisionMap.get(ArithmeticLogicUnit.KEY_REMAINDER));
                }
            }              
        }
        
        public void trr(int rx, int ry) {
            Register register1 = getR(rx);
            Register register2 = getR(ry);
            
            if (register1.getBitValue().equals(register2.getBitValue()))
                setConditionCode(ConditionCode.EQUALORNOT, true);
            else
                setConditionCode(ConditionCode.EQUALORNOT, false);
        }        
        
        public void src(int register, BitWord arithmeticOrLogic, BitWord leftOrRight, BitWord shiftCount)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            //if(irr >= 0) {
                    //IAR.setBitValue(ea);  
            //} else {
                    // Else set IAR value to Shift Register
                    // src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
                    IAR.setBitValue(ArithmeticLogicUnit.src(String.valueOf(irr), arithmeticOrLogic.getValue(), leftOrRight.getValue(), shiftCount.getValue()));
            //}

            // TODO: Check that address specified by IAR is valid (not reserved, not larger than max)

            // Store IAR contents into the PC
            // PC can only hold 12 bits so chop off the leading zeros
            //String pc = IAR.getBitValue().getValue().substring(4, 16);
            //PC.setBitValue(pc);
    	}
    	/**
    	 * Rotate Register by Count
    	 * @param register
    	 * @param arithmeticOrLogic
    	 * @param leftOrRight
    	 * @param shiftCount
    	 */
    	public void rrc(int register, BitWord arithmeticOrLogic, BitWord leftOrRight, BitWord shiftCount)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            //if(irr >= 0) {
                    //IAR.setBitValue(ea);  
            //} else {
                    // Else set IAR value to Shift Register
                    // src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
                    IAR.setBitValue(ArithmeticLogicUnit.rrc(String.valueOf(irr), arithmeticOrLogic.getValue(), leftOrRight.getValue(), shiftCount.getValue()));
            //}

            // TODO: Check that address specified by IAR is valid (not reserved, not larger than max)

            // Store IAR contents into the PC
            // PC can only hold 12 bits so chop off the leading zeros
            //String pc = IAR.getBitValue().getValue().substring(4, 16);
            //PC.setBitValue(pc);
    	}	
    	public void and(int register, int register2)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);
            Register registerSelect2 = getR(register2);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());
            IRR[1].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            int irr2 = Integer.parseInt(IRR[1].getBitValue().getValue());
            //if(irr >= 0) {
                    //IAR.setBitValue(ea);  
            //} else {
                    // Else set IAR value to Shift Register
                    // src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
                    IAR.setBitValue(ArithmeticLogicUnit.and(String.valueOf(irr), String.valueOf(irr2)));
            //}

            // TODO: Check that address specified by IAR is valid (not reserved, not larger than max)

            // Store IAR contents into the PC
            // PC can only hold 12 bits so chop off the leading zeros
            //String pc = IAR.getBitValue().getValue().substring(4, 16);
            //PC.setBitValue(pc);
    	}
    	
    	public void orr(int register, int register2)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);
            Register registerSelect2 = getR(register2);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());
            IRR[1].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            int irr2 = Integer.parseInt(IRR[1].getBitValue().getValue());
            //if(irr >= 0) {
                    //IAR.setBitValue(ea);  
            //} else {
                    // Else set IAR value to Shift Register
                    // src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
                    IAR.setBitValue(ArithmeticLogicUnit.orr(String.valueOf(irr), String.valueOf(irr2)));
            //}

            // TODO: Check that address specified by IAR is valid (not reserved, not larger than max)

            // Store IAR contents into the PC
            // PC can only hold 12 bits so chop off the leading zeros
            //String pc = IAR.getBitValue().getValue().substring(4, 16);
            //PC.setBitValue(pc);
    	}
    	
    	public void not(int register)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            //if(irr >= 0) {
                    //IAR.setBitValue(ea);  
            //} else {
                    // Else set IAR value to Shift Register
                    // src(String Registervalue, String ArithmeticOrLogic, String LeftOrRight, String sCount)
                    IAR.setBitValue(ArithmeticLogicUnit.not(String.valueOf(irr)));
            //}

            // TODO: Check that address specified by IAR is valid (not reserved, not larger than max)

            // Store IAR contents into the PC
            // PC can only hold 12 bits so chop off the leading zeros
            //String pc = IAR.getBitValue().getValue().substring(4, 16);
            //PC.setBitValue(pc);
    	}

	
	// TODO in later parts: other instructions
	
	/* End Instruction methods */
	
	/* Helpers */
	
	private BitWord calculateEffectiveAddress(int indexRegister, boolean isIndirectAddress, BitWord address)
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
						
				String ea = ArithmeticLogicUnit.add(indexValue, toAdd);
						
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
				
				addr = ArithmeticLogicUnit.add(indexValue, toAdd);
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
	
	private void setConditionCode(ConditionCode conditionCode, boolean isTrue)
	{
		String flag = isTrue ? "1" : "0";
		String first = CC.getBitValue().getValue().substring(0, conditionCode.ordinal());
		String last = CC.getBitValue().getValue().substring(conditionCode.ordinal()+1, CC.getBitSize());
			
		CC.setBitValue(first + flag + last);
	}        
	/* End Helpers */
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
	public static final String PROGRAM_ONE_NAME = "Project1_bin.txt";
	public static final String PROGRAM_TWO_NAME = "Project2_bin.txt";
	public static final String PROGRAM_TWO_INPUT_NAME = "prgm-2-input.txt";
	public static volatile ProgramCode currentProgram = ProgramCode.BOOTPROGRAM;
	public static PrintWriter logger;
	
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
	private Cache theCache; // this will replace cache once all cache methods are moved to Cache class
	
	private IOObject inputObject;
	private IOObject outputObject;
	
	private int bootPrgmLength;
	private int prgmOneLength;
	private int prgmTwoLength;
	private String bootPrgmStart12Bits;
	private String prgmOneStart12Bits;
	private String prgmTwoStart12Bits;
	private int prgmTwoInputPointer;
	private boolean isRunningTrap;
	private boolean isRunningFault;
	
	
	public MiniComputer() throws FileNotFoundException, IOException
	{
		// Initialize logger
		logger = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE_NAME)), true);
		
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
		
		MFR.setBitValue("1111"); //Default value of MFR is set to 15, since 0 - 3 are fault values
		
		// Initialize Memory and Cache
		memory = new TreeMap<String, MemoryLocation>();
		cache = new Vector<CacheLine>();
		theCache = new Cache();
		
		// Initialize I/O transfer objects
		inputObject = new IOObject();
		outputObject = new IOObject();
		
		// Initialize program lengths
		bootPrgmLength = 0;
		prgmOneLength = 0;
		prgmTwoLength = 0;
		
		bootPrgmStart12Bits = "";
		prgmOneStart12Bits = "";
		prgmTwoStart12Bits = "";
		prgmTwoInputPointer = 0;
		isRunningTrap = false;
		isRunningFault = false;
		
		// Initialize IRR
		for (int i = 0; i < IRR.length; i++)
		{
			IRR[i] = new Register(16);
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
	
	public Cache getCache()
	{
		return theCache;
	}
	
	
	/**
	 * Loads the ROM contents.
	 * Called when IPL button is pressed.
	 */
	public void loadROM()
	{
		currentProgram = ProgramCode.BOOTPROGRAM;
		loadToMemory(BOOT_PROGRAM_NAME, MemoryLocation.ADDRESS_BOOT_PRGM_START);
		runThroughMemory();
	}
	
	/**
	 * Loads the contents of the program.
	 * Called when Load File button is pressed.
	 */
	public void loadFromFile(String fileName)
	{
		if (fileName.equals(PROGRAM_ONE_NAME)) 
		{
			currentProgram = ProgramCode.PROGRAMONE;
		}
		else if (fileName.equals(PROGRAM_TWO_NAME))
		{
			currentProgram = ProgramCode.PROGRAMTWO;
		}
		else 
		{
			System.out.println("Invalid File selected");
			return;
		}
		loadToMemory(fileName, MemoryLocation.ADDRESS_PRGM_ONE_START);
	}
	
	private void loadToMemory(String fileName, String startAddress)
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
			String prgmStart16Bits = startAddress;
			String address = prgmStart16Bits;
			while ((line = br.readLine()) != null) {
				// Read the 16-bit instruction
				String instruction = line.substring(0, 16);
				prgmLength++;
				
				// Store the instruction in memory
				// Since technically the ROM is already supposed to be in the computer,
				// we will input it directly into memory
				// instead of constructing instructions to input the boot program instructions into memory
				MemoryLocation memLoc = new MemoryLocation(address, instruction);
				memory.put(address, memLoc);
				
				// Increment address
				address = (String) ArithmeticLogicUnit.add(address, BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM);
			}
			
			br.close();
			
			if (currentProgram.equals(ProgramCode.BOOTPROGRAM))
			{
				bootPrgmLength = prgmLength;
				bootPrgmStart12Bits = prgmStart16Bits.substring(4, 16);
			} 
			else if (currentProgram.equals(ProgramCode.PROGRAMONE))
			{
				prgmOneLength = prgmLength;
				prgmOneStart12Bits = prgmStart16Bits.substring(4, 16);
			}
			else if (currentProgram.equals(ProgramCode.PROGRAMTWO))
			{
				prgmTwoLength = prgmLength;
				prgmTwoStart12Bits = prgmStart16Bits.substring(4, 16);
			}
		}
		catch(Exception ex)
		{
			// TODO: Handle exceptions
			System.out.println(ex);
		}
	}
	
	public void runThroughMemory() {
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/*
	 * Overriden method from interface Runnable
	 * Called when thread.start() is called
	 * Used for creating a new thread in the program so the GUI doesn't freeze when waiting for user input
	 */
	@Override
	public void run() {
		
		String prgmStart12Bits = "";
		int prgmLength = 0;
		
		if (currentProgram == ProgramCode.BOOTPROGRAM)
		{
			prgmStart12Bits = bootPrgmStart12Bits;
			prgmLength = bootPrgmLength;
		}
		else if (currentProgram == ProgramCode.PROGRAMONE)
		{
			prgmStart12Bits = prgmOneStart12Bits;
			prgmLength = prgmOneLength;
		}
		else if (currentProgram == ProgramCode.PROGRAMTWO)
		{
			prgmStart12Bits = prgmTwoStart12Bits;
			prgmLength = prgmTwoLength;
		}
		
		MiniComputerGui.haltButtonClicked = false;
		
		MFR.setBitValue("1111");
		//Update GUI status panel
		setChanged();
		notifyObservers(MFR);
		
		// Execute boot program (mostly load/store)
		// PC can only hold 12 bits, so chop off the leading zeros
		PC.setBitValue(prgmStart12Bits);
		for(int k = 1; k <= prgmLength; k++)
		{
			int machineFaultCode = Integer.parseInt(MFR.getBitValue().getValue(), 2);
			
			if (MiniComputerGui.haltButtonClicked)
			{
				System.out.println("TERMINATED: Halt has been clicked");
				break;
			}
			else if (machineFaultCode < 15)
			{
				System.out.println("TERMINATED: Machine fault, code " + machineFaultCode);
				break;
			}
			else
			{
				// continue running program
				singleStep();
			}
		}
		
		// Set PC back to the start of the boot program
		// PC can only hold 12 bits, so chop off the leading zeros
		PC.setBitValue(prgmStart12Bits);
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
        BitWord trapCode;
		
        System.out.println("PC: " + PC.getBitValue().getValue()); // DEBUG code
        
        MFR.setBitValue("1111");
		//Update GUI status panel
		setChanged();
		notifyObservers(MFR);
		
        // Transfer PC value to MAR
        MAR.setBitValue(ArithmeticLogicUnit.padZeros16(PC.getBitValue().getValue()));

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
        
        if (opcode == null)
        {
        	opcode = new BitWord(BitWord.VALUE_INVALID_OPCODE);
        }
        
        switch (opcode.getValue())
        {
            case OpCode.HLT:
                //TODO in Part II
                break;
            case OpCode.TRAP:
                trapCode = instructionParse.get(BitInstruction.KEY_TRAP_CODE);
                trap(trapCode);
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
                register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
                arithmeticOrLogic = instructionParse.get(BitInstruction.KEY_ARITHMETIC_OR_LOGIC);
                leftOrRight = instructionParse.get(BitInstruction.KEY_LEFT_OR_RIGHT);
                shiftCount = instructionParse.get(BitInstruction.KEY_SHIFT_COUNT); 
                src(register, arithmeticOrLogic, leftOrRight, shiftCount);            	
                break;
            case OpCode.RRC:
                register = Integer.parseInt(instructionParse.get(BitInstruction.KEY_REGISTER).getValue(), 2); 
                arithmeticOrLogic = instructionParse.get(BitInstruction.KEY_ARITHMETIC_OR_LOGIC);
                leftOrRight = instructionParse.get(BitInstruction.KEY_LEFT_OR_RIGHT);
                shiftCount = instructionParse.get(BitInstruction.KEY_SHIFT_COUNT); 
                rrc(register, arithmeticOrLogic, leftOrRight, shiftCount);            	
                break;
            case OpCode.AND:
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                and(rx, ry);            	
                break;
            case OpCode.ORR: 
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                ry = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RY).getValue(), 2);
                orr(rx, ry);             	
                break;
            case OpCode.NOT: 
                rx = Integer.parseInt(instructionParse.get(BitInstruction.KEY_RX).getValue(), 2);
                not(rx);             	
                break;
            default:
            	handleMachineFault(FaultCode.ILLEGAL_OPCODE);
                break;                        
        }
		
		// Update PC with address of next instruction (GUI will call getPC().getBitValue() when updating the text box
        if(!isTransferInstruction 
        		&& !opcode.getValue().equals(OpCode.HLT)
        		&& !opcode.getValue().equals(OpCode.TRAP))
        {
        	// Increment PC
        	String pc = (String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM);
        	// PC can only hold 12 bits, so chop off the leading zeros
        	pc = pc.substring(4, 16);
        	PC.setBitValue(pc);
        }
        // For transfer instructions, PC is set when executing that instruction
        
        if(isRunningTrap)
        {
        	// Retrieve next PC from reserved memory location 2
    		String pc = memory.get(MemoryLocation.RESERVED_ADDRESS_TRAP_PC).getValue().getValue();
    		// PC can only hold 12 bits, so chop off the leading zeros
    		pc = pc.substring(4,  16);
    		PC.setBitValue(pc);
    		
    		isRunningTrap = false;
        }
        
        if(isRunningFault)
        {
        	// Retrieve next PC from reserved memory location 4
    		String pc = memory.get(MemoryLocation.RESERVED_ADDRESS_FAULT_PC).getValue().getValue();
    		// PC can only hold 12 bits, so chop off the leading zeros
    		pc = pc.substring(4,  16);
    		PC.setBitValue(pc);
    		
    		isRunningFault = false;
        }
	}
	
	/* Instruction methods */
	
	/**
	 * Trap to Memory Address 0
	 * @param trap code
	 */
	public void trap(BitWord trapCode)
	{
		int trapCodeInt = Integer.parseInt(trapCode.getValue(), 2);
		
		if (trapCodeInt < 0 || trapCodeInt > 15)
		{
			handleMachineFault(FaultCode.ILLEGAL_TRAP_CODE);
			return;
		}
		
		if (memory.get(MemoryLocation.RESERVED_ADDRESS_TRAP) != null)
		{
			// Write PC+1 to reserved memory location 2
			String pc = (String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM);
			MemoryLocation memLoc = new MemoryLocation(MemoryLocation.RESERVED_ADDRESS_TRAP_PC, pc);
			
			theCache.writeToCacheAndMemory(memory, MemoryLocation.RESERVED_ADDRESS_TRAP_PC, memLoc);
			
			// Set PC to trap routine address
			BitWord trapAddress = memory.get(MemoryLocation.RESERVED_ADDRESS_TRAP).getValue();
			String trapRoutineAddress = (String) ArithmeticLogicUnit.add(trapAddress.getValue(), trapCode.getValue()).get(ArithmeticLogicUnit.KEY_SUM);
			
			PC.setBitValue(trapRoutineAddress);
			
			isRunningTrap = true;
			
			// Run trap instruction
			singleStep();
		}
	}
	
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

		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
		theCache.writeToCacheAndMemory(memory, IAR.getBitValue().getValue(), memLoc);
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

		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
		theCache.writeToCacheAndMemory(memory, IAR.getBitValue().getValue(), memLoc);
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

		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
		
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
		Map<String, Object> result = ArithmeticLogicUnit.add(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue());
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue((String) result.get(ArithmeticLogicUnit.KEY_SUM));
		}
		
		// Set the OVERFLOW bit
		setConditionCode(ConditionCode.OVERFLOW, (boolean) result.get(ArithmeticLogicUnit.KEY_ISOVERFLOW));
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

		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
		MBR.setBitValue(ArithmeticLogicUnit.padZeros16(immediate.getValue()));
		
		// Move the data from the MBR to an Internal Result Register (IRR)
		IRR[1].setBitValue(MBR.getBitValue());
		
		// Store sum of IRR contents and MBR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue((String) ArithmeticLogicUnit.add(IRR[0].getBitValue().getValue(), IRR[1].getBitValue().getValue()).get(ArithmeticLogicUnit.KEY_SUM));
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
		MBR.setBitValue(ArithmeticLogicUnit.padZeros16(immediate.getValue()));
		
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
		
		if (devId.getValue() == DeviceId.FILE_READER_ASCII)
		{
			char inputChar = loadPrgm2Input();
			inProcessing(DataConversion.textToBinary(inputChar), register);
		}
		else if (devId.getValue() == DeviceId.CONSOLE_KEYBOARD)
		{
			// update GUI to allow typing into console keyboard
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
	}
	
	/**
	 * Read character from Program 2 input file
	 * @return character read
	 */
	public char loadPrgm2Input()
	{

		File file = null;
		FileInputStream fileIn = null;
		BufferedReader br = null;
		int value = 0;
		
		try 
		{
			file = new File(PROGRAM_TWO_INPUT_NAME);
			fileIn = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fileIn));
			
			br.skip(prgmTwoInputPointer); // start where previous call left off
			
			value = br.read(); // read in next character
			
			if (value == -1) // if EOF
			{
				value = 26;
				prgmTwoInputPointer = 0;
			}
			else
			{
				prgmTwoInputPointer++;
			}
			
			fileIn.close();
			br.close();
		}
		catch(Exception ex)
		{
			// TODO: Handle exceptions
			System.out.println(ex.getMessage());
		}
		
		return (char) value;
	}
	
	/**
	 * Input character to register from console keyboard
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
			registerSelect1.setBitValue(ArithmeticLogicUnit.padZeros16(Integer.toBinaryString(inputInt)));
		}
	}
	
	/**
	 * Input character to register from file
	 * @param inputString, register
	 *
	 */
	public void inProcessing(String inputString, int register)
	{
		// Retrieve the specified Index Register (IR a.k.a X)
		Register registerSelect1 = getR(register);
		
		// Store IRR contents into the specified register
		if(registerSelect1 != null)
		{
			registerSelect1.setBitValue(inputString);
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
		
		// update GUI by entering output to console printer
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is zero, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr == 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is NOT zero, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr != 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
		// Copy the specified bit from the CC register into the Internal Result Register (IRR)
		IRR[0].setBitValue(ArithmeticLogicUnit.padZeros16(CC.getBitValue().getValue().substring(conditionCode.ordinal(), conditionCode.ordinal()+1)));

		// If the specified CC bit is 1, move the EA to the Internal Address Register (IAR)
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr == 1) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));
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

		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
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
            
    		// Machine Fault check
    		if (isIllegalMemoryAddress(ea))
    		{
    			return;
    		}
    				
            // Set General Purpose Register R3 to the PC + 1
            R3.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));

            // Move the EA to the Internal Address Register (IAR)
            IAR.setBitValue(ArithmeticLogicUnit.padZeros16(ea.getValue()));
            
            // Set R0 to the address of the first parameter
            // Assumes the parameter list starts at the max memory address 2048 and goes backward
            // until the end of the parameters is indicated by -17777
            R0.setBitValue(MAX_MEMORY_ADDRESS);
            
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
            R0.setBitValue(new BitWord(ArithmeticLogicUnit.padZeros16(immed.getValue())));
            
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is > 0, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(!isUnderflow && irr > 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));
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
		
		// Machine Fault check
		if (isIllegalMemoryAddress(ea))
		{
			return;
		}
				
		// Move the register contents into the Internal Result Register (IRR)?
		IRR[0].setBitValue(registerSelect1.getBitValue());

		// If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
		// Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
		int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
		if(irr >= 0) {
			IAR.setBitValue(ea);
		} else {
			// Else set IAR value to PC contents + 1
			IAR.setBitValue((String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM));
		}
		
		// TODO: Check that address specified by IAR is valid (not reserved, not larger than max)
		
		// Store IAR contents into the PC
		// PC can only hold 12 bits so chop off the leading zeros
		String pc = IAR.getBitValue().getValue().substring(4, 16);
		PC.setBitValue(pc);
	}
        
         /**
	 * Multiply Register by Register
	 * @param rx
	 * @param ry
	 */
        public void mlt(int rx, int ry) {
            Register register1 = getR(rx);
            Register register2 = getR(ry);
            
            //Registers must be either R0 or R2
            if (!register1.equals(R0) && !register1.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else if (!register2.equals(R0) && !register2.equals(R2))
                System.out.println("Register must be R0 or R2.");
            else {
            	Map<String, Object> result = ArithmeticLogicUnit.multiply(register1.getBitValue().getValue(), register2.getBitValue().getValue());
                String product = (String) result.get(ArithmeticLogicUnit.KEY_PRODUCT);
                
                //Get high and low order bits from product
                BitWord highBits = new BitWord(product.substring(0, 16));
                BitWord lowBits = new BitWord(product.substring(16, 32));
                
                //rx contains high order bits
                register1.setBitValue(highBits);
                //rx + 1 contains low order bits
                Register registerPlusOne = getR(rx + 1);
                registerPlusOne.setBitValue(lowBits);
                
                // Set OVERFLOW bit
                setConditionCode(ConditionCode.OVERFLOW, (boolean) result.get(ArithmeticLogicUnit.KEY_ISOVERFLOW));
            }                
        } 
        
         /**
	 * Divide Register by Register
	 * @param rx
	 * @param ry
	 */
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
        
         /**
	 * Equality of Register and Register
	 * @param rx
	 * @param ry
	 */
        public void trr(int rx, int ry) {
            Register register1 = getR(rx);
            Register register2 = getR(ry);
            
            if (register1.getBitValue().equals(register2.getBitValue()))
                setConditionCode(ConditionCode.EQUALORNOT, true);
            else
                setConditionCode(ConditionCode.EQUALORNOT, false);
        }        
        
         /**
	 * Shift Register by Count
	 * @param register
	 * @param arithmeticOrLogic
         * @param leftOrRight
         * @param shiftCount
	 */
        public void src(int register, BitWord arithmeticOrLogic, BitWord leftOrRight, BitWord shiftCount)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());
            
    		if(registerSelect1 != null)
    		{
    			//registerSelect1.setBitValue(IRR[0].getBitValue());
    			Map<String, Object> differenceMap = ArithmeticLogicUnit.src(IRR[0].getBitValue().getValue(), arithmeticOrLogic.getValue(), leftOrRight.getValue(), shiftCount.getValue());
    			if (!(Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISOVERFLOW))
    			{//after performing the shift save the new value in the IRR
    				IRR[0].setBitValue(String.valueOf(differenceMap.get(ArithmeticLogicUnit.KEY_REGISTERVALUE)));
    				// Store IRR contents into the specified register
    				registerSelect1.setBitValue(String.valueOf(differenceMap.get(ArithmeticLogicUnit.KEY_REGISTERVALUE)));
    			}
    			setConditionCode(ConditionCode.OVERFLOW, (Boolean) differenceMap.get(ArithmeticLogicUnit.KEY_ISOVERFLOW));
    		
    		}
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
            IRR[0].setBitValue(ArithmeticLogicUnit.rrc(IRR[0].getBitValue().getValue(), arithmeticOrLogic.getValue(), leftOrRight.getValue(), shiftCount.getValue()));
    		// Store IRR contents into the specified register
    		if(registerSelect1 != null)
    		{
    			registerSelect1.setBitValue(IRR[0].getBitValue());
    		}
    	}

        /**
        * Logical and of Register and Register
        * @param register
        * @param register2
        */
    	public void and(int register, int register2)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);
            Register registerSelect2 = getR(register2);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());
            IRR[1].setBitValue(registerSelect2.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            int irrtwo = Integer.parseInt(IRR[1].getBitValue().getValue());
            IRR[0].setBitValue(ArithmeticLogicUnit.and(String.valueOf(irr), String.valueOf(irrtwo)));
            
    		if(registerSelect1 != null)
    		{
    			registerSelect1.setBitValue(IRR[0].getBitValue());
    		}
    	}
    	
        /**
        * Logical or of Register and Register
        * @param register
        * @param register2
        */        
    	public void orr(int register, int register2)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);
            Register registerSelect2 = getR(register2);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());
            IRR[1].setBitValue(registerSelect2.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            int irrtwo = Integer.parseInt(IRR[1].getBitValue().getValue());
            IRR[0].setBitValue(ArithmeticLogicUnit.orr(String.valueOf(irr), String.valueOf(irrtwo)));
            
    		if(registerSelect1 != null)
    		{
    			registerSelect1.setBitValue(IRR[0].getBitValue());
    		}

    	}
    	
        /**
        * Logical not of Register and Register
        * @param register
        */        
    	public void not(int register)
    	{
            // Retrieve the specified register
            Register registerSelect1 = getR(register);

            // Move the register contents into the Internal Result Register (IRR)?
            IRR[0].setBitValue(registerSelect1.getBitValue());

            // If IRR contents is >= 0, move the EA to the Internal Address Register (IAR)
            // Should I be calling the TRR instruction or setting the EQUALORNOT CC register bit when testing if zero??
            int irr = Integer.parseInt(IRR[0].getBitValue().getValue());
            //perform logic of NOT statement on bitword
            IRR[0].setBitValue(ArithmeticLogicUnit.not(String.valueOf(irr)));
    		//write the answer to the rx specified register
            if(registerSelect1 != null)
    		{
    			registerSelect1.setBitValue(IRR[0].getBitValue());
    		}
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
				return new BitWord(ArithmeticLogicUnit.padZeros16(address.getValue()));
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
						
				String ea = (String) ArithmeticLogicUnit.add(indexValue, toAdd).get(ArithmeticLogicUnit.KEY_SUM);
						
				return new BitWord(ea);
			}
			else
			{
				// Should never reach here, but just in case
				return new BitWord(ArithmeticLogicUnit.padZeros16(address.getValue()));	//does it make sense to return this?
			}
		}
		else
		{
			String addr = "";
			
			if(indexRegister == 0)
			{
				addr = ArithmeticLogicUnit.padZeros16(address.getValue());
			}
			else if(indexRegister >= 1 && indexRegister <= 3)
			{
				String indexValue = getX(indexRegister).getBitValue().getValue();
				
				String toAdd = address.getValue();
				
				addr = (String) ArithmeticLogicUnit.add(indexValue, toAdd).get(ArithmeticLogicUnit.KEY_SUM);
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
	{	//example condition code return values and the respective state/condition
		//1000 - OVERFLOW true
		//0100 - UNDERFLOW true
		//0010 - DIVZERO true
		//0001 - EQUALORNOT true
		//1001 - OVERFLOW true and EQUALORNOT true
		String flag = isTrue ? "1" : "0";
		String first = CC.getBitValue().getValue().substring(0, conditionCode.ordinal());
		String last = CC.getBitValue().getValue().substring(conditionCode.ordinal()+1, CC.getBitSize());
			
		CC.setBitValue(first + flag + last);
	}        
        
	private boolean isIllegalMemoryAddress(BitWord bitAddress)
	{
		if (MemoryLocation.isAddressReserved(bitAddress.getValue()))
		{
			handleMachineFault(FaultCode.ILLEGAL_MEMORY_ADDRESS_TO_RESERVED_LOCATION);
			return true;
		}
		
		if (Integer.parseInt(bitAddress.getValue(), 2) > 2047)
		{
			handleMachineFault(FaultCode.ILLEGAL_MEMORY_ADDRESS_BEYOND_2047);
			return true;
		}
		
		return false;
	}
	
	private void handleMachineFault(FaultCode faultCode)
	{
		if (memory.get(MemoryLocation.RESERVED_ADDRESS_FAULT) != null)
		{
			// Write PC+1 to reserved memory location 4
			String pc = (String) ArithmeticLogicUnit.add(PC.getBitValue().getValue(), BitWord.VALUE_ONE).get(ArithmeticLogicUnit.KEY_SUM);
			MemoryLocation memLoc = new MemoryLocation(MemoryLocation.RESERVED_ADDRESS_FAULT_PC, pc);
			
			theCache.writeToCacheAndMemory(memory, MemoryLocation.RESERVED_ADDRESS_FAULT_PC, memLoc);
			
			// Set PC to fault trap address
			BitWord faultAddress = memory.get(MemoryLocation.RESERVED_ADDRESS_FAULT).getValue();
			
			PC.setBitValue(faultAddress);
			
			isRunningFault = true;
			
			// Run machine fault instruction
			singleStep();
		}
		
		String fault = MFR.getBitValue().getValue();
		
		if (faultCode == FaultCode.ILLEGAL_MEMORY_ADDRESS_TO_RESERVED_LOCATION)
		{
			System.out.println("Error: Illegal Memory Address to Reserved Location");
			fault = "0000"; // 0
		}
		if (faultCode == FaultCode.ILLEGAL_TRAP_CODE)
		{
			System.out.println("Error: Illegal Trap Code");
			fault = "0001"; // 1
		}
		if (faultCode == FaultCode.ILLEGAL_OPCODE)
		{
			System.out.println("Error: Illegal OpCode");
			fault = "0010"; // 2
		}
		if (faultCode == FaultCode.ILLEGAL_MEMORY_ADDRESS_BEYOND_2047)
		{
			System.out.println("Error: Illegal Memory Address Beyond 2048");
			fault = "0011"; // 3
		}	
		
		MFR.setBitValue(fault);
		
		//Update GUI status panel
		setChanged();
		notifyObservers(MFR);
		
	}
	
        private void fetchFromCache() {
            MemoryLocation tempMemory = null;   
            int count = 1;
            String firstTwelveBits = MAR.getBitValue().getValue().substring(0, 13);
            logger.println("Searching for the following 12 bit address in the cache: " + firstTwelveBits);              
            for (CacheLine cacheLine : cache) {         
                //if first 12 bits of address tag match
                if (firstTwelveBits.equals(cacheLine.addressTag.getValue())) {
                   MemoryLocation[] tempBlock = cacheLine.getBlock();
                   for (MemoryLocation block : tempBlock) {
                       //get address from block 
                       if (block.getAddress().getValue().equals(firstTwelveBits)) {
                           tempMemory = block;
                           break;
                       }
                   }
                   logger.println(count + ". Address in cache: " + cacheLine.getAddressTag() + " is a match.");                   
                   break; //found in cache, move on
                }
                else
                    logger.println(count + ". Address in cache: " + cacheLine.getAddressTag() + " not a match.");
                count++;
            }            
            if (tempMemory != null) {
                //memory was in cache                
                MBR.setBitValue(tempMemory.getAddress());
                logger.println("Address was found in cache. Setting MBR... Value = " + MBR.getBitValue().getValue());
            }
            else {
                //memory was not in cache
                logger.println("Address was not in cache... find in memory and put in cache.");
                // Fetch the contents in memory at the address specified by MAR into the MBR
                if(memory.containsKey(MAR.getBitValue().getValue()))
                {
                    MBR.setBitValue(memory.get(MAR.getBitValue().getValue()).getValue());
                }
                else
                {			
                    MBR.setBitValue(BitWord.VALUE_DEFAULT);
                }
                //put memory in cache and remove first element (fifo)
                //first on
                cache.add(new CacheLine(MBR.getBitValue().getValue()));
                //if cache is greater than 16, first off
                if(cache.size() > 16)
                    cache.remove(0);
            }
        }
	
	/* End Helpers */
}

import java.util.HashMap;
import java.util.Map;

public class BitInstruction extends BitWord
{    
    public static final String KEY_OPCODE = "opCode";
    public static final String KEY_INDEX = "index";
    public static final String KEY_REGISTER = "register";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_INDIRECT_ADDR = "indirectAddr";
    public static final String KEY_IMMEDIATE = "immediate";
    public static final String KEY_CONDITION_CODE = "conditionCode";
    public static final String KEY_ARITHMETIC_OR_LOGIC = "ArithmeticOrLogic";
    public static final String KEY_LEFT_OR_RIGHT = "LeftOrRight";
    public static final String KEY_SHIFT_COUNT = "SHiftCount";
    public static final String KEY_REGISTER2 = "register2";
    
    
    public BitInstruction()
    {
        super();
    }
    
    public BitInstruction(BitWord instruction)
    {
    	super(instruction.getValue());
    }
    
    public Map<String, BitWord> ParseInstruction()
    {         
    	//Key = Instruction Name (ex. OpCode), Value = Instruction Bits (ex. "0001")
        Map<String, BitWord> instructionParts = new HashMap<String, BitWord>();
        String opCode = value.substring(0, 6);
        
        switch (opCode)
        {
            case OpCode.HLT:
                //BEN TODO
                break;
            case OpCode.TRAP:
                //TODO in Part III
                break;
            case OpCode.LDR:
                instructionParts = parseLoadStore(opCode);
                break;
            case OpCode.STR:
                instructionParts = parseLoadStore(opCode);
                break;
            case OpCode.LDA:
            	instructionParts = parseLoadStore(opCode);
                break;
            case OpCode.LDX:
            	instructionParts = parseLoadStoreIndex(opCode);
                break;
            case OpCode.STX:
            	instructionParts = parseLoadStoreIndex(opCode);
                break;
            case OpCode.AMR:
            	instructionParts = parseLoadStore(opCode);
                break;
            case OpCode.SMR:
            	instructionParts = parseLoadStore(opCode);
                break;
            case OpCode.AIR:
                instructionParts = parseImmediate(opCode);
                break;
            case OpCode.SIR:
                instructionParts = parseImmediate(opCode);
                break;
            case OpCode.JZ:
            	//Instruction schema is similar to that of the load/store instructions
            	instructionParts = parseLoadStore(opCode);
            	break;
            case OpCode.JNE:
            	//Instruction schema is similar to that of the load/store instructions
            	instructionParts = parseLoadStore(opCode);
            	break;
            case OpCode.JCC:
            	instructionParts = parseJCC();
            	break;
            case OpCode.JMA:
            	//Instruction schema is similar to that of the load/store instructions for index registers
            	instructionParts = parseLoadStoreIndex(opCode);
            	break;
            case OpCode.JSR:
            	//KEEGAN TODO
            	break;
            case OpCode.RFS:
            	//KEEGAN TODO
            	break;
            case OpCode.SOB:
            	//Instruction schema is similar to that of the load/store instructions
            	instructionParts = parseLoadStore(opCode);
            	break;
            case OpCode.JGE:
            	//Instruction schema is similar to that of the load/store instructions
            	instructionParts = parseLoadStore(opCode);
            	break;
            case OpCode.AND:
            	//AND
            	instructionParts = parseLogic(opCode);  // use whatever Kegan calls his opCode parser
            	break;
            case OpCode.ORR:
            	//AND
            	instructionParts = parseLogic(opCode); // use whatever Kegan calls his opCode parser
            	break;
            case OpCode.NOT:
            	//AND
            	instructionParts = parseLogic(opCode); // use whatever Kegan calls his opCode parser
            	break;
            case OpCode.SRC:
            	//Shift
            	instructionParts = parseShiftRotate(opCode);
            	break;
            case OpCode.RRC:
            	//Rotate
            	instructionParts = parseShiftRotate(opCode);
            	break;
            default:
                break;                        
        }
        
        return instructionParts;
    }  
    
    /**
     * Parses the register, index, indirectAddr, and address
     * @param opCode
     * @return
     */
    private Map<String, BitWord> parseLoadStore(String opCode)
    {
        Map<String, BitWord> parse = new HashMap<String, BitWord>();
        
        String register = value.substring(6, 8);
        String index = value.substring(8, 10);
        String indirectAddr = value.substring(10, 11);    
        String address = value.substring(11, 16);
        
        parse.put(KEY_OPCODE, new BitWord(opCode));
        parse.put(KEY_REGISTER, new BitWord(register));
        parse.put(KEY_INDEX, new BitWord(index));
        parse.put(KEY_INDIRECT_ADDR, new BitWord(indirectAddr));
        parse.put(KEY_ADDRESS, new BitWord(address));
        
        return parse;
    }
    
    /**
     * Parses the index, indirectAddr, and address
     * @param opCode
     * @return
     */
    private Map<String, BitWord> parseLoadStoreIndex(String opCode)
    {
        Map<String, BitWord> parse = new HashMap<String, BitWord>();
        
        String index = value.substring(8, 10);
        String indirectAddr = value.substring(10, 11);    
        String address = value.substring(11, 16);
        
        parse.put(KEY_OPCODE, new BitWord(opCode));
        parse.put(KEY_INDEX, new BitWord(index));
        parse.put(KEY_INDIRECT_ADDR, new BitWord(indirectAddr));
        parse.put(KEY_ADDRESS, new BitWord(address));
        
        return parse;
    }
    
    /**
     * Parses the condition code, index, indirectAddr, and address
     * @return
     */
    private Map<String, BitWord> parseJCC()
    {
    	Map<String, BitWord> parse = new HashMap<String, BitWord>();
    
    	String condCode = value.substring(6, 8);
        String index = value.substring(8, 10);
        String indirectAddr = value.substring(10, 11);    
        String address = value.substring(11, 16);
        
        parse.put(KEY_OPCODE, new BitWord(OpCode.JCC));
        parse.put(KEY_CONDITION_CODE, new BitWord(condCode));
        parse.put(KEY_INDEX, new BitWord(index));
        parse.put(KEY_INDIRECT_ADDR, new BitWord(indirectAddr));
        parse.put(KEY_ADDRESS, new BitWord(address));
        
        return parse;
    }
    
    /**
     * Parses the register and immediate
     * @return 
     */
    private Map<String, BitWord> parseImmediate(String opCode)
    {
        Map<String, BitWord> parse = new HashMap<String, BitWord>();
        
        String register = value.substring(6, 8);  
        String immediate = value.substring(11, 16);
        
        parse.put(KEY_OPCODE, new BitWord(opCode));
        parse.put(KEY_REGISTER, new BitWord(register));
        parse.put(KEY_IMMEDIATE, new BitWord(immediate));
        
        return parse;
    }
    private Map<String, BitWord> parseShiftRotate(String opCode)
    {
        Map<String, BitWord> parse = new HashMap<String, BitWord>();
        
        String register = value.substring(6, 8);  
        String ArithmeticOrLogic = value.substring(8, 9);  //this is a flag to adjust for a sign bit; 0 = arithmetic and 1 = logic;
    	String LeftOrRight = value.substring(9, 10);  // left = 1; right = 0;
    	String ShiftCount = value.substring(12, 16);  //number of times to shift the bits
        
        parse.put(KEY_OPCODE, new BitWord(opCode));
        parse.put(KEY_REGISTER, new BitWord(register));
        parse.put(KEY_ARITHMETIC_OR_LOGIC, new BitWord(ArithmeticOrLogic));
        parse.put(KEY_LEFT_OR_RIGHT, new BitWord(LeftOrRight));
        parse.put(KEY_SHIFT_COUNT, new BitWord(ShiftCount));

        return parse;
    }
}
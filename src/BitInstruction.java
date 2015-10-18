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
    public static final String KEY_DEVID = "devId";
    
    
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
            case OpCode.IN:
            	instructionParts = parseIO(opCode);
            	break;
            case OpCode.OUT:
            	instructionParts = parseIO(opCode);
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
    
    /**
     * Parses the register and device id
     * @param opCode
     * @return 
     */
    private Map<String, BitWord> parseIO(String opCode)
    {
        Map<String, BitWord> parse = new HashMap<String, BitWord>();
        
        String register = value.substring(6, 8);  
        String devId = value.substring(11, 16);
        
        parse.put(KEY_OPCODE, new BitWord(opCode));
        parse.put(KEY_REGISTER, new BitWord(register));
        parse.put(KEY_DEVID, new BitWord(devId));
        
        return parse;
    }
}
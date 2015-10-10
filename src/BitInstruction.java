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
                //CHIHOON TODO
                break;
            case OpCode.SMR:
                //CHIHOON TODO
                break;
            case OpCode.AIR:
                //CHIHOON TODO
                break;
            case OpCode.SIR:
                //CHIHOON TODO
                break;
            default:
                break;                        
        }
        
        return instructionParts;
    }  
    
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

}
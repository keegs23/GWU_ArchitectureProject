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
                //KEEGAN TODO
                break;
            case OpCode.TRAP:
                //KEEGAN TODO
                break;
            case OpCode.LDR:
                instructionParts = parseLoadStore();
                break;
            case OpCode.STR:
                instructionParts = parseLoadStore();
                break;
            case OpCode.LDA:
            	instructionParts = parseLoadStore();
                break;
            case OpCode.LDX:
            	instructionParts = parseLoadStoreIndex();
                break;
            case OpCode.STX:
            	instructionParts = parseLoadStoreIndex();
                break;
            case OpCode.AMR:
                //do soemthing
                break;
            case OpCode.SMR:
                //blah
                break;
            case OpCode.AIR:
                //do something
                break;
            case OpCode.SIR:
                //do soemthing
                break;
            default:
            	
                break;                        
        }
        
        return instructionParts;
    }  
    
    private Map<String, BitWord> parseLoadStore()
    {
        Map<String, BitWord> ldrParse = new HashMap<String, BitWord>();
        
        String register = value.substring(6, 8);
        String index = value.substring(8, 10);
        String indirectAddr = value.substring(10, 11);    
        String address = value.substring(11, 16);
        
        ldrParse.put(KEY_OPCODE, new BitWord(OpCode.LDR));
        ldrParse.put(KEY_REGISTER, new BitWord(register));
        ldrParse.put(KEY_INDEX, new BitWord(index));
        ldrParse.put(KEY_INDIRECT_ADDR, new BitWord(indirectAddr));
        ldrParse.put(KEY_ADDRESS, new BitWord(address));
        
        return ldrParse;
    }
    
    private Map<String, BitWord> parseLoadStoreIndex()
    {
        Map<String, BitWord> ldrParse = new HashMap<String, BitWord>();
        
        String index = value.substring(8, 10);
        String indirectAddr = value.substring(10, 11);    
        String address = value.substring(11, 16);
        
        ldrParse.put(KEY_OPCODE, new BitWord(OpCode.LDR));
        ldrParse.put(KEY_INDEX, new BitWord(index));
        ldrParse.put(KEY_INDIRECT_ADDR, new BitWord(indirectAddr));
        ldrParse.put(KEY_ADDRESS, new BitWord(address));
        
        return ldrParse;
    }

}
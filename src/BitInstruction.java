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
    
    //Key = Instruction Name (ex. OpCode), Value = Instruction Bits (ex. "0001")
    private Map<String, BitWord> instructionParts;     
    
    public BitInstruction()
    {
        super();
    	instructionParts = new HashMap<String, BitWord>();
    }
    
    public BitInstruction(BitWord instruction)
    {
    	super(instruction.getValue());
        instructionParts = new HashMap<String, BitWord>();
    }
    
    public Map<String, BitWord> ParseInstruction()
    {         
        String opCode = value.substring(0, 6);
        
        switch (value)
        {
            case OpCode.HLT:
                //KEEGAN TODO
                break;
            case OpCode.TRAP:
                //KEEGAN TODO
                break;
            case OpCode.LDR:
                instructionParts = parseLDR();
                break;
            case OpCode.STR:
                instructionParts = parseSTR();
                break;
            case OpCode.LDA:
                //KEEGAN TODO
                break;
            case OpCode.LDX:
                //KEEGAN TODO
                break;
            case OpCode.STX:
                //KEEGAN TODO
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
    
    private Map<String, BitWord> parseLDR()
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
        // Add the other relevant key-value pairs
        
        return ldrParse;
    }
    
    private Map<String, BitWord> parseSTR()
    {
        
    }
    public Map<String, BitWord> getInstructionParts()
    {
        return instructionParts;
    }
}
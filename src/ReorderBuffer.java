
public class ReorderBuffer {
	
	private InstructionType instructionType;
	private String value;
	private String destinationAddress;
	private Register destinationRegister;
	private boolean ready;
	
	public ReorderBuffer() {}
	
	// For branch and store instructions
	public ReorderBuffer(InstructionType instructionType, String value, String destinationAddress, boolean ready) {
		
		this.setInstructionType(instructionType);
		this.setValue(value);
		this.setDestinationAddress(destinationAddress);
		this.setDestinationRegister(null);
		this.setReady(ready);
	}
	
	// For register instructions
	public ReorderBuffer(InstructionType instructionType, String value, Register destinationRegister, boolean ready) {
		
		this.setInstructionType(instructionType);
		this.setValue(value);
		this.setDestinationAddress(null);
		this.setDestinationRegister(destinationRegister);
		this.setReady(ready);
	}

	public InstructionType getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(InstructionType instructionType) {
		this.instructionType = instructionType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public Register getDestinationRegister() {
		return destinationRegister;
	}

	public void setDestinationRegister(Register destinationRegister) {
		this.destinationRegister = destinationRegister;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

}

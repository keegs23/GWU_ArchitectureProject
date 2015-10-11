import java.io.Serializable;


public class IOObject implements Serializable {
	
	private static final long serialVersionUID = 7254721575993235546L;
	private String opCode = "";
	private int registerId = 0;
	private String devId = "";
	
	public IOObject() {}
	
	public IOObject(String opCode, int registerId, String devId) {
		
		this.opCode = opCode;
		this.registerId = registerId;
		this.devId = devId;
	}
	
	public String getOpCode() {
		return opCode;
	}
	
	public int getRegisterId() {
		return registerId;
	}
	
	public String getDevId() {
		return devId;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setRegisterId(int registerId) {
		this.registerId = registerId;
	}
	
	public void setDevId(String devId) {
		this.devId = devId;
	}

}

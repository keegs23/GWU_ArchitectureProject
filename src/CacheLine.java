public class CacheLine {
	public static final int TAG_SIZE = 12;
	public static final int BLOCK_SIZE = (int) Math.pow(2, 16 - TAG_SIZE);	//16 when TAG_SIZE = 12
	
	/**
	 * The first [TAG_SIZE] bits of the address.
	 */
	BitWord addressTag;
	
	/**
	 * An array of size BLOCK_SIZE that holds copies of the MemoryLocations 
	 * that are within 16 offsets from the addressTag.
	 */
	MemoryLocation[] block;
	
	public CacheLine(String tag) {
		if(tag.length() == TAG_SIZE) {
			addressTag = new BitWord(tag);			
		}
		else {
			System.err.println("Address tag must be of size " + TAG_SIZE + "!");
		}
		block = new MemoryLocation[BLOCK_SIZE];
	}
	
	public BitWord getAddressTag() {
		return addressTag;
	}
	
	public void setAddressTag(String tag) {
		if(tag.length() == TAG_SIZE) {
			addressTag = new BitWord(tag);			
		}
		else {
			System.err.println("Address tag must be of size " + TAG_SIZE + "!");
		}
	}
	
	public MemoryLocation[] getBlock() {
		return block;
	}
	
	public void setBlock(MemoryLocation[] newBlock) {
		if(newBlock.length == BLOCK_SIZE) {
			block = newBlock;
		}
		else {
			System.err.println("Block must be of size " + BLOCK_SIZE + "!");
		}
	}
}

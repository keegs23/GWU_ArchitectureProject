import java.util.LinkedList;


public class WriteBuffer {

	private LinkedList<MemoryLocation> buffer;
	private int maxSize;
	
	public WriteBuffer(int size) {
		buffer = new LinkedList<MemoryLocation>();
		maxSize = size;
	}
	
	public void addToBuffer(MemoryLocation memLoc) {
		
		if (buffer.size() >= maxSize) {
			return;
		}
		buffer.add(memLoc);
	}
	
	public MemoryLocation pushFromBuffer() {
		return buffer.poll();
	}
	
	public void writeToMemory() {
		// write to cache if not already in cache, then write to buffer, then to memory
	}
}

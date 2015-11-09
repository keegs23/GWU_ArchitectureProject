import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;


public class Cache {

	public static final int MAX_BUFFER_SIZE = 4;
	public static final int MAX_CACHE_SIZE = 16;
	
	private Vector<CacheLine> cache;
	private LinkedList<MemoryLocation> writeBuffer;
	
	public Cache() {
		
		cache = new Vector<CacheLine>();
		writeBuffer = new LinkedList<MemoryLocation>();
	}
	
	public Vector<CacheLine> getCache() {
		return cache;
	}
	
	public LinkedList<MemoryLocation> getWriteBuffer() {
		return writeBuffer;
	}
	
	public void setCache(Vector<CacheLine> newCache) {		
		
		if (newCache.size() == MAX_CACHE_SIZE) {
			cache = newCache;
		} else {
			System.err.println("Cache must be of size " + MAX_CACHE_SIZE + "!");
		}
	}
	
	public void setWriteBuffer(LinkedList<MemoryLocation> buffer) {
		
		if (buffer.size() == MAX_BUFFER_SIZE) {
			writeBuffer = buffer;
		} else {
			System.err.println("Write buffer must be of size " + MAX_BUFFER_SIZE + "!");
		}
	}
	
	// Cache Writer method
	// Writes to cache and buffer, then to memory
	public void writeToCacheAndMemory(Map<String, MemoryLocation> memory, String address, MemoryLocation memLoc) {
		
		writeToCache(address, memLoc);
		writeToWriteBuffer(memLoc);
		
		MiniComputer.logger.print("Writing to memory address " + address + "... ");
		
		memory.put(address, writeBuffer.poll());
		
		MiniComputer.logger.println("Done");
	}
	
	private void writeToWriteBuffer(MemoryLocation memLoc) {
		
		MiniComputer.logger.print("Writing to Write Buffer... ");
		
		if (writeBuffer.size() < MAX_BUFFER_SIZE) {
			
			MiniComputer.logger.println("Success");
			writeBuffer.add(memLoc);
		} else {
			
			// should never reach here
			MiniComputer.logger.println("Failed: Write buffer is full!");
			System.err.println("Write buffer is full!");
		}
	}
	
	private void writeToCache(String address, MemoryLocation memLoc) {
		
		MiniComputer.logger.println("Writing to Cache... ");
		
		String firstTwelveBits = address.substring(0, 12);
		boolean writeHit = false;
		
		for (int i = 0; i < cache.size(); i++) {
			if (cache.get(i).getAddressTag().getValue().equals(firstTwelveBits)) { // if addressTag is found in one of cache's CacheLines
				for (int j = 0; j < cache.get(i).getBlock().length; j++) {
					if (cache.get(i).getBlock()[j].getAddress().getValue().equals(address)) { // when address is found in one of the memoryLocations
						
						MiniComputer.logger.println("Write hit in vector " + i + ", block " + j + " for address " + address);
						cache.get(i).getBlock()[j].setValue(memLoc.getValue()); // overwrite with new memoryLocation
						writeHit = true;
						break;
					}
				}
				break;
			}
		}
		if (!writeHit) { // if value was not in cache
			
			MiniComputer.logger.println("Write miss");
			
			if (cache.size() >= MAX_CACHE_SIZE) {
				
				MiniComputer.logger.println("--Cache is full. Removing first element");
				cache.remove(0);
			}
			 
			addNewCacheLine(firstTwelveBits);
		}
	}
	
	// Creates a new CacheLine object with block initialized with 16 MemoryLocations and adds it to cache
	private void addNewCacheLine(String firstTwelveBits) {
		
		MiniComputer.logger.print("Adding new cacheline to cache with addressTag " + firstTwelveBits + "... ");
		
		CacheLine newCacheLine = new CacheLine(firstTwelveBits);
		int binaryInt;
		String lastFourBits;
		
		for (int i = 0; i < newCacheLine.getBlock().length; i++) {
			
			binaryInt = Integer.parseInt(Integer.toBinaryString(i)); // TODO: move this to DataConversion
			lastFourBits = String.format("%04d", binaryInt);
			newCacheLine.getBlock()[i] = new MemoryLocation(firstTwelveBits + lastFourBits, BitWord.VALUE_ZERO); // initialize each block
		}
		
		MiniComputer.logger.println("Done");
		cache.add(newCacheLine);
		
	}
	
}

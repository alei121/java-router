package code.messy.net.ip;

import java.net.InetAddress;

public class Tuple {
    private InetAddress srcAddress, dstAddress;
    private int srcPort, dstPort;

    public Tuple(InetAddress srcAddress, InetAddress dstAddress,
            int srcPort, int dstPort) {
        this.srcAddress = srcAddress;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }
    
    @Override
    public String toString() {
    	return "src=" + srcAddress.toString() + ":" + (srcPort & 0xFFFF) + " dst=" + dstAddress.toString() + ":" + (dstPort & 0xFFFF);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == this) return true;
    	
    	Tuple t = (Tuple)obj;
    	if (!srcAddress.equals(t.srcAddress)) return false;
    	if (!dstAddress.equals(t.dstAddress)) return false;
    	if (srcPort != t.srcPort) return false;
    	if (dstPort != t.dstPort) return false;
    	return true;
    }
    
    @Override
    public int hashCode() {
    	return srcAddress.hashCode() ^ dstAddress.hashCode() ^ srcPort ^ dstPort;
    }

	public InetAddress getSrcAddress() {
		return srcAddress;
	}

	public InetAddress getDstAddress() {
		return dstAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public int getDstPort() {
		return dstPort;
	}
}

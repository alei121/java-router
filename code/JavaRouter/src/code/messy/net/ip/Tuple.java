package code.messy.net.ip;

import java.net.InetAddress;

public class Tuple {
    InetAddress srcAddress, dstAddress;
    int srcPort, dstPort;

    public Tuple(InetAddress srcAddress, InetAddress dstAddress,
            int srcPort, int dstPort) {
        this.srcAddress = srcAddress;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }
    
    @Override
    public String toString() {
    	return "srcAddress=" + srcAddress.toString() + " dstAddress=" + dstAddress.toString() + " srcPort=" + srcPort + " dstPort=" + dstPort;
    }
}

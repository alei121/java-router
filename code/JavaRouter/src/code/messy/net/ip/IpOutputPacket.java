/*
 * Created on Sep 2, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;
import code.messy.util.Flow;
import code.messy.util.PayloadHelper;

public class IpOutputPacket implements OutputPacket {
	private ByteBuffer header;
	private OutputPacket data;
	
	public IpOutputPacket(InetAddress src, InetAddress dst, IpInputPacket.Protocol protocol, int ttl, OutputPacket data) {
        Flow.trace("IpOutputPayload: src=" + src + " dst=" + dst + " protocol=" + protocol);
        
		this.data = data;
		
        header = ByteBuffer.allocateDirect(20);
        
        int length = PayloadHelper.getLength(data);
        
        // start of ip header
        header.put((byte)0x45);
        header.put((byte)0x0);
        // total length
        header.putShort((short)(length + 20));
        // id, flags and fragment offset
        header.putShort((short)0);
        header.putShort((short)0);
        
        // ttl
        header.put((byte)ttl);
        header.put(protocol.getValue());
        
        // header checksum. unknown now
        header.putShort((short)0);
        
        // source
        header.put(src.getAddress());
        // dst
        header.put(dst.getAddress());
        header.flip();

        // Checksum now
        header.putShort(10, IpInputPacket.getChecksum(header, 0, 20));
        header.flip();
	}
	
    public IpOutputPacket(InetAddress src, InetAddress dst, IpInputPacket.Protocol protocol, OutputPacket data) {
    	// TTL 64
    	this(src, dst, protocol, 64, data);
	}
    

	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(header);
		data.getByteBuffers(bbs);
	}
}

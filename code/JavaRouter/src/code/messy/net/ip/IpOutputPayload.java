/*
 * Created on Sep 2, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPayload;
import code.messy.util.Flow;
import code.messy.util.PayloadHelper;

public class IpOutputPayload implements OutputPayload {
	private ByteBuffer header;
	private OutputPayload data;
	
	public IpOutputPayload(InetAddress src, InetAddress dst, IpPacket.Protocol protocol, int ttl, OutputPayload data) {
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
        header.putShort(10, IpPacket.getChecksum(header, 0, 20));
        header.flip();
	}
	
    public IpOutputPayload(InetAddress src, InetAddress dst, IpPacket.Protocol protocol, OutputPayload data) {
    	// TTL 64
    	this(src, dst, protocol, 64, data);
	}
    

	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(header);
		data.getByteBuffers(bbs);
	}
}

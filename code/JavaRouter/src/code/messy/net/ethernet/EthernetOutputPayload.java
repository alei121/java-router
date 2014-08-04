package code.messy.net.ethernet;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPayload;
import code.messy.net.Payload;
import code.messy.util.Flow;

public class EthernetOutputPayload implements OutputPayload {
    private ByteBuffer header;
    private Payload data;
    
    public EthernetOutputPayload(MacAddress src, MacAddress dst, Ethertype type, Payload data) {
        Flow.trace("EthernetOutputPayload: src=" + src + " dst=" + dst + " ethertype=" + type);

        this.data = data;
        header = ByteBuffer.allocateDirect(60);
        header.put(dst.getAddress());
        header.put(src.getAddress());
        header.putShort(type.getValue());
        header.flip();
    }
	
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(header);
		data.getByteBuffers(bbs);
	}

}

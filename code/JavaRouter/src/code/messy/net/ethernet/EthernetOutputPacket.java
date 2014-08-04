package code.messy.net.ethernet;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;
import code.messy.net.Payload;
import code.messy.util.Flow;

public class EthernetOutputPacket implements OutputPacket {
    private ByteBuffer header;
    private Payload data;
    
    public EthernetOutputPacket(MacAddress src, MacAddress dst, Ethertype type, Payload data) {
        Flow.trace("EthernetOutputPayload: src=" + src + " dst=" + dst + " ethertype=" + type);

        this.data = data;
        header = ByteBuffer.allocateDirect(60);
        header.put(dst.getAddress());
        header.put(src.getAddress());
        header.putShort(type.getValue());
        header.flip();
    }
	
	@Override
	public void getOutput(ArrayList<ByteBuffer> bbs) {
		bbs.add(header);
		data.getOutput(bbs);
	}

}

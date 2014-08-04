/*
 * Created on Apr 30, 2008
 */
package code.messy.net.ethernet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.InputPacket;
import code.messy.net.Payload;
import code.messy.net.Port;
import code.messy.net.RawSocket;
import code.messy.util.Flow;

public class EthernetPort extends Thread implements Port, Registrable<Ethertype, Receiver<EthernetInputPacket>> {
    RawSocket socket;
    MacAddress mac;
    String port;

    public EthernetPort(String port) throws IOException {
    	this.port = port;
        socket = new RawSocket(port);
        mac = new MacAddress(socket.getHardwareAddress());
    }

    public MacAddress getMac() {
        return mac;
    }

    @Override
    public String toString() {
        return "EthernetPort(port=" + port + ", mac=" + mac + ")";
    }
    
    @Override
    public void send(Payload payload) throws IOException {
    	ArrayList<ByteBuffer> list = new ArrayList<ByteBuffer>();
    	payload.getByteBuffers(list);
    	ByteBuffer[] bbs = list.toArray(new ByteBuffer[list.size()]);
        socket.write(bbs);
    }

    public void send(MacAddress dstMac, Ethertype type, Payload data) throws IOException {
    	EthernetOutputPacket payload = new EthernetOutputPacket(mac, dstMac, type, data);
    	
    	ArrayList<ByteBuffer> list = new ArrayList<ByteBuffer>();
    	payload.getByteBuffers(list);
    	ByteBuffer[] bbs = list.toArray(new ByteBuffer[list.size()]);
        socket.write(bbs);
    }

    public void send(MacAddress dstMac, Ethertype type, ByteBuffer[] payload) throws IOException {
    	Flow.trace("EthernetPort.send: dst=" + dstMac + " type=" + type);
        ByteBuffer header = ByteBuffer.allocateDirect(60);
        header.put(dstMac.getAddress());
        header.put(mac.getAddress());
        header.putShort(type.getValue());
        header.flip();
        
        ByteBuffer bbs[] = new ByteBuffer[payload.length + 1];
        bbs[0] = header;
        for (int i = 0; i < payload.length; i++) {
            bbs[i + 1] = payload[i];
        }
        socket.write(bbs);
    }
    
    public void send(MacAddress dstMac, Ethertype type, InputPacket packet) throws IOException {
    	Flow.trace("EthernetPort.send: dst=" + dstMac + " type=" + type);
        ByteBuffer header = ByteBuffer.allocateDirect(60);
        header.put(dstMac.getAddress());
        header.put(mac.getAddress());
        header.putShort(type.getValue());
        header.flip();

        ByteBuffer payload = packet.getByteBuffer();
        payload.position(packet.getDataOffset());
        
        ByteBuffer bbs[] = new ByteBuffer[2];
        bbs[0] = header;
        bbs[1] = payload;
        socket.write(bbs);
    }
    
    // TODO new code using publisher and handler
    HashMap<Ethertype, Receiver<EthernetInputPacket>> map = new HashMap<Ethertype, Receiver<EthernetInputPacket>>();
    Receiver<EthernetInputPacket> defaultHandler = null;
    
	@Override
	public void register(Ethertype type, Receiver<EthernetInputPacket> handler) {
        map.put(type, handler);
	}

	@Override
	public void register(Receiver<EthernetInputPacket> handler) {
		defaultHandler = handler;
	}

    @Override
    public void run() {
        try {
            for (;;) {
                ByteBuffer bb = ByteBuffer.allocateDirect(2048);
                socket.read(bb);
                bb.flip();
        		EthernetInputPacket ep = new EthernetInputPacket(bb, this);
        		
        		Flow.traceStart();
        		Flow.trace("EthernetPort port=" + port + " src=" + ep.getSourceAddress());
        		Receiver<EthernetInputPacket> ph = map.get(ep.getEthertype());
                if (ph != null) {
                    ph.receive(ep);
                } else if (defaultHandler != null) {
                    defaultHandler.receive(ep);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

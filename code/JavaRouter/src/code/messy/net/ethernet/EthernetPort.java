/*
 * Created on Apr 30, 2008
 */
package code.messy.net.ethernet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.RawSocket;
import code.messy.util.Flow;

public class EthernetPort extends Thread implements Port, Registrable<Ethertype, Receiver<EthernetPacket>> {
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

    
    // TODO remove this send(Packet)???
    
    @Override
    public void send(Packet packet) throws IOException {
        ByteBuffer bb = packet.getByteBuffer();
        bb.rewind();
        socket.write(bb);
    }

    @Override
    public void send(ByteBuffer bb) throws IOException {
        socket.write(bb);
    }
/*
    @Override
    public void send(InetAddress src, InetAddress dst, ByteBuffer[] payload)
            throws IOException {
        Dump.dumpIndent();
        Dump.dump("EthernetPort: send src=" + src + " dst=" + dst);

        MacAddress dstMac;
        if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        } else {
            dstMac = ArpHandler.getAddress(src, dst, this);
            if (dstMac == null) {
                Dump.dump("Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                Dump.dumpDedent();
                return;
            }
        }
        ByteBuffer header = ByteBuffer.allocateDirect(14);
        header.put(dstMac.getAddress());
        header.put(mac.getAddress());
        // TODO assuming v4 now
        header.putShort((short) 0x800);

        header.flip();

        ByteBuffer bbs[] = new ByteBuffer[payload.length + 1];
        bbs[0] = header;
        for (int i = 0; i < payload.length; i++) {
            bbs[i + 1] = payload[i];
        }
        socket.write(bbs);
        Dump.dumpDedent();
    }
    */

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
    
    public void send(MacAddress dstMac, Ethertype type, Packet packet) throws IOException {
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
    HashMap<Ethertype, Receiver<EthernetPacket>> map = new HashMap<Ethertype, Receiver<EthernetPacket>>();
    Receiver<EthernetPacket> defaultHandler = null;
    
	@Override
	public void register(Ethertype type, Receiver<EthernetPacket> handler) {
        map.put(type, handler);
	}

	@Override
	public void register(Receiver<EthernetPacket> handler) {
		defaultHandler = handler;
	}

    @Override
    public void run() {
        try {
            for (;;) {
                ByteBuffer bb = ByteBuffer.allocateDirect(2048);
                socket.read(bb);
                bb.flip();
        		EthernetPacket ep = new EthernetPacket(bb, this);
        		
        		Flow.traceStart();
        		Flow.trace("EthernetPort port=" + port + " src=" + ep.getSourceAddress());
        		Receiver<EthernetPacket> ph = map.get(ep.getEthertype());
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

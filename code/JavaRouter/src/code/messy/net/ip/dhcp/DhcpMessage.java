package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.ip.dhcp.option.OptionFactory;
import code.messy.net.ip.dhcp.option.OptionIF;

public class DhcpMessage {
	// RFC 2131
	byte op;
	byte htype;
	byte hlen;
	byte hops;
	long xid;
	short secs;
	short flags;
	long ciaddr, yiaddr, siaddr, giaddr;
	byte chaddr[] = new byte[16];
	String sname;
	String file;
	// options
	
	MessageType type;
	
	enum MessageType {
		DISCOVER, OFFER, REQUEST, ACK, INFO, RELEASE
	}
	
	List<OptionIF> options = new ArrayList<OptionIF>();
	
	public DhcpMessage(ByteBuffer bb) {
		op = bb.get();
		htype = bb.get();
		hlen = bb.get();
		hops = bb.get();
		xid = bb.getLong();
		secs = bb.getShort();
		flags = bb.getShort();
		ciaddr = bb.getLong();
		yiaddr = bb.getLong();
		siaddr = bb.getLong();
		giaddr = bb.getLong();
		bb.get(chaddr);
		
		// 192 octets zero padding for discovery.
		// TODO how about other message types?
		bb.position(bb.position() + 192);
		
		long magicCookie = bb.getLong();
		
		// options here
		byte type;
		while ((type = bb.get()) != 255) {
			// Skip padding
			if (type != 0) {
				byte len = bb.get();
				byte[] value = new byte[len];
				bb.get(value);
				options.add(OptionFactory.createOption(type, value));
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DhcpMessage");

		sb.append(" op=" + op);
		sb.append(" htype=" + htype);
		sb.append(" hlen=" + hlen);
		sb.append(" hops=" + hops);
		sb.append(" xid=" + xid);

		sb.append("options=[");
		for (OptionIF option : options) {
			sb.append("{");
			sb.append(option.toString());
			sb.append("} ");
		}
		sb.append("]");
		
		return super.toString();
	}
}

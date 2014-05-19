package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.ip.dhcp.option.OptionFactory;
import code.messy.net.ip.dhcp.option.OptionIF;

public class DhcpMessage {
	// RFC 2131
	byte op, htype, hlen, hops;
	int xid;
	short secs;
	short flags;
	int ciaddr, yiaddr, siaddr, giaddr;
	byte chaddr[] = new byte[16];
	String sname;
	String file;
	List<OptionIF> options = new ArrayList<OptionIF>();
	
	public DhcpMessage(ByteBuffer bb) {
		op = bb.get();
		htype = bb.get();
		hlen = bb.get();
		hops = bb.get();
		xid = bb.getInt();
		secs = bb.getShort();
		flags = bb.getShort();
		ciaddr = bb.getInt();
		yiaddr = bb.getInt();
		siaddr = bb.getInt();
		giaddr = bb.getInt();
		bb.get(chaddr);
		
		// 192 octets zero padding for discovery.
		// TODO how about other message types?
		bb.position(bb.position() + 192);
		
		int magicCookie = bb.getInt();
		if (magicCookie != 0x63825363) {
			System.out.println("No magic");
		}

		System.out.println("option now");

		// options here
		byte type;
		while ((type = bb.get()) != -1) {
			System.out.println("type=" + type);
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

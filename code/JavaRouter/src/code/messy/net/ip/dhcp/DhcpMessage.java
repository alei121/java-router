package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.ip.dhcp.option.DHCPMessageType;
import code.messy.net.ip.dhcp.option.OptionFactory;
import code.messy.net.ip.dhcp.option.OptionIF;
import code.messy.util.ByteHelper;

public class DhcpMessage {
	public final static int MAGIC_COOKIE = 0x63825363;
	
	// RFC 2131
	private byte op, htype, hlen, hops;
	private int xid;
	private short secs;
	private short flags;
	private int ciaddr, yiaddr, siaddr, giaddr;
	private byte chaddr[] = new byte[16];
	private String sname;
	private String file;
	private List<OptionIF> options = new ArrayList<OptionIF>();
	
	private int messageType = 0;
	
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

		// options here
		byte type;
		while ((type = bb.get()) != -1) {
			// Skip padding
			if (type != 0) {
				byte len = bb.get();
				byte[] value = new byte[len];
				bb.get(value);
				OptionIF option = OptionFactory.createOption(type, value);
				options.add(option);
				if (option instanceof DHCPMessageType) {
					messageType = ((DHCPMessageType)option).getType();
				}
			}
		}
	}
	
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocateDirect(1024);
		bb.put(op);
		bb.put(htype);
		bb.put(hlen);
		bb.put(hops);
		bb.putInt(xid);
		bb.putShort(secs);
		bb.putShort(flags);

		bb.putInt(ciaddr);
		bb.putInt(yiaddr);
		bb.putInt(siaddr);
		bb.putInt(giaddr);
		bb.put(chaddr);
		
		for (int i = 0; i < 192; i++) {
			bb.put((byte)0);
		}
		bb.putInt(MAGIC_COOKIE);
		
		for (OptionIF option : options) {
			bb.put(option.getPayload());
		}
		
		bb.put((byte)-1);
		bb.flip();
		return bb;
	}
	
	public void setOp(byte op) {
		this.op = op;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DhcpMessage");

		sb.append(" op=" + op);
		sb.append(" htype=" + htype);
		sb.append(" hlen=" + hlen);
		sb.append(" hops=" + hops);
		sb.append(" xid=" + xid);
		sb.append(" xid=" + xid);
		sb.append(" chaddr=");
		sb.append(ByteHelper.toString(chaddr, ":", hlen));
		sb.append(" options=[");
		for (OptionIF option : options) {
			sb.append("{");
			sb.append(option.toString());
			sb.append("} ");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	public void setOptions(List<OptionIF> options) {
		this.options = options;
	}
	
	public int getMessageType() {
		return messageType;
	}
	
	public void setYiaddr(int yiaddr) {
		this.yiaddr = yiaddr;
	}
	
	public byte[] getChaddr() {
		return chaddr;
	}
	
	public byte getHlen() {
		return hlen;
	}
}

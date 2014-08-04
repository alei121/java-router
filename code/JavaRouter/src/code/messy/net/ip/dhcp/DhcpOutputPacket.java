package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.OutputPacket;
import code.messy.net.ip.dhcp.option.OptionIF;

public class DhcpOutputPacket implements OutputPacket {
	private DhcpMessage message;

	public DhcpOutputPacket(DhcpMessage message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "DhcpOutputPayload " + message.toString();
	}
	
	public void setOp(byte op) {
		message.setOp(op);
	}
	
	public void setOptions(List<OptionIF> options) {
		message.setOptions(options);
	}
	
	public void setYiaddr(int yiaddr) {
		message.setYiaddr(yiaddr);
	}
	
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		// TODO getPayload causes dhcp message to be reconstructed.
		// TODO maybe more efficient as immutable
		bbs.add(message.getPayload());
	}
}

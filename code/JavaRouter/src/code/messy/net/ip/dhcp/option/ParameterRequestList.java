package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class ParameterRequestList implements OptionIF {
	byte[] list;
	
	public ParameterRequestList(byte[] list) {
		this.list = list;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(list.length + 2);
		bb.put((byte)55);
		bb.put((byte)list.length);
		bb.put(list);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ParameterRequestList list=");
		for (int i = 0; i < list.length; i++) {
			Code code = Code.find(list[i]);
			if (code != null) {
				sb.append(code.name() + "(" + list[i] + ")");
			}
			else {
				sb.append(list[i]);
			}
			if (i < (list.length - 1)) sb.append(",");
		}
		return sb.toString();
	}
}

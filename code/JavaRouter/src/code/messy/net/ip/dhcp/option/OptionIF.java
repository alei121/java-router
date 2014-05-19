package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public interface OptionIF {
	ByteBuffer getPayload();
}

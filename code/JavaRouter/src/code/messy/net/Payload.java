package code.messy.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public interface Payload {
	public void getOutput(ArrayList<ByteBuffer> bbs);
}

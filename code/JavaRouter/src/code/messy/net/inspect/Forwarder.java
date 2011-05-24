package code.messy.net.inspect;

import java.io.IOException;
import java.nio.ByteBuffer;

import code.messy.Service;
import code.messy.net.RawSocket;

public class Forwarder implements Service {
	boolean keepRunning = true;
	RawSocket rs1, rs2;
	Thread forwardLoop;
	
	InspectIF inspector;

	public Forwarder(RawSocket rs1, RawSocket rs2) {
		this.rs1 = rs1;
		this.rs2 = rs2;
	}

	class ForwardLoop implements Runnable {
		@Override
		public void run() {
			ByteBuffer bb = ByteBuffer.allocateDirect(2048);

			try {
				while (keepRunning) {
					bb.clear();
					rs1.read(bb);
					bb.flip();
					if (inspector != null) {
						inspector.inspect(bb);
						bb.rewind();
					}
					rs2.write(bb);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setInspector(InspectIF inspector) {
		this.inspector = inspector;
	}
	
	@Override
	public void start() throws Exception {
		forwardLoop = new Thread(new ForwardLoop());
		forwardLoop.start();
	}
	
	@Override
	public void stop() {
		keepRunning = false;
		forwardLoop.interrupt();
		
		// TODO need to interrupt read/write
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
	}
}

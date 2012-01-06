package code.messy.net.ip.tcp;

import static org.junit.Assert.assertEquals;

import java.nio.BufferOverflowException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RingBufferTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOffset() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());

		byte[] b = new byte[10];
		rb.write(b, 0, 2);
		assertEquals(2, rb.size());
		rb.write(b, 4, 2);
		assertEquals(4, rb.size());
		rb.write(b, 8, 2);
		assertEquals(6, rb.size());
		
		rb.read(b, 4, 6);
		assertEquals(0, rb.size());
	}
	
	@Test
	public void testWrite() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());
		
		byte[] b = new byte[1];
		rb.write(b);
		assertEquals(9, rb.remaining());
		
		b = new byte[9];
		rb.write(b);
		assertEquals(0, rb.remaining());
	}

	@Test
	public void testReadWrite() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());
		
		byte[] b = new byte[1];
		rb.write(b);
		assertEquals(9, rb.remaining());

		rb.read(b);
		assertEquals(10, rb.remaining());
	}
	
	@Test
	public void testRing() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());
		
		byte[] b = new byte[3];
		rb.write(b);
		assertEquals(7, rb.remaining());
		rb.write(b);
		assertEquals(4, rb.remaining());
		rb.write(b);
		assertEquals(1, rb.remaining());
		rb.read(b);
		assertEquals(4, rb.remaining());
		rb.write(b);
		assertEquals(1, rb.remaining());
		rb.read(b);
		assertEquals(4, rb.remaining());
		rb.read(b);
		assertEquals(7, rb.remaining());
		rb.read(b);
		assertEquals(10, rb.remaining());
	}
	
	@Test
	public void testContent() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());
		
		byte[] b1 = new byte[10];
		byte[] b2 = new byte[10];
		
		for (int i = 0; i < 10; i++) {
			b1[i] = (byte)i;
		}

		// making it cross the boundary first
		rb.write(b1, 0, 8);
		rb.read(b2, 0, 7);
		rb.write(b1, 0, 8);
		rb.read(b2, 0, 1);
		assertEquals(8, rb.size());
		
		// now read and check
		rb.read(b2, 0, 8);
		for (int i = 0; i < 8; i++) {
			assertEquals(i, b2[i]);
		}
	}
	
	@Test(expected=BufferOverflowException.class)
	public void testOverflow() {
		RingBuffer rb = new RingBuffer(10);
		assertEquals(10, rb.remaining());
		
		byte[] b = new byte[11];
		rb.write(b);
	}
}

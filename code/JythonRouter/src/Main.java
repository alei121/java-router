import java.io.FileInputStream;

import org.python.util.PythonInterpreter;

import code.messy.Server;

public class Main {
	/**
	 * Main <preconfig.py> <config.py>
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		byte[] b = new byte[1024];
		FileInputStream in = new FileInputStream(args[0]);
		int len = in.read(b);
		in.close();
		String preconfig = new String(b, 0, len);

		in = new FileInputStream(args[1]);
		len = in.read(b);
		in.close();
		String config = new String(b, 0, len);

        PythonInterpreter interp = new PythonInterpreter();
        interp.exec(preconfig);
        interp.exec(config);
        
        Server server = Server.getInstance();
        server.init();
        server.start();
	}

}

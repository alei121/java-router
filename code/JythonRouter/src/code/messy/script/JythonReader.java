package code.messy.script;

import java.io.FileInputStream;
import org.python.util.PythonInterpreter;

/**
 * Main <config-1.py> <config-2.py> ... <config-n.py>
 * 
 * @author alei
 *
 */
public class JythonReader {
	private static final int MAX_FILE_SIZE = 32 * 1024;
	
	public static void main(String[] args) throws Exception {
		byte[] b = new byte[MAX_FILE_SIZE];
        PythonInterpreter interp = new PythonInterpreter();

        for (String arg : args) {
        	FileInputStream in = new FileInputStream(arg);
        	int len = in.read(b);
        	in.close();
        	
        	if (len >= MAX_FILE_SIZE) {
        		System.out.println("Aborting because file size larger than buffer: " + arg);
        		System.exit(1);
        	}
        	
        	String config = new String(b, 0, len);
        	interp.exec(config);
        }
	}

}

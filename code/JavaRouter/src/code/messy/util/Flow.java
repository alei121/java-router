/*
 * Created on Apr 24, 2008
 */
package code.messy.util;


public class Flow {
    static ThreadLocal<Integer> currentCount = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return new Integer(0);
        }
    };

    public static void trace(String message) {
        int count = currentCount.get();
        for (int i = 0; i < count; i++) {
            System.out.print("  ");
        }
        System.out.println(message);
        count++;
        currentCount.set(count);
    }
    
    public static void traceStart() {
    	currentCount.set(0);
    }
}

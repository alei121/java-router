package code.messy.net.ip;

import java.net.InetAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import code.messy.net.ip.util.Mask;
import code.messy.util.IpAddressHelper;

public class NetworkMap<T> {
    HashMap<InetAddress, HashMap<InetAddress, T>> maskMap = new HashMap<InetAddress, HashMap<InetAddress, T>>();
    TreeMap<Integer, InetAddress> mapOfPrefixToMask = new TreeMap<>(new ReverseComparator());

	// Using reverse sorted to match bigger mask first in getByMasking
	private static class ReverseComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
		
	}

	/**
	 * To match address directly
	 * 
	 * @param address
	 * @param item
	 * @return
	 */
    public T put(InetAddress address, T item) {
    	InetAddress mask = IpAddressHelper.BROADCAST_ADDRESS;
        HashMap<InetAddress, T> networkMap = maskMap.get(mask);

        if (networkMap == null) {
            networkMap = new HashMap<InetAddress, T>();
            maskMap.put(mask, networkMap);
            mapOfPrefixToMask.put(32, mask);
        }
        return networkMap.put(address, item);
    }

    public T put(NetworkNumber network, T item) {
    	InetAddress mask = network.getMask();
        HashMap<InetAddress, T> networkMap = maskMap.get(mask);

        if (networkMap == null) {
            networkMap = new HashMap<InetAddress, T>();
            maskMap.put(mask, networkMap);
            mapOfPrefixToMask.put(network.prefix, mask);
        }
        return networkMap.put(network.getAddress(), item);
    }
    
    public void remove(NetworkNumber network) {
    	HashMap<InetAddress, T> networkMap = maskMap.get(network.getMask());
    	if (networkMap != null)
    		networkMap.remove(network.getAddress());
    }
    
    public T getByMasking(InetAddress address) {
    	Set<Entry<Integer, InetAddress>> set = mapOfPrefixToMask.entrySet();
    	for (Entry<Integer, InetAddress> entry : set) {
    		InetAddress mask = entry.getValue();
            InetAddress network = Mask.applyMask(address, mask);
            HashMap<InetAddress, T> networkMap = maskMap.get(mask);
            T item = networkMap.get(network);
            if (item != null)
                return item;
        }
        return null;
    }
    
    public boolean contains(NetworkNumber network) {
    	HashMap<InetAddress, T> networkMap = maskMap.get(network.getMask());
    	if (networkMap != null) {
    		return networkMap.containsKey(network);
    	}
    	return false;
    }

    public T get(NetworkNumber network) {
    	HashMap<InetAddress, T> networkMap = maskMap.get(network.getMask());
    	if (networkMap != null) {
    		return networkMap.get(network.getAddress());
    	}
    	return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("NetworkMap:");
    	Set<Entry<Integer, InetAddress>> maskSet = mapOfPrefixToMask.entrySet();
    	for (Entry<Integer, InetAddress> maskEntry : maskSet) {
    		InetAddress mask = maskEntry.getValue();
            HashMap<InetAddress, T> networkMap = maskMap.get(mask);
            Set<Entry<InetAddress, T>> set = networkMap.entrySet();
            for (Entry<InetAddress, T> entry : set) {
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("/");
                sb.append(mask);
                sb.append("/");
                sb.append(entry.getValue());
            }
        }
        return sb.toString();
    }
}

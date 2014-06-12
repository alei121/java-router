package code.messy.net.ip.dhcp.option;

import java.util.HashMap;
import java.util.Map;

public enum Code {
	SubnetMask(1),
	TimeOffset(2),
	RouterOption(3),
	TimeServerOption(4),
	NameServerOption(5),
	DomainNameServerOption(6),
	HostNameOption(12),
	DomainName(15),
	InterfaceMTUOption(26),
	BroadcastAddressOption(28),
	NetworkTimeProtocolServersOption(42),
	NetBIOSOverTCPIPNameServerOption(44),
	NetBIOSOverTCPIPScopeOption(47),
	RequestedIPAddress(50),
	IPAddressLeaseTime(51),
	OptionOverload(52),
	DHCPMessageType(53),
	ServerIdentifier(54),
	ParameterRequestList(55),
	Message(56),
	DomainSearchOption(119),
	ClasslessRouteOption(121)
	;
	
	private static Map<Integer, Code> map = new HashMap<>();
	static {
		for (Code code : Code.values()) {
			map.put(code.getCode(), code);
		}
	}
	
	public static Code find(int code) {
		return map.get(code);
	}
	
	private int code;
	private Code(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}

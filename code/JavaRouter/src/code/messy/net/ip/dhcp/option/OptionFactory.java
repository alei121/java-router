package code.messy.net.ip.dhcp.option;

public class OptionFactory {
	public static OptionIF createOption(int type, byte[] value) {
		Code code = Code.find(type);
		switch (code) {
		case SubnetMask:
			return new SubnetMask(value);
		case RouterOption:
			return new RouterOption(value);
		case DomainNameServerOption:
			return new DomainNameServerOption(value);
		case HostNameOption:
			return new HostNameOption(value);
		case RequestedIPAddress:
			return new RequestedIPAddress(value);
		case IPAddressLeaseTime:
			return new IPAddressLeaseTime(value);
		case DHCPMessageType:
			return new DHCPMessageType(value);
		case ServerIdentifier:
			return new ServerIdentifier(value);
		case ParameterRequestList:
			return new ParameterRequestList(value);
		default:
			return new Unknown(type, value);
		}
	}
}

package code.messy.net.ip.dhcp.option;

public class OptionFactory {
	public static OptionIF createOption(int type, byte[] value) {
		switch (type) {
		case 1:
			return new SubnetMask(value);
		case 3:
			return new RouterOption(value);
		case 6:
			return new DNSOption(value);
		case 50:
			return new RequestedIPAddress(value);
		case 51:
			return new IPAddressLeaseTime(value);
		case 53:
			return new MessageType(value);
		case 54:
			return new ServerIdentitier(value);
		case 55:
			return new ParameterRequestList(value);
		}
		return null;
	}
}

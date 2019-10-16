package ru.vniiem.can;

import ru.vniiem.socketcan.SocketCanDevice;

public class SocketCanTest {

	public static void main(String[] args) {
	ICanDevice device = new SocketCanDevice();
	device.openDevice("mxcan0");
	CanPacket cp =device.readPacket(10000);
	if (cp!=null)
		device.sendPacket(cp);
	}

}

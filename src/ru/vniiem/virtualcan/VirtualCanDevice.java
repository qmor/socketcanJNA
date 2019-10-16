package ru.vniiem.virtualcan;

import java.util.Random;

import ru.vniiem.can.CanPacket;
import ru.vniiem.can.ICanDevice;

public class VirtualCanDevice implements ICanDevice{
String devname = "";
	@Override
	public void openDevice(String devname) {
		this.devname = devname;
	}

	@Override
	public void closeDevice() {


	}

	@Override
	public CanPacket readPacket(int timeoutMs) {
		int timer = 0;
		while (timer<timeoutMs)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			timer+=1;
			if (new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean())
			{
				byte[] data = new byte[8];
				new Random().nextBytes(data);
				return new CanPacket(new Random().nextInt()|0x80000000, 8, data);
			}
		}
		return null;
	}

	@Override
	public void sendPacket(CanPacket pkt) {


	}
	@Override
	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}

}

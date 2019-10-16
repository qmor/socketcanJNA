package ru.vniiem.socketcan;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import fr.labri.unixsocket.CanSocket;
import ru.vniiem.can.CanPacket;
import ru.vniiem.can.ICanDevice;

public class SocketCanDevice implements ICanDevice {
	int fd = 0;
	CanSocket cs;
	String devname = "";
	public void openDevice(String devname)
	{
		try {
			cs = new CanSocket(devname);
			this.devname = devname;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void closeDevice() {

	}

	@Override
	public CanPacket readPacket(int timeoutMs) {
		int readed =0;
		int timer = 0;
		if (cs==null)
			return null;
		while (timer<timeoutMs || timeoutMs==0)
		{
			try {
				byte[] data = new byte[16];
				readed = cs.getInputStream().read(data);
				if (readed == 16)
				{
					CanPacket cp = CanPacket.createFromBytes(data);
					return cp;
				}
				else
				{
					//System.out.print(String.format("Readed less than 16 %d", readed));
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			timer+=1;
		}
		return null;
	}

	@Override
	public void sendPacket(CanPacket pkt) {
		if (cs==null)
			return;
		byte[] outpacket = new byte[16];
		ByteBuffer bb = ByteBuffer.wrap(outpacket) ;
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt((pkt.getId()|1<<31)&0xffffffff);
		int dlc = pkt.getDlc();
		bb.put((byte) dlc);
		System.arraycopy(pkt.getData(), 0, outpacket, 8, dlc);
		try {
			cs.getOutputStream().write(outpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}
}

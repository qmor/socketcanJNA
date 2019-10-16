package ru.vniiem.can;

public interface ICanDevice {
	public void openDevice(String devname);
	public void closeDevice();
	public CanPacket readPacket(int timeoutMs);
	public void sendPacket(CanPacket pkt);
	public String getDevname();
}

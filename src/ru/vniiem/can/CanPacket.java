package ru.vniiem.can;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
public class CanPacket {
	public CanPacket()
	{
		
	}
	public CanPacket (int id, int dlc, byte[] data)
	{
		this.id = id;
		this.dlc = dlc;
		this.data= data;
	}
	public static CanPacket createFromBytes(byte[] socketbytes)
	{
		if (socketbytes!=null && socketbytes.length == 16)
		{
			ByteBuffer bb = ByteBuffer.wrap(socketbytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			int id = bb.getInt();
			int dlc = bb.get();
			byte[] data = new byte[dlc];
			System.arraycopy(socketbytes, 8, data, 0, dlc);
			CanPacket cp = new CanPacket(id, dlc, data);
			return cp;
		}
		return null;
	}
	int packetNumber = 0;
	public int getPacketNumber() {
		return packetNumber;
	}
	public void setPacketNumber(int packetNumber) {
		this.packetNumber = packetNumber;
	}
	public LocalDateTime getPacketTime() {
		return packetTime;
	}
	public void setPacketTime(LocalDateTime packetTime) {
		this.packetTime = packetTime;
	}
	
   
    private long pk;
	
	public long getPk() {
		return pk;
	}
	public void setPk(long pk) {
		this.pk = pk;
	}
	
	LocalDateTime packetTime = LocalDateTime.now();
	
	int id;
	byte[] data = new byte[8];

	int dlc;
/**
 * Packet source (device name)
 */
	String origin ="";
	public int getId() {
		return id;
	}
	public byte[] getData() {
		return data;
	}
	public int getDlc() {
		return dlc;
	}
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes,int offset,int len) {
		if (bytes==null)
			return null;
		char[] hexChars = new char[len * 2];
		for ( int j = offset; j < offset+len; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[(j-offset) * 2] = hexArray[v >>> 4];
			hexChars[(j-offset) * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	@Override
	public String toString() {
	
		if (dlc!=0)
		{
			return String.format("ID: %08X DLC: %02d data %s", this.id, this.dlc, bytesToHex(this.data, 0, data.length));
		}
		return String.format("ID: %08X DLC: %02d", this.id, this.dlc);
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}

package fr.labri.unixsocket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * Adapted from the projet syslog4j
 * URL : www.syslog4j.org
 * 
 */
public class CanSocket extends Socket {
	public static final String LIBC = "c";

	private static final int AF_CAN = 29;
	
	private static final int SOCK_RAW	= 3;
	private static final int CAN_RAW	=	1; /* RAW sockets */
	private static final int SIOCGIFINDEX=0x8933;
	private static final int  F_SETFL=		4;	/* set file->f_flags */
	private static final int O_NONBLOCK =	0x00004000;
	private JNAUnixSocket library = null;
	private int sockfd = -1;
	private InputStream is = null;
	private OutputStream os = null;

	public CanSocket(String devname) throws SocketException {
		if (Platform.isWindows() || Platform.isWindowsCE()) {
			throw new SocketException(
					"loadLibrary(): Unix sockets are not supported on Windows platforms");
		}

		library = (JNAUnixSocket) Native.load(LIBC, JNAUnixSocket.class);
		this.sockfd = socket(AF_CAN, SOCK_RAW, CAN_RAW);
		if (sockfd == -1)
		{
			new SocketException("UnixSocket(..): could not open socket");
		}
		
		/*
		 * 	struct ifreq ifr;
		strcpy(ifr.ifr_name, "can0");
		ioctl(fd, SIOCGIFINDEX, &ifr); // ifr.ifr_ifindex gets filled with that device's index
		 */
		byte[] ifreq = new byte[40];
		System.arraycopy( devname.getBytes(), 0, ifreq, 0, devname.getBytes().length);

		
		int res = library.ioctl(sockfd, SIOCGIFINDEX, ifreq); 
		ByteBuffer bb = ByteBuffer.wrap(ifreq,16,16);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int ifindex = bb.getInt();
			
		/*
		 * 	struct sockaddr_can addr;
			addr.can_family = AF_CAN;
			addr.can_ifindex = ifr.ifr_ifindex;
			bind(fd, (struct sockaddr*)&addr, sizeof(addr));
		 */
		byte[] addr = new byte[16];
		bb = ByteBuffer.wrap(addr);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(AF_CAN);
		bb.putInt(ifindex);
		

		res = library.bind(sockfd, addr, 16);

		// Set to non blocking
		res = library.fcntl(sockfd, F_SETFL, O_NONBLOCK);
		this.is = new BufferedInputStream(new UnixSocketInputStream(this));
		this.os = new UnixSocketOutputStream(this);
		
	}

	
	private void throwIOException(String prefixMessage, LastErrorException lee)
			throws IOException {
		String strerror = library.strerror(lee.getErrorCode());

		throw new IOException(prefixMessage + ": " + strerror);
	}

	private void throwSocketException(String prefixMessage,
			LastErrorException lee) throws SocketException {
		String strerror = library.strerror(lee.getErrorCode());

		throw new SocketException(prefixMessage + ": " + strerror);
	}

	private int socket(int domain, int type, int protocol)
			throws SocketException {
		try {
			int sockfd = library.socket(domain, type, protocol);

			return sockfd;

		} catch (LastErrorException lee) {
			throwSocketException("socket(..): could not open socket", lee);
			return -1;
		}
	}

	/*
	 * used by UnixInputStream
	 */
	int read(byte[] buf, int count) throws IOException {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(buf);

			int length = this.library.read(this.sockfd, buffer, count);

			return length;

		} catch (LastErrorException lee) {
			throwIOException("read(..): could not read from socket", lee);
			return -1;
		}
	}

	/* 
	 * Used by UnixOutputStream
	 */
	int write(byte[] buf, int count) throws IOException {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(buf);

			int length = this.library.write(this.sockfd, buffer, count);

			return length;

		} catch (LastErrorException lee) {
			throwIOException("write(..): could not write to socket", lee);
			return -1;
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return is;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public void shutdownInput() throws IOException {
		is = null;
	}

	@Override
	public void shutdownOutput() throws IOException {
		os = null;
	}

	@Override
	public synchronized void close() throws IOException {
		try {
			shutdownInput();
			shutdownOutput();
			this.library.close(this.sockfd);

		} catch (LastErrorException lee) {
			throwIOException("close(..): could not close socket", lee);
		}
	}
}
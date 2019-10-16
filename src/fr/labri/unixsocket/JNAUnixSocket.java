package fr.labri.unixsocket;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

interface JNAUnixSocket extends Library {

	int socket(int domain, int type, int protocol);
	int read(int fd, ByteBuffer buffer, int count);
	int write(int fd, ByteBuffer buffer, int count);
	int write(int fd, byte[] buffer, int count);
	int ioctl(int fd, int cmd, Pointer p);
	int ioctl(int fd, int cmd, byte[] p);
	int bind(int sockfd, byte[] addr,int addrlen);
	int fcntl(int fd, int cmd, int arg );
	public int close(int fd);
	public String strerror(int errno);
}
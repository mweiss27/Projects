package com.weiss.remote_connect.packets;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

import javax.imageio.ImageIO;


public class FramePacket extends Packet {

	private static final long serialVersionUID = 1L;
	
	public transient BufferedImage frame;
	public byte[] frameBytes;
	public final Point mouseLocation;

	public FramePacket(final BufferedImage frame, final Point mouseLocation) {
		this.frame = frame;
		this.mouseLocation = mouseLocation;
	}
	
	@Override
	public DatagramPacket get() throws IOException {
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ImageIO.write(this.frame, "JPG", bOut);
		this.frameBytes = bOut.toByteArray();
		
		final ByteArrayOutputStream objectBytesOut = new ByteArrayOutputStream();
		final ObjectOutputStream oOut = new ObjectOutputStream(objectBytesOut);
		oOut.writeObject(this);
		final byte[] objBytes = objectBytesOut.toByteArray();
		return new DatagramPacket(objBytes, objBytes.length);
	}

}

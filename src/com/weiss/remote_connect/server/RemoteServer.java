package com.weiss.remote_connect.server;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.weiss.io.Log;
import com.weiss.remote_connect.packets.FramePacket;
import com.weiss.remote_connect.packets.Packet;
import com.weiss.remote_connect.packets.mouse.MouseEventPacket;
import com.weiss.remote_connect.util.RemoteConnectConfig;

/**
 * A single-threaded server to ensure that only one client can connect at a time.
 */
public class RemoteServer {

	private final ServerSocket server;
	private Socket connectedClient;

	private final Rectangle screen = new Rectangle(0, 0);

	private Point mouseLocation;
	private Robot robot;
	private BufferedImage screenCapture;

	private ExecutorService exec = Executors.newFixedThreadPool(2);

	public RemoteServer(final int port) throws IOException {
		this.server = new ServerSocket(RemoteConnectConfig.PORT);

		this.screen.setSize(new Dimension(1920, 1080));

		try {
			this.robot = new Robot();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create Robot?");
		}
	}

	public void start() {
		while (true) {
			try {

				Log.info("Waiting for a client to connect...");
				this.connectedClient = this.server.accept();
				Log.info("Client conncted. Starting up threads");
				final Future<?> inputThread = this.exec.submit(new Runnable() {
					@Override
					public void run() {
						try {

							final byte[] buffer = new byte[1024];
							final DataInputStream readIn = new DataInputStream(RemoteServer.this.connectedClient.getInputStream());

							do {
								readIn.read(buffer);

								final Object o = RemoteServer.this.deserialize(buffer);

								if (o == null) {
									Log.err("We received a null object.");
									continue;
								}

								if (!(o instanceof Packet)) {
									Log.err("We received an object, but it isn't a Packet: " + o.getClass());
									continue;
								}

								if (o instanceof MouseEventPacket) {
									Log.info("We received a MouseEventPacket: " + o.toString());
									final MouseEventPacket mouseEventPacket = (MouseEventPacket) o;
									mouseEventPacket.handleEvent(RemoteServer.this.robot);
								}
								else {
									Log.err("We received a Packet, but we don't recognize it: " + o.getClass());
								}

							} while (!RemoteServer.this.server.isClosed());
						} catch (final Exception any) {
							any.printStackTrace();
						}
					}
				});

				final Future<?> outputThread = this.exec.submit(new Runnable() {
					@Override
					public void run() {
						try {
							
							final DataOutputStream writeOut = new DataOutputStream(RemoteServer.this.connectedClient.getOutputStream());
							
							do {
								RemoteServer.this.screenCapture = RemoteServer.this.robot.createScreenCapture(RemoteServer.this.screen);
								RemoteServer.this.mouseLocation = MouseInfo.getPointerInfo().getLocation();
								
								final DatagramPacket packet = new FramePacket(RemoteServer.this.screenCapture, RemoteServer.this.mouseLocation).get();
								
								if (!RemoteServer.this.connectedClient.isClosed() && !RemoteServer.this.connectedClient.isOutputShutdown()) {
									//System.out.println("[Server] Sending " + packet.getLength() + " bytes.");
									writeOut.writeInt(packet.getData().length);
									writeOut.write(packet.getData());
									System.gc();
								}
							} while (!RemoteServer.this.server.isClosed());
						} catch (final Exception any) {
							any.printStackTrace();
						}
					}
				});

				inputThread.get();
				outputThread.get();

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized Object deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
		final ObjectInputStream oIn = new ObjectInputStream(bIn);

		return oIn.readObject();
	}

}

package com.weiss.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ScreenCaptureUtil {

	private static final Dimension _720p = new Dimension(1366, 768);
	private static final Dimension _1080p = new Dimension(1920, 1080);
	private static final Dimension _1440p = new Dimension(2560, 1440);

	private static final Dimension SCREEN = _1440p;
	public final Rectangle WINDOW = new Rectangle(0, 0, SCREEN.width, SCREEN.height);

	private ExecutorService exec = Executors.newSingleThreadExecutor();

	public volatile boolean running = false;

	private BlockingQueue<BufferedImage> images = new LinkedBlockingQueue<>();

	private Thread mainThread;

	public void start() {
		if (running) {
			throw new IllegalStateException("This ScreenCaptureUtil is already running!");
		}
		mainThread = Thread.currentThread();
		running = true;
		exec.execute(new Runnable() {
			@Override
			public void run() {

				try {
					final Robot r = new Robot();
					while (running) {
						submitScreenshot(System.currentTimeMillis(), r.createScreenCapture(ScreenCaptureUtil.this.WINDOW));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void submitScreenshot(final long time, final BufferedImage img) {
		try {
			System.out.println("Adding image. Time since last: " + Time.toc(mainThread));
		} catch (Exception firstWillBlowUp) {}

		images.offer(img);
		synchronized(images) {
			images.notifyAll();
		}

		Time.tic(mainThread);
	}

	public BufferedImage getNextImage() {
		try {
			return images.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void stop() {
		this.running = false;
	}

	public static void main(String[] args) {
		final ScreenCaptureUtil util = new ScreenCaptureUtil();

		final JFrame frame = new JFrame();


		final AtomicReference<BufferedImage> image = new AtomicReference<>();

		final JPanel p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (image.get() != null) {
					g.drawImage(image.get(), 0, 0, null);
				}
			}
		};
		p.setPreferredSize(SCREEN);

		final MouseAdapter ma = new MouseAdapter() {

			private Point pressed;

			@Override
			public void mousePressed(MouseEvent e) {
				pressed = e.getPoint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					util.WINDOW.x = 0;
					util.WINDOW.y = 0;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				final Point dragged = e.getPoint();

				final int dx = dragged.x - pressed.x;
				final int dy = dragged.y - pressed.y;

				util.WINDOW.x -= dx;
				util.WINDOW.y -= dy;

				pressed = dragged;
			}
		};

		p.addMouseListener(ma);
		p.addMouseMotionListener(ma);

		frame.setContentPane(p);
		frame.pack();
		frame.setLocation(2560, 0);
		frame.setVisible(true);
		frame.toFront();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				util.running = false;
			}
		});

		final Thread t = Thread.currentThread();

		Worker.execute(new Runnable() {
			@Override
			public void run() {
				int images = 0;
				Time.tic();
				do {
					synchronized(t) {
						t.notify();
					}
					images++;
					image.set(util.getNextImage());
					System.out.println("Pulled an image. Current queue: " + util.images.size());
					p.repaint();
				}
				while (util.running);
				final long elapsed = Time.toc();
				System.out.println("Processed " + images + " images in " + (elapsed / 1000) + "s");
				System.out.println("Average FPS: " + (images / (elapsed / 1000)));
				System.exit(0);
			}
		});
		synchronized(t) {
			try {
				t.wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		util.start();

	}

}

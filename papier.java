import java.net.*;
import java.awt.Color;
import java.io.*;
import java.lang.System;
import java.awt.Image;
import java.awt.image.*;
import java.util.Random;
import javax.imageio.ImageIO;

class ImageWriter implements Runnable {
	private BufferedImage img;
	private int width;
	private int height;
	private int from;
	private int to;
	private int tox;
	private int fromx;

	private Writer out;

	private Random rand;

	ImageWriter(String server, int port, BufferedImage img, int from, int to, int fromx, int tox) {
		try {
			connect(server, port);
		} catch (Exception e) {
			System.out.println(e);
		}

		this.img = img;
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.from = from;
		this.to = to;
		this.fromx = fromx;
		this.tox = tox;

		this.out = out;
		this.rand = new Random();
	}

	private final static String rgbToHex(int rgb) throws NullPointerException {
		String hexColour = Integer.toHexString(rgb & 0xffffff);
		if (hexColour.length() < 6) {
			hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		}
		return hexColour;
	}

	private void connect(String server, int port) throws IOException {
		Socket s = new Socket(server, port);
		OutputStream tmp = s.getOutputStream();
		BufferedOutputStream bufout = new BufferedOutputStream(tmp);

		out = new PrintWriter(bufout);
	}

	@Override
	public void run() {
		int x;
		int y;

		System.out.println("thread: " + from + " " + fromx);

		while (true) {
			for (y = from; y < to; y++) {
				for (x = fromx; x < tox; x++) {
					try {
						out.write("PX " + x + " " + y + " " + rgbToHex(img.getRGB(x, y)) + "\n");
					} catch (Exception e) {}
				}
			}
		}
	}
}


public class papier {
	public static void main(String[] args) throws InterruptedException, IOException {
		if (args.length != 4) {
			System.out.println("Usage: " + "papier <server> <port> <image> <threads>");
			System.exit(1);
		}

		String server = args[0];
		int port = Integer.valueOf(args[1]);
		BufferedImage image = ImageIO.read(new File(args[2]));
		int height = image.getHeight();
		int width = image.getWidth();
		int threads = Integer.valueOf(args[3]);

		Thread t[] = new Thread[threads];

		int sqrtthreads = (int) Math.sqrt(threads);
		int zoney = height/sqrtthreads;
		int zonex = width/sqrtthreads;

		for (int i = 0; i < sqrtthreads; i++) {
			for (int j = 0; j < sqrtthreads; j++) {
				System.out.println("Starting Writer. from (" + j * zonex + "," + i * zoney + ") to (" + (j+1) * zonex + "," + (i+1)*zoney + ")");
				ImageWriter writer = new ImageWriter(server, port, image, i*zoney, (i+1)*zoney, j*zonex, (j+1)*zonex);
				t[i+j] = new Thread(writer);
				t[i+j].start();
			}
		}

		for (int i = 0; i < threads; i++) {
			t[i].join();
		}
	}
}

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

	private Writer out;

	private Random rand;

	ImageWriter(String server, int port, BufferedImage img) {
		try {
			connect(server, port);
		} catch (Exception e) {
			System.out.println(e);
		}

		this.img = img;
		this.width = img.getWidth();
		this.height = img.getHeight();

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

		while (true) {
			x = rand.nextInt(Integer.MAX_VALUE) % width;
			y = rand.nextInt(Integer.MAX_VALUE) % height;

			try {
				out.write("PX " + x + " " + y + " " + rgbToHex(img.getRGB(x, y)) + "\n");
			} catch (Exception e) {}
		}
	}
}


public class PifPE {
	public static void main(String[] args) throws InterruptedException, IOException {
		if (args.length != 4) {
			System.out.println("Usage: " + "PifPE <server> <port> <image> <threads>");
			System.exit(1);
		}

		String server = args[0];
		int port = Integer.valueOf(args[1]);
		BufferedImage image = ImageIO.read(new File(args[2]));
		int threads = Integer.valueOf(args[3]);

		Thread t[] = new Thread[threads];

		for (int i = 0; i < threads; i++) {
			ImageWriter writer = new ImageWriter(server, port, image);
			t[i] = new Thread(writer);
			t[i].start();
		}

		for (int i = 0; i < threads; i++) {
			t[i].join();
		}
	}
}
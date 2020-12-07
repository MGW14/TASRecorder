package work.mgnet.tasrecorder.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.filechooser.FileSystemView;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import work.mgnet.tasrecorder.ScreenshotQueue;
import work.mgnet.tasrecorder.ScreenshotQueue.WorkImage;
import work.mgnet.tasrecorder.TASRecorder;

public class ScreenshotUtils {

	public static final File screenshotDir = new File(
			FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/ffmpeg");

	public static int width;
	public static int height;

	// Colors of the Image. 3 = RGB, 4 = RGBA
	public static int bpp = 4;
	// Colors of the Image 2. GL_RGB, GL_RGBA
	public static int glbpp = GL11.GL_RGBA;

	public static String getScreenshotName() {
		TASRecorder.currentFrame++;
		int t = TASRecorder.currentFrame;
		String tInStr = t + "";

		int toAdd = 6 - tInStr.length();
		String finalStr = "";

		for (int i = 0; i < toAdd; i++) {
			finalStr = finalStr + "0";
		}

		finalStr = finalStr + t;

		String name = "img" + finalStr + ".uncompressed";
		return name;
	}

	public static ByteBuffer takeScreenshot() {
		if (width == 0 || height == 0) {
			width = Display.getWidth();
			height = Display.getHeight();
		}

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, glbpp, GL11.GL_UNSIGNED_BYTE, buffer);
		return buffer;
	}

	public static void saveScreenshot(WorkImage img) throws IOException {
		ByteBuffer buffer = img.buffer;
		File file = new File(screenshotDir, img.name);
		FileOutputStream oos = new FileOutputStream(file);
		FileChannel fc = oos.getChannel();
		
		fc.write(buffer);
		
		fc.close();
		oos.close();
	
		synchronized (ScreenshotQueue.toCompress) {
			ScreenshotQueue.toCompress.add(img.name);
		}
	}
	
	public static BufferedImage compressScreenshot(ByteBuffer buffer) {
		BufferedImage image = new BufferedImage(ScreenshotUtils.width, ScreenshotUtils.height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < ScreenshotUtils.width; x++) {
			for (int y = 0; y < ScreenshotUtils.height; y++) {
				int i = (x + (ScreenshotUtils.width * y)) * ScreenshotUtils.bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, ScreenshotUtils.height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return image;
	}

}

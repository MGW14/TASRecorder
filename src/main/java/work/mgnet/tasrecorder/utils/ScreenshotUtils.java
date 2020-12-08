package work.mgnet.tasrecorder.utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.filechooser.FileSystemView;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import work.mgnet.tasrecorder.ScreenshotQueue;
import work.mgnet.tasrecorder.ScreenshotQueue.WorkImage;

public class ScreenshotUtils {

	public static final File screenshotDir = new File(
			FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/ffmpeg");

	public static int width;
	public static int height;

	public static int bpp = 3;
	public static int glbpp = GL11.GL_RGB;

	public static ByteBuffer takeScreenshot() {
		if (width == 0 || height == 0) {
			width = Display.getWidth();
			height = Display.getHeight();
		}

		ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, glbpp, GL11.GL_UNSIGNED_BYTE, buffer);
		return buffer;
	}

	public static void saveScreenshot(WorkImage img) throws IOException {
		if (ScreenshotQueue.toConvert.size() != 0) System.out.println("Pending: " + ScreenshotQueue.toConvert.size());
		Picture pic = Picture.create(1920, 1080, ColorSpace.RGB);
		
		byte[] dstData = pic.getPlaneData(0);
		
		ByteBuffer buffer = img.buffer;

		
        int j = 0;
        for (int y = 1079; y > 0; y--) {
            for (int x = 0; x < 1920; x++) {
                int i = (x + (1920 * y)) * 3;
                
                dstData[j++] = 
                		(byte) (buffer.get(i) - 128);
                dstData[j++] = 
                        (byte) (buffer.get(i + 1) - 128);
                dstData[j++] = 
                        (byte) (buffer.get(i + 2) - 128);
                
            }
        }
        
        try {
        	ScreenshotQueue.encoder.encodeNativeFrame(pic);
        } catch (Exception e) {
			
		}
        
	}

}

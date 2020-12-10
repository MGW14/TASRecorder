package work.mgnet.tasrecorder.utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class ScreenshotUtils {

	public static final File videosFolder = new File(System.getenv("userprofile"), "Videos");

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

	public static Picture saveScreenshot(ByteBuffer buffer) throws IOException {
		Picture pic = Picture.create(width, height, ColorSpace.RGB);
		
		byte[] dstData = pic.getPlaneData(0);
		
        int j = 0;
        for (int y = (height - 1); y > 0; y--) {
            for (int x = 0; x < width; x++) {
                int i = (x + (width * y)) * 3;
                
                dstData[j++] = 
                		(byte) (buffer.get(i) - 128);
                dstData[j++] = 
                        (byte) (buffer.get(i + 1) - 128);
                dstData[j++] = 
                        (byte) (buffer.get(i + 2) - 128);
                
            }
        }
        return pic;
	}

}

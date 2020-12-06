package work.mgnet.tasrecorder.ffmpeg;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import org.lwjgl.opengl.Display;

import com.google.common.collect.ImmutableList;

import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class FFMPEGBuilder {

	public static final String ffmpegDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/ffmpeg/bin";
	public static final int quality = 10;
	public static final int framerate = 30;
	
	public static void build() {
		
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		
		int frames = 0;
		for (File file : ScreenshotUtils.screenshotDir.listFiles()) {
			if (file.getName().startsWith("img")) frames++;
		}
		
		FFMPEGHelper ffmpeg = new FFMPEGHelper(FFMPEGBuilder.ffmpegDir);
		ffmpeg.setArgument("-r", framerate + "");
		ffmpeg.setArgument("-f", "image2");
		ffmpeg.setArgument("-s", width + "x" + height);
		ffmpeg.setArgument("-i", "../img%06d.jpg");
		ffmpeg.setArgument("-vcodec", "libx264");
		ffmpeg.setArgument("-crf", quality + "");
		ffmpeg.setArgument("-pix_fmt", "yuv420p");
		ffmpeg.setArgument("-vframes", frames + "");
		ffmpeg.setArgument("-start_number", "1");
		ffmpeg.addAllArguments(ImmutableList.of("../output.mp4"));
		
		try {
			ffmpeg.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

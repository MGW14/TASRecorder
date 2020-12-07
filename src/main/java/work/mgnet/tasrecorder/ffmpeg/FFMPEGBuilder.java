package work.mgnet.tasrecorder.ffmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.filechooser.FileSystemView;

import org.lwjgl.opengl.Display;

import com.google.common.collect.ImmutableList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketTitle.Type;
import net.minecraft.util.text.TextComponentString;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class FFMPEGBuilder {

	public static final String ffmpegDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/ffmpeg/bin";
	public static int quality = 5;
	public static final int framerate = 60;
	
	public static void build(ICommandSender sender) {
		
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		
		int frames = 0;
		for (File file : ScreenshotUtils.screenshotDir.listFiles()) {
			if (file.getName().startsWith("img")) frames++;
			if (file.getName().startsWith("output.mp4")) file.delete();
		}
		
		final int exactFrames = frames;
		
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
			Process p = ffmpeg.run();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						BufferedReader out = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String line = out.readLine();
						System.out.println("READING");
						while (line != null) {
							line = out.readLine();
							try {
								if (line.trim().startsWith("frame= ")) {
								int currentFrame = Integer.parseInt(line.trim().replaceAll("   ", " ").replaceAll("  ", " ").split(" ")[1]);
								float percentage = (((float) currentFrame) / ((float) exactFrames)) * ((float) 100);
								
								
								SPacketTitle packet = new SPacketTitle(Type.TITLE, new TextComponentString(String.format("%.02f", percentage) + "% exported"), 0, 80, 0);
								SPacketTitle packet2 = new SPacketTitle(Type.SUBTITLE, new TextComponentString(currentFrame + "/" + exactFrames + " Frames"), 0, 80, 0);
								SPacketTitle times = new SPacketTitle(0, 80, 0);
								if (sender instanceof EntityPlayerMP) {
									((EntityPlayerMP) sender).connection.sendPacket(packet);
									((EntityPlayerMP) sender).connection.sendPacket(packet2);
									((EntityPlayerMP) sender).connection.sendPacket(times);
								}
							}} catch (Exception e) {
								
							}
						}
						SPacketTitle packet = new SPacketTitle(Type.TITLE, new TextComponentString("Done!"), 1, 5, 1);
						if (sender instanceof EntityPlayerMP) ((EntityPlayerMP) sender).connection.sendPacket(packet);
					} catch (Exception e) {

					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

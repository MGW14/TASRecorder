package work.mgnet.tasrecorder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;

import com.google.common.collect.ImmutableList;

import me.guichaguri.tastickratechanger.api.TickrateAPI;
import net.minecraft.client.Minecraft;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class ScreenshotQueue {
	
	public static Timer scheduler = new Timer();
	public static TimerTask workerTask;
	
	public static boolean isRecording = false;
	public static boolean freeMemory = false;
	
	public static Thread workedThread;
	
	public static Queue<ByteBuffer> toConvert = new LinkedList<ByteBuffer>();
	public static int toRecord = 0;
	
	private static final List<String> allowed = ImmutableList.of("guichest", "guibeacon", "guibrewingstand", "guichat", "guicommandblock", "guidispenser",
			"guienchantment", "guifurnace", "guihopper", "guiinventory", "guirecipebook", "guirecipeoverlay", "guimerchant", "guicontainercreative", "guishulkerbox", "guirepair", "guicrafting");
	
	public static void endRecording() {
		isRecording = false;
		TickrateAPI.changeTickrate(20.0f);
	}
	
	public static void startRecording() {
		if (!ScreenshotUtils.videosFolder.exists()) ScreenshotUtils.videosFolder.mkdir();
		
		isRecording = true;
		
		TickrateAPI.changeTickrate(1.0f);
				
		ScreenshotQueue.scheduler = new Timer();
		ScreenshotQueue.workedThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				File movie = new File(ScreenshotUtils.videosFolder, "TAS" + new SimpleDateFormat(" MM.dd.yyyy HH.mm.ss").format(new Date()) + ".mp4");
				try {
					movie.createNewFile();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				SequenceEncoder encoder = null;
				try {
					encoder = new SequenceEncoder(NIOUtils.writableFileChannel(movie.getAbsolutePath()), Rational.R(60, 1), Format.MOV, Codec.H264, null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				while (isRecording) {
					synchronized (ScreenshotQueue.toConvert) {
						if (ScreenshotQueue.toConvert.size() != 0) {
							ByteBuffer buffer = ScreenshotQueue.toConvert.poll();
							try {
								Picture pic = ScreenshotUtils.saveScreenshot(buffer);
								encoder.encodeNativeFrame(pic);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				try {
					encoder.finish();
				} catch (IOException e) {
					e.printStackTrace();
				}
				freeMemory = true;
			}
		});
		ScreenshotQueue.workedThread.start();
		ScreenshotQueue.workerTask = new TimerTask() {
			
			@Override
			public void run() {
				
				if (Minecraft.getMinecraft().currentScreen == null) {
					ScreenshotQueue.toRecord++;
				} else if (allowed.contains(Minecraft.getMinecraft().currentScreen.getClass().getSimpleName().toLowerCase())) {
					ScreenshotQueue.toRecord++;
				}
			}
		};
		ScreenshotQueue.scheduler.scheduleAtFixedRate(ScreenshotQueue.workerTask, 16L, Math.round(1000 / 3));
	}
	
}

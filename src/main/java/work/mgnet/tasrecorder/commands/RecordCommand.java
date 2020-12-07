package work.mgnet.tasrecorder.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.google.common.collect.ImmutableList;

import me.guichaguri.tastickratechanger.TickrateChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import work.mgnet.tasrecorder.ScreenshotQueue;
import work.mgnet.tasrecorder.ScreenshotQueue.WorkImage;
import work.mgnet.tasrecorder.TASRecorder;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class RecordCommand extends CommandBase {

	private static final List<String> allowed = ImmutableList.of("guichest", "guibeacon", "guibrewingstand", "guichat", "guicommandblock", "guidispenser",
			"guienchantment", "guifurnace", "guihopper", "guiinventory", "guirecipebook", "guirecipeoverlay", "guimerchant", "guicontainercreative", "guishulkerbox", "guirepair", "guicrafting");

	@Override
	public String getName() {
		return "record";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/record";
	}

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("record");
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	return null;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (TASRecorder.isRecording) {
			TASRecorder.isRecording = false;
			ScreenshotQueue.workerThread.interrupt();
			ScreenshotQueue.scheduler.cancel();
			sender.sendMessage(new TextComponentString("You have stopped the recording"));
		} else {
			if (ScreenshotUtils.screenshotDir.exists()) {
				for (File file : ScreenshotUtils.screenshotDir.listFiles()) {
					file.delete();
				}
			} else sender.sendMessage(new TextComponentString("You haven't installed FFMpeg yet"));
			ScreenshotQueue.workerThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						synchronized (ScreenshotQueue.toConvert) {
							if (!ScreenshotQueue.toConvert.isEmpty()) {
								WorkImage img = ScreenshotQueue.toConvert.poll();
								try {
									ScreenshotUtils.saveScreenshot(img);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
			ScreenshotQueue.compressThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						synchronized (ScreenshotQueue.toCompress) {
							boolean afk = false;
							try {
								afk = !allowed.contains(Minecraft.getMinecraft().currentScreen.getClass().getSimpleName().toLowerCase());
							} catch (Exception e) {
								
							}
							while (afk && ScreenshotQueue.toCompress.size() != 0) {
								
								int max = 0;
								List<Thread> threads = new ArrayList<>();
								for (int i = 0; i < ScreenshotQueue.toCompress.size(); i++) {
									if (max > 60) break; 
									final String file = ScreenshotQueue.toCompress.poll();
									Thread t = new Thread(new Runnable() {
										
										@Override
										public void run() {
											try {
												File uncompressedFile = new File(ScreenshotUtils.screenshotDir, file);
												File jpgFile = new File(ScreenshotUtils.screenshotDir, file.replaceFirst("uncompressed", "jpg"));
												
												FileInputStream fs = new FileInputStream(uncompressedFile);
												FileChannel fc = fs.getChannel();
												
												ByteBuffer buffer = BufferUtils.createByteBuffer(ScreenshotUtils.width * ScreenshotUtils.height * ScreenshotUtils.bpp);
												fc.read(buffer);
												
												ImageIO.write(ScreenshotUtils.compressScreenshot(buffer), "JPG", jpgFile);
												
												fs.close();
												fc.close();
												
												uncompressedFile.delete();
												
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									t.start();
									threads.add(t);
									
									max++;
								}
								
								while (threads.size() != 0) {
									for (Thread th : new ArrayList<Thread>(threads)) {
										if (!th.isAlive()) {
											threads.remove(th);
											break;
										}
									}
								}
							}
						}
					}
				}
			});
			ScreenshotQueue.workerThread.start();
			ScreenshotQueue.compressThread.start();
			ScreenshotQueue.scheduler = new Timer();
			ScreenshotQueue.workerTask = new TimerTask() {
				
				@Override
				public void run() {
					
					if (Minecraft.getMinecraft().currentScreen == null) ScreenshotQueue.toRecord.add(ScreenshotUtils.getScreenshotName());
					else if (allowed.contains(Minecraft.getMinecraft().currentScreen.getClass().getSimpleName().toLowerCase())) ScreenshotQueue.toRecord.add(ScreenshotUtils.getScreenshotName());
				}
			};
			ScreenshotQueue.scheduler.scheduleAtFixedRate(ScreenshotQueue.workerTask, 0L, Math.round(1000 / (TickrateChanger.TICKS_PER_SECOND * 3)));
			
			TASRecorder.currentFrame = 0;
			
			TASRecorder.isRecording = true;
			sender.sendMessage(new TextComponentString("You have started the Recording"));
		
		}
	}
	
}

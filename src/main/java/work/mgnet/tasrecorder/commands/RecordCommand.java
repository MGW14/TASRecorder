package work.mgnet.tasrecorder.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

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
			ScreenshotQueue.workedThread.interrupt();
			ScreenshotQueue.scheduler.cancel();
			try {
				ScreenshotQueue.encoder.finish();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			sender.sendMessage(new TextComponentString("You have stopped the recording"));
		} else {
			if (ScreenshotUtils.screenshotDir.exists()) {
				for (File file : ScreenshotUtils.screenshotDir.listFiles()) {
					file.delete();
				}
			} else sender.sendMessage(new TextComponentString("You haven't installed FFmpeg yet"));
			try {
				ScreenshotQueue.encoder = new SequenceEncoder(NIOUtils.writableChannel(new File(ScreenshotUtils.screenshotDir, "output.mp4")), Rational.R(60, 1), Format.MOV, Codec.H264, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ScreenshotQueue.scheduler = new Timer();
			ScreenshotQueue.workedThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						synchronized (ScreenshotQueue.toConvert) {
							if (ScreenshotQueue.toConvert.size() != 0) {
								final WorkImage job = ScreenshotQueue.toConvert.poll();
								try {
									ScreenshotUtils.saveScreenshot(job);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
			ScreenshotQueue.workedThread.start();
			ScreenshotQueue.workerTask = new TimerTask() {
				
				@Override
				public void run() {
					
					if (Minecraft.getMinecraft().currentScreen == null) {
						ScreenshotQueue.toRecord.add(TASRecorder.currentFrame);
						TASRecorder.currentFrame++;
					}
					else if (allowed.contains(Minecraft.getMinecraft().currentScreen.getClass().getSimpleName().toLowerCase())) {
						ScreenshotQueue.toRecord.add(TASRecorder.currentFrame);
						TASRecorder.currentFrame++;
					}
				}
			};
			ScreenshotQueue.scheduler.scheduleAtFixedRate(ScreenshotQueue.workerTask, 0L, Math.round(1000 / (TickrateChanger.TICKS_PER_SECOND * 3)));
			
			TASRecorder.currentFrame = 0;
			
			TASRecorder.isRecording = true;
			sender.sendMessage(new TextComponentString("You have started the Recording"));
		
		}
	}
	
}

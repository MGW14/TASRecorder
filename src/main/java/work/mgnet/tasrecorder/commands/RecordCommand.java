package work.mgnet.tasrecorder.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

import com.google.common.collect.ImmutableList;

import me.guichaguri.tastickratechanger.api.TickrateAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import work.mgnet.tasrecorder.ScreenshotQueue;
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
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ScreenshotQueue.scheduler.cancel();
					while (!ScreenshotQueue.toConvert.isEmpty() || ScreenshotQueue.toRecord != 0) {
						
					}
					ScreenshotQueue.workedThread.interrupt();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						ScreenshotQueue.encoder.finish();
						sender.sendMessage(new TextComponentString("Your file has been successfully saved. Please restart your MC if you want to make another recording. (dunno why but that's just how it is)"));
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage(new TextComponentString("§cError. This is why you don't use pre-releases. :( i dunno myself why this error occurs."));
					}
					ScreenshotQueue.encoder = null;
					ScreenshotQueue.scheduler = null;
					ScreenshotQueue.toConvert = new LinkedList<ByteBuffer>();
					ScreenshotQueue.toRecord = 0;
					ScreenshotQueue.workedThread = null;
					ScreenshotQueue.workerTask = null;
				}
			}).start();
			sender.sendMessage(new TextComponentString("You have stopped the recording. Your Video is saved under Videos"));
			TickrateAPI.changeTickrate(20.0f);
		} else {
			File movie = new File(ScreenshotUtils.videosFolder, "TAS-Exported.mp4");
			if (ScreenshotUtils.videosFolder.exists()) {
				if (movie.exists()) {
					movie.delete();
					sender.sendMessage(new TextComponentString("Your old TAS has been deleted. Please rerun the command"));
					return;
				}
			} else ScreenshotUtils.videosFolder.mkdir();
			TickrateAPI.changeTickrate(1.0f);
			try {
				ScreenshotQueue.encoder = new SequenceEncoder(NIOUtils.writableChannel(movie), Rational.R(60, 1), Format.MOV, Codec.H264, null);
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
								ByteBuffer buffer = ScreenshotQueue.toConvert.poll();
								try {
									ScreenshotUtils.saveScreenshot(buffer);
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
						ScreenshotQueue.toRecord++;
					} else if (allowed.contains(Minecraft.getMinecraft().currentScreen.getClass().getSimpleName().toLowerCase())) {
						ScreenshotQueue.toRecord++;
					}
				}
			};
			ScreenshotQueue.scheduler.scheduleAtFixedRate(ScreenshotQueue.workerTask, 16L, Math.round(1000 / 3));
			
			TASRecorder.isRecording = true;
			sender.sendMessage(new TextComponentString("You have started the Recording"));
		
		}
	}
	
}

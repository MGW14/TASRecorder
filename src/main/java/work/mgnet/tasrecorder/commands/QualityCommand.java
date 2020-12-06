package work.mgnet.tasrecorder.commands;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import work.mgnet.tasrecorder.TASRecorder;
import work.mgnet.tasrecorder.ffmpeg.FFMPEGBuilder;
import work.mgnet.tasrecorder.utils.ConfigHandler;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class QualityCommand extends CommandBase {

	@Override
	public String getName() {
		return "quality";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/quality";
	}

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("quality");
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	return null;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (TASRecorder.isRecording) {
			sender.sendMessage(new TextComponentString("You are recording!"));
		} else {
			if (args.length == 1) {
				switch (args[0]) {
				case "low":
					ScreenshotUtils.bpp = 4;
					ScreenshotUtils.glbpp = GL11.GL_RGBA;
					FFMPEGBuilder.quality = 30;
					ConfigHandler.writeConfig("main", "quality", 0);
					break;
				case "medium":
					ScreenshotUtils.bpp = 4;
					ScreenshotUtils.glbpp = GL11.GL_RGBA;
					FFMPEGBuilder.quality = 25;
					ConfigHandler.writeConfig("main", "quality", 1);
					break;
				case "high":
					ScreenshotUtils.bpp = 4;
					ScreenshotUtils.glbpp = GL11.GL_RGBA;
					FFMPEGBuilder.quality = 15;
					ConfigHandler.writeConfig("main", "quality", 2);
					break;
				case "ultra":
					ScreenshotUtils.bpp = 4;
					ScreenshotUtils.glbpp = GL11.GL_RGBA;
					FFMPEGBuilder.quality = 5;
					ConfigHandler.writeConfig("main", "quality", 3);
					break;
				default:
					sender.sendMessage(new TextComponentString("Choose: low, medium, high, ultra"));
					break;
				}
			} else {
				sender.sendMessage(new TextComponentString("Choose: low, medium, high, ultra"));
			}
		}
	}
	
}

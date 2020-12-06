package work.mgnet.tasrecorder.commands;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import work.mgnet.tasrecorder.TASRecorder;
import work.mgnet.tasrecorder.ffmpeg.FFMPEGBuilder;

public class GenerateCommand extends CommandBase {

	@Override
	public String getName() {
		return "generate";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/generate";
	}

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("generate");
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	return null;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (TASRecorder.isRecording) {
			sender.sendMessage(new TextComponentString("You are recording"));
		} else {
			FFMPEGBuilder.build(sender);
		}
	}
	
}

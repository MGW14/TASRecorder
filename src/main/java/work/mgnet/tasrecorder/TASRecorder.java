package work.mgnet.tasrecorder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import work.mgnet.tasrecorder.commands.GenerateCommand;
import work.mgnet.tasrecorder.commands.RecordCommand;

@Mod(modid = TASRecorder.MODID, name = TASRecorder.NAME, version = TASRecorder.VERSION)
public class TASRecorder {
	
	public static final String MODID = "tasrecorder";
	public static final String NAME = "TASRecorder";
	public static final String VERSION = "1.0";
	
	public static int currentFrame;
	public static boolean isRecording = false;
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
	
	@EventHandler
	public void start(FMLServerStartingEvent event) {
		event.registerServerCommand(new RecordCommand());
		event.registerServerCommand(new GenerateCommand());
	}
}

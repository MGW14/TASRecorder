package work.mgnet.tasrecorder;

import de.scribble.lp.TASTools.ClientProxy;
import de.scribble.lp.TASTools.ModLoader;
import de.scribble.lp.TASTools.savestates.SavestatePacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import work.mgnet.tasrecorder.commands.RecordCommand;

@Mod(modid = TASRecorder.MODID, name = TASRecorder.NAME, version = TASRecorder.VERSION)
public class TASRecorder {
	
	public static final String MODID = "tasrecorder";
	public static final String NAME = "TASRecorder";
	public static final String VERSION = "1.4";
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@EventHandler
	public void start(FMLServerStartingEvent event) {
		event.registerServerCommand(new RecordCommand());
	}
	
	@SubscribeEvent
	public void keybind(InputEvent.KeyInputEvent event) {
		if (ClientProxy.SavestateSaveKey.isPressed()) {
			ScreenshotQueue.shouldRestart = true;
			ScreenshotQueue.isRecording = false;
			ModLoader.NETWORK.sendToServer(new SavestatePacket(true));
		}
		if (ClientProxy.SavestateLoadKey.isPressed()) {
			ScreenshotQueue.shouldRestart = true;
			ScreenshotQueue.isRecording = false;
			ModLoader.NETWORK.sendToServer(new SavestatePacket(false));
		}
	}
	
}

package work.mgnet.tasrecorder;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import work.mgnet.tasrecorder.commands.GenerateCommand;
import work.mgnet.tasrecorder.commands.QualityCommand;
import work.mgnet.tasrecorder.commands.RecordCommand;
import work.mgnet.tasrecorder.ffmpeg.FFMPEGBuilder;
import work.mgnet.tasrecorder.utils.ConfigHandler;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

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
        ConfigHandler.init();
        if (ConfigHandler.hasKey("main", "quality")) {
        	switch (ConfigHandler.getInt("main", "quality")) {
			case 0:
				ScreenshotUtils.bpp = 4;
				ScreenshotUtils.glbpp = GL11.GL_RGBA;
				FFMPEGBuilder.quality = 30;
				break;
			case 1:
				ScreenshotUtils.bpp = 4;
				ScreenshotUtils.glbpp = GL11.GL_RGBA;
				FFMPEGBuilder.quality = 25;
				break;
			case 2:
				ScreenshotUtils.bpp = 4;
				ScreenshotUtils.glbpp = GL11.GL_RGBA;
				FFMPEGBuilder.quality = 15;
				break;
			case 3:
				ScreenshotUtils.bpp = 4;
				ScreenshotUtils.glbpp = GL11.GL_RGBA;
				FFMPEGBuilder.quality = 5;
				break;
			}
        }
    }
	
	@EventHandler
	public void start(FMLServerStartingEvent event) {
		event.registerServerCommand(new RecordCommand());
		event.registerServerCommand(new GenerateCommand());
		event.registerServerCommand(new QualityCommand());
	}
}

package work.mgnet.tasrecorder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import work.mgnet.tasrecorder.ScreenshotQueue;
import work.mgnet.tasrecorder.TASRecorder;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu {
	
	@Inject(method = "drawScreen", at = @At(value="TAIL"), cancellable = true)
	public void redodrawScreen(CallbackInfo ci) {
		Minecraft.getMinecraft().fontRenderer.drawString("Saved Uncompressed: " + ScreenshotQueue.toCompress.size(), 150, 50, 0xFFFFFF);
		Minecraft.getMinecraft().fontRenderer.drawString("Saved Compressed: " + (TASRecorder.currentFrame - ScreenshotQueue.toCompress.size()), 150, 60, 0xFFFFFF);
	}
	
}

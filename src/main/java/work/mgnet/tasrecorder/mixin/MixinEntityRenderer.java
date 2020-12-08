package work.mgnet.tasrecorder.mixin;

import java.nio.ByteBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.EntityRenderer;
import work.mgnet.tasrecorder.ScreenshotQueue;
import work.mgnet.tasrecorder.utils.ScreenshotUtils;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	
	@Inject(method = "updateCameraAndRender", at = @At(value="TAIL"), cancellable = true)
	public void redoupdateCameraAndRender(CallbackInfo ci) {
		if (ScreenshotQueue.toRecord > 0) {
			ScreenshotQueue.toRecord--;
			ByteBuffer bytes = ScreenshotUtils.takeScreenshot();
			ScreenshotQueue.toConvert.add(bytes);
		}
	 }
	
}

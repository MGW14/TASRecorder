package work.mgnet.tasrecorder.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentString;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Inject(method = "displayGuiScreen", at = @At(value="HEAD"), cancellable = true)
	public void redodisplayGuiScreen(@Nullable GuiScreen guiScreenIn, CallbackInfo ci) {
			if (guiScreenIn instanceof GuiMemoryErrorScreen) {
				guiScreenIn = new GuiIngameMenu();
			}
	 }
	
	@Inject(method = "runTick", at = @At(value="HEAD"), cancellable = true)
	public void redorunTick(CallbackInfo ci) {
		if (Runtime.getRuntime().freeMemory() < 150000000) {
			Minecraft.memoryReserve = new byte[0];
			GuiIngameMenu menu = new GuiIngameMenu();
			Minecraft.getMinecraft().displayGuiScreen(menu);
			menu.drawString(Minecraft.getMinecraft().fontRenderer, "Memory Garbage was collected", 10, 10, 0xFFFFFF);
			Minecraft.getMinecraft().renderGlobal.deleteAllDisplayLists();
			System.gc();
		} else if (Runtime.getRuntime().freeMemory() > (160000000 + 10485760) && Minecraft.memoryReserve.length == 0) {
			Minecraft.memoryReserve = new byte[10485760];
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("[DEBUG] Memory was saved"));
		}
	}
	
}

package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.gui.GuiDownloadTerrain;

@Mixin(GuiDownloadTerrain.class)
public class MixinGuiDownloadTerrain {

	@Inject(method = "updateScreen", at = @At("HEAD"), cancellable = true)
	public void onUpdateScreen(CallbackInfo ci) {
		if (MultiConnectHelper.getProtocolVersion() >= PacketLists.PROTOCOL_1_12_2)
			ci.cancel();
	}

}

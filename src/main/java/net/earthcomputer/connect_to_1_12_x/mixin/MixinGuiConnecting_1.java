package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.earthcomputer.connect_to_1_12_x.Config;
import net.earthcomputer.connect_to_1_12_x.PacketLists;

@Mixin(targets = "net.minecraft.client.multiplayer.GuiConnecting$1")
public class MixinGuiConnecting_1 {

	@ModifyConstant(method = "run()V", constant = @Constant(intValue = PacketLists.PROTOCOL_1_12))
	public int modifyDefaultProtocol(int oldVersion) {
		int newProtocol = Config.DEFAULT_PROTOCOL_VERSION.getIntegerValue();
		if (PacketLists.isProtocolSupported(newProtocol))
			return newProtocol;
		else
			return oldVersion;
	}

}

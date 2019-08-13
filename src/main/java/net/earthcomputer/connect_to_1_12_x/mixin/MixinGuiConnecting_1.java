package net.earthcomputer.connect_to_1_12_x.mixin;

import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.earthcomputer.connect_to_1_12_x.PacketLists;

@Mixin(targets = "net.minecraft.client.multiplayer.GuiConnecting$1")
public class MixinGuiConnecting_1 {

	@ModifyConstant(method = "run()V", constant = @Constant(intValue = PacketLists.PROTOCOL_1_12))
	public int modifyHandshakeProtocol(int oldVersion) {
		int newProtocol = MultiConnectHelper.getProtocolVersion();
		if (PacketLists.isProtocolSupported(newProtocol))
			return newProtocol;
		else
			return oldVersion;
	}

}

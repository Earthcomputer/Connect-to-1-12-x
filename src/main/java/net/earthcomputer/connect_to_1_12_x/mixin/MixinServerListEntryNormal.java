package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;

@Mixin(ServerListEntryNormal.class)
public class MixinServerListEntryNormal {

	@Shadow
	@Final
	private ServerData server;

	@ModifyConstant(method = "drawEntry", constant = @Constant(intValue = PacketLists.PROTOCOL_1_12_2), expect = 2)
	public int modifyProtocolVersion(int oldVersion) {
		if (PacketLists.isProtocolSupported(server.version))
			return server.version;
		else
			return oldVersion;
	}

}

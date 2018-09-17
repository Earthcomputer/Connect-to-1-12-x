package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.netty.channel.Channel;
import net.earthcomputer.connect_to_1_12_x.INetworkManager;
import net.minecraft.network.NetworkManager;

@Mixin(NetworkManager.class)
public class MixinNetworkManager implements INetworkManager {

	@Shadow
	private Channel channel;

	@Override
	public void removeChannel() {
		channel = null;
	}
	
}

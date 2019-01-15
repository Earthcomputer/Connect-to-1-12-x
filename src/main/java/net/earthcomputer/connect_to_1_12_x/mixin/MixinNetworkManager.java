package net.earthcomputer.connect_to_1_12_x.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.netty.channel.Channel;
import net.earthcomputer.connect_to_1_12_x.INetworkManager;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager implements INetworkManager {

	@Shadow
	private Channel channel;

	@Override
	public void removeChannel() {
		channel = null;
	}

	// Some null checks for safety

	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	private void channelRead0Check(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		if (channel == null)
			ci.cancel();
	}

	@Inject(method = "closeChannel", at = @At("HEAD"), cancellable = true)
	private void closeChannelCheck(ITextComponent message, CallbackInfo ci) {
		if (channel == null)
			ci.cancel();
	}
	
}

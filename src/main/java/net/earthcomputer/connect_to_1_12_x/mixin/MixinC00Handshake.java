package net.earthcomputer.connect_to_1_12_x.mixin;

import net.earthcomputer.connect_to_1_12_x.PacketLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;

@Mixin(C00Handshake.class)
public class MixinC00Handshake {

	@Shadow private int protocolVersion;

	@Inject(method = "Lnet/minecraft/network/handshake/client/C00Handshake;<init>(Ljava/lang/String;ILnet/minecraft/network/EnumConnectionState;)V", at = @At("RETURN"))
	public void onConstructor(String host, int port, EnumConnectionState nextState, CallbackInfo ci) {
		int version = MultiConnectHelper.getProtocolVersion();
		if (PacketLists.isProtocolSupported(version)) {
			protocolVersion = version;
			MultiConnectHelper.onHandshakeSent(version, host, port, nextState);
		} else {
			MultiConnectHelper.onHandshakeSent(PacketLists.PROTOCOL_1_12_2, host, port, nextState);
		}
	}
}

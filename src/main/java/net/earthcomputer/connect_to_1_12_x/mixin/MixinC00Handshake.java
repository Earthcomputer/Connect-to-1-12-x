package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;

@Mixin(C00Handshake.class)
public class MixinC00Handshake {

	@Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/EnumConnectionState;)V", at = @At("RETURN"))
	public void onConstructor(int version, String host, int port, EnumConnectionState nextState, CallbackInfo ci) {
		MultiConnectHelper.onHandshakeSent(version, host, port, nextState);
	}

}

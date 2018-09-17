package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.earthcomputer.connect_to_1_12_x.INetworkManager;
import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.util.text.ITextComponent;

@Mixin(NetHandlerLoginClient.class)
public class MixinNetHandlerLoginClient {

	@Shadow
	@Final
	private NetworkManager networkManager;
	
	@Inject(method = "onDisconnect", at = @At("HEAD"), cancellable = true)
	public void onOnDisconnect(ITextComponent reason, CallbackInfo ci) {
		if (MultiConnectHelper.isConnecting()) {
			MultiConnectHelper.tryNextVersion();
			ci.cancel();
			((INetworkManager) networkManager).removeChannel();
		}
	}

	@Inject(method = "handleEncryptionRequest", at = @At("HEAD"))
	public void onHandleEncryptionRequest(SPacketEncryptionRequest packet, CallbackInfo ci) {
		MultiConnectHelper.onConnectSuccess();
	}

	@Inject(method = "handleLoginSuccess", at = @At("HEAD"))
	public void onHandleLoginSuccess(SPacketLoginSuccess packet, CallbackInfo ci) {
		MultiConnectHelper.onConnectSuccess();
	}

}

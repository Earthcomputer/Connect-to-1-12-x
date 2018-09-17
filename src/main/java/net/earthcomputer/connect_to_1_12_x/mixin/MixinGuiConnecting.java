package net.earthcomputer.connect_to_1_12_x.mixin;

import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.earthcomputer.connect_to_1_12_x.IServerConnector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;

@Mixin(GuiConnecting.class)
public class MixinGuiConnecting implements IServerConnector {

	@Shadow
	private NetworkManager networkManager;

	@Shadow
	@Final
	private GuiScreen previousGuiScreen;

	@Shadow
	@Final
	private static AtomicInteger CONNECTION_ID;

	@Shadow
	private boolean cancel;

	private boolean showProtocolVersion;
	private int shownProtocolVersion;

	@Override
	public void setConnection(NetworkManager connection) {
		this.networkManager = connection;
	}

	@Override
	public NetworkManager getConnection() {
		return networkManager;
	}

	@Override
	public GuiScreen getPreviousScreen() {
		return previousGuiScreen;
	}

	@Override
	public String nextConnectThreadName() {
		return "Server Connector #" + CONNECTION_ID.incrementAndGet();
	}

	@Override
	public boolean isCanceled() {
		return cancel;
	}

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"), expect = 2)
	public String redirectMessage(String translationKey, Object... params) {
		if (!showProtocolVersion)
			return I18n.format(translationKey, params);
		else
			return I18n.format("multi." + translationKey, shownProtocolVersion);
	}

	@Override
	public void setShowProtocolVersion(boolean showProtocolVersion) {
		this.showProtocolVersion = showProtocolVersion;
	}

	@Override
	public void setShownProtocolVersion(int protocolVersion) {
		this.shownProtocolVersion = protocolVersion;
	}

}

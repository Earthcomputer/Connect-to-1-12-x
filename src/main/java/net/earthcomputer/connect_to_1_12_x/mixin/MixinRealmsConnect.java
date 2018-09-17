package net.earthcomputer.connect_to_1_12_x.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.earthcomputer.connect_to_1_12_x.IServerConnector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetworkManager;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;

@Mixin(RealmsConnect.class)
public class MixinRealmsConnect implements IServerConnector {

	@Shadow
	private NetworkManager connection;

	@Shadow
	@Final
	private RealmsScreen onlineScreen;

	@Shadow
	private volatile boolean aborted;

	@Override
	public void setConnection(NetworkManager connection) {
		this.connection = connection;
	}

	@Override
	public NetworkManager getConnection() {
		return connection;
	}

	@Override
	public GuiScreen getPreviousScreen() {
		return onlineScreen.getProxy();
	}

	@Override
	public String nextConnectThreadName() {
		return "Realms-connect-task";
	}

	@Override
	public boolean isCanceled() {
		return aborted;
	}

	@Override
	public void setShowProtocolVersion(boolean showProtocolVersion) {
	}

	@Override
	public void setShownProtocolVersion(int protocolVersion) {
	}

}

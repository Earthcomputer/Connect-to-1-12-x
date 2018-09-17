package net.earthcomputer.connect_to_1_12_x;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetworkManager;

public interface IServerConnector {

	void setConnection(NetworkManager connection);
	
	NetworkManager getConnection();
	
	GuiScreen getPreviousScreen();
	
	String nextConnectThreadName();
	
	boolean isCanceled();
	
	void setShowProtocolVersion(boolean showProtocolVersion);
	
	void setShownProtocolVersion(int protocolVersion);
	
}

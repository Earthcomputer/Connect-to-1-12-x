package net.earthcomputer.connect_to_1_12_x;

import java.io.File;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.modconfig.ConfigPanel;

import fi.dy.masa.malilib.config.ConfigManager;

public class LiteModConnectTo112x implements LiteMod, Configurable {

	@Override
	public String getName() {
		return "Connect to 1.12.x";
	}

	@Override
	public String getVersion() {
		return "1.3.1";
	}

	@Override
	public void init(File configPath) {
		Config.loadConfig();
		ConfigManager.getInstance().registerConfigHandler("connect_to_1_12_x", new Config());
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
	}

	@Override
	public Class<? extends ConfigPanel> getConfigPanelClass() {
		return ConnectTo112xConfigPanel.class;
	}

}

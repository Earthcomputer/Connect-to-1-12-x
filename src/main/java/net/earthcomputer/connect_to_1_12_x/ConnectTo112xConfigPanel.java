package net.earthcomputer.connect_to_1_12_x;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.ConfigPanelSub;

public class ConnectTo112xConfigPanel extends ConfigPanelBase {

	@Override
	protected String getPanelTitlePrefix() {
		return "Connect to 1.12.x Options";
	}

	@Override
	protected void createSubPanels() {
		addSubPanel(
				new ConfigPanelSub("connect_to_1_12_x", "General", Config.OPTIONS.toArray(new IConfigValue[0]), this));
	}

}

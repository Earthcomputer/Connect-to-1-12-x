package net.earthcomputer.connect_to_1_12_x;

import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.GuiModConfigs;

public class ConnectTo112xConfigPanel extends ConfigPanelBase {

	@Override
	protected String getPanelTitlePrefix() {
		return "Connect to 1.12.x Options";
	}

	@Override
	protected void createSubPanels() {
		addSubPanel(new GuiModConfigs("connect_to_1_12_x", "General", Config.OPTIONS).setConfigWidth(120));
	}

}

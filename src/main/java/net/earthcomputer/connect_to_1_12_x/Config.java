package net.earthcomputer.connect_to_1_12_x;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mumfrey.liteloader.core.LiteLoader;

import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.JsonUtils;

public class Config implements IConfigHandler {

	public static final ConfigInteger CONNECTION_THROTTLE = new ConfigInteger("connectionThrottle", 0, 0, 10000,
			"The amount of time to wait between reconnecting with a different version");
	public static final ConfigInteger DEFAULT_PROTOCOL_VERSION = new ConfigInteger("defaultProtocolVersion",
			PacketLists.PROTOCOL_1_12, PacketLists.PROTOCOL_1_12, PacketLists.PROTOCOL_1_12_2,
			"The first protocol version to attempt to log into servers. Use for servers with nonstandard handshake procedure");

	public static final List<IConfigValue> OPTIONS = ImmutableList.of(CONNECTION_THROTTLE, DEFAULT_PROTOCOL_VERSION);

	public static void loadConfig() {
		File configFile = new File(LiteLoader.getCommonConfigFolder(), "connect_to_1_12_x.cfg");
		if (!configFile.isFile()) {
			return;
		}

		JsonElement element = JsonUtils.parseJsonFile(configFile);
		if (element != null && element.isJsonObject()) {
			JsonObject root = element.getAsJsonObject();
			ConfigUtils.readConfigBase(root, "General", OPTIONS);
		}
	}

	public static void saveConfig() {
		File configFile = new File(LiteLoader.getCommonConfigFolder(), "connect_to_1_12_x.cfg");
		if (!configFile.getParentFile().isDirectory())
			return;

		JsonObject root = new JsonObject();
		ConfigUtils.writeConfigBase(root, "General", OPTIONS);
		JsonUtils.writeJsonToFile(root, configFile);
	}

	@Override
	public void onConfigsChanged() {
		saveConfig();
	}

	@Override
	public void save() {
		saveConfig();
	}

}

package net.earthcomputer.connect_to_1_12_x;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsTasks.RealmsConnectTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class MultiConnectHelper {

	private static final Logger LOGGER = LogManager.getLogger("Connect to 1.12.x");

	private static boolean connecting = false;
	private static Set<Integer> triedVersions = new LinkedHashSet<>();
	private static int protocolVersion;
	private static String ip;
	private static int port;

	public static int getProtocolVersion() {
		return protocolVersion;
	}

	public static void abort() {
		connecting = false;

		String msg = "connect.disconnected.mvfailed";
		ITextComponent extraInfo = new TextComponentTranslation(
				"connect.disconnected.mvfailed.version", Joiner.on(", ").join(triedVersions));

		IServerConnector connector = getConnector();
		if (connector == null) {
			Minecraft.getMinecraft()
					.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()),
							msg, extraInfo));
		} else {
			GuiScreen prevScreen = connector.getPreviousScreen();
			if (Minecraft.getMinecraft().isConnectedToRealms()) {
				Minecraft.getMinecraft()
						.displayGuiScreen(new DisconnectedRealmsScreen(((GuiScreenRealmsProxy) prevScreen).getProxy(),
								msg, extraInfo).getProxy());
			} else {
				Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(prevScreen, msg, extraInfo));
			}
		}
		triedVersions.clear();
	}

	public static boolean isConnecting() {
		return connecting;
	}

	public static void onConnectSuccess() {
		connecting = false;
		triedVersions.clear();
		IServerConnector connector = getConnector();
		if (connector != null)
			connector.setShowProtocolVersion(false);
	}

	public static void onHandshakeSent(int version, String ip, int port, EnumConnectionState nextState) {
		if (nextState == EnumConnectionState.LOGIN) {
			protocolVersion = version;
			if (!connecting) {
				onStartConnect(version, ip, port);
			}
			PacketLists.switchProtocol(version);
		}
	}

	public static void onStartConnect(int version, String ip, int port) {
		connecting = true;
		triedVersions.add(version);
		MultiConnectHelper.ip = ip;
		MultiConnectHelper.port = port;
		IServerConnector connector = getConnector();
		if (connector != null) {
			connector.setShownProtocolVersion(version);
			connector.setShowProtocolVersion(true);
		}
	}

	public static void tryNextVersion() {
		Integer version = PacketLists.getSupportedProtocols().stream().filter(v -> !triedVersions.contains(v)).findAny()
				.orElse(null);
		if (version == null)
			abort();
		else
			tryReconnect(version);
	}

	private static void tryReconnect(int version) {
		triedVersions.add(version);

		IServerConnector connector = getConnector();
		if (connector == null) {
			LOGGER.error("Couldn't reconnect");
			abort();
			return;
		}

		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			LOGGER.error("Couldn't reconnect", e);
			abort();
			return;
		}

		new Thread(connector.nextConnectThreadName()) {
			@Override
			public void run() {
				connector.setShownProtocolVersion(version);
				connector.setShowProtocolVersion(true);
				try {
					Thread.sleep(Config.CONNECTION_THROTTLE.getIntegerValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (connector.isCanceled())
					return;
				connector.setConnection(NetworkManager.createNetworkManagerAndConnect(inetAddress, port,
						Minecraft.getMinecraft().gameSettings.isUsingNativeTransport()));
				if (connector.isCanceled())
					return;
				connector.getConnection().setNetHandler(new NetHandlerLoginClient(connector.getConnection(),
						Minecraft.getMinecraft(), connector.getPreviousScreen()));
				if (connector.isCanceled())
					return;
				connector.getConnection().sendPacket(new C00Handshake(version, ip, port, EnumConnectionState.LOGIN));
				if (connector.isCanceled())
					return;
				connector.getConnection()
						.sendPacket(new CPacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
				if (connector.isCanceled())
					return;
				connector.setShowProtocolVersion(false);
			}
		}.start();
	}

	private static IServerConnector getConnector() {
		try {
			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
			if (currentScreen instanceof GuiConnecting) {
				return (IServerConnector) Minecraft.getMinecraft().currentScreen;
			} else if (Minecraft.getMinecraft().isConnectedToRealms()
					&& currentScreen instanceof GuiScreenRealmsProxy) {
				RealmsScreen realmsScreen = ((GuiScreenRealmsProxy) currentScreen).getProxy();
				if (realmsScreen instanceof RealmsLongRunningMcoTaskScreen) {
					Field field = RealmsLongRunningMcoTaskScreen.class.getDeclaredField("task");
					field.setAccessible(true);
					LongRunningTask task = (LongRunningTask) field.get(realmsScreen);
					if (task instanceof RealmsConnectTask) {
						field = RealmsConnectTask.class.getDeclaredField("realmsConnect");
						field.setAccessible(true);
						return (IServerConnector) field.get(task);
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

}

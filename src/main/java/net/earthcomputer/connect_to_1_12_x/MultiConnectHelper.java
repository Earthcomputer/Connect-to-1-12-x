package net.earthcomputer.connect_to_1_12_x;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;

public class MultiConnectHelper {

    public static int getProtocolVersion() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        return data == null ? PacketLists.PROTOCOL_1_12_2 : ((IExServerData) data).getProtocolVersion();
    }

    public static void onHandshakeSent(int version, String ip, int port, EnumConnectionState nextState) {
        if (nextState == EnumConnectionState.LOGIN) {
            PacketLists.switchProtocol(version);
        }
    }

}

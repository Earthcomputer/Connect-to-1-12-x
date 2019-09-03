package net.earthcomputer.connect_to_1_12_x;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketKeepAlive1121 implements Packet<INetHandlerPlayServer> {

	private int id;

	public CPacketKeepAlive1121() {
	}

	public CPacketKeepAlive1121(int id) {
		this.id = id;
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		throw new UnsupportedOperationException("This should never be called!");
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarInt(id);
	}

	@Override
	public void processPacket(INetHandlerPlayServer handler) {
		throw new UnsupportedOperationException("This should never be called!");
	}
}

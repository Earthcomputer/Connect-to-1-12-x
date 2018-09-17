package net.earthcomputer.connect_to_1_12_x;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketKeepAlive1122 implements Packet<INetHandlerPlayClient> {

	private long id;

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.id = buf.readLong();
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		throw new UnsupportedOperationException("This should never be called!");
	}

	@Override
	public void processPacket(INetHandlerPlayClient handler) {
		Minecraft.getMinecraft().getConnection().sendPacket(new CPacketKeepAlive1122(id));
	}

}

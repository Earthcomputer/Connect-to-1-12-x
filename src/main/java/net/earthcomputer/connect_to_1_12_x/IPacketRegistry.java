package net.earthcomputer.connect_to_1_12_x;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

public interface IPacketRegistry {

	void register(EnumPacketDirection dir, Class<? extends Packet<?>> packetClass);
	
	void clear();
	
}

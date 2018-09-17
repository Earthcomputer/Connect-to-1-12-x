package net.earthcomputer.connect_to_1_12_x.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.BiMap;

import net.earthcomputer.connect_to_1_12_x.IPacketRegistry;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

@Mixin(EnumConnectionState.class)
public abstract class MixinEnumConnectionState implements IPacketRegistry {

	@Shadow
	@Final
	private Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> directionMaps;

	@Shadow
	@Final
	private static Map<Class<? extends Packet<?>>, EnumConnectionState> STATES_BY_CLASS;

	@Shadow
	protected abstract EnumConnectionState registerPacket(EnumPacketDirection dir,
			Class<? extends Packet<?>> packetClass);

	@Override
	public void register(EnumPacketDirection dir, Class<? extends Packet<?>> packetClass) {
		registerPacket(dir, packetClass);
		STATES_BY_CLASS.put(packetClass, (EnumConnectionState) (Object) this);
	}

	@Override
	public void clear() {
		for (BiMap<Integer, Class<? extends Packet<?>>> bimap : directionMaps.values()) {
			for (Class<? extends Packet<?>> packetClass : bimap.values()) {
				STATES_BY_CLASS.remove(packetClass);
			}
		}
		directionMaps.clear();
	}

}

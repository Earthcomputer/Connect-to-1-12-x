package net.earthcomputer.connect_to_1_12_x;

import java.io.IOException;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketPlaceRecipe implements Packet<INetHandlerPlayServer> {

	private int windowId;
	private IRecipe recipe;
	private boolean shiftClick;

	public CPacketPlaceRecipe() {
	}

	public CPacketPlaceRecipe(int windowId, IRecipe recipe, boolean shiftClick) {
		this.windowId = windowId;
		this.recipe = recipe;
		this.shiftClick = shiftClick;
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		throw new UnsupportedOperationException("This should never be called!");
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeByte(windowId);
		buf.writeVarInt(CraftingManager.getIDForRecipe(recipe));
		buf.writeBoolean(shiftClick);
	}

	@Override
	public void processPacket(INetHandlerPlayServer handler) {
		throw new UnsupportedOperationException("This should never be called!");
	}

}

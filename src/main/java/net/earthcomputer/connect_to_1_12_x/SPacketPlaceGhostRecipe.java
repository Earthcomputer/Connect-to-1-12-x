package net.earthcomputer.connect_to_1_12_x;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.inventory.Container;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {

	private int windowId;
	private IRecipe recipe;

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.windowId = buf.readByte();
		this.recipe = CraftingManager.getRecipeById(buf.readVarInt());
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		throw new UnsupportedOperationException("This should never be called!");
	}

	@Override
	public void processPacket(INetHandlerPlayClient handler) {
		PacketThreadUtil.checkThreadAndEnqueue(this, handler, Minecraft.getMinecraft());

		Container openContainer = Minecraft.getMinecraft().player.openContainer;
		if (openContainer.windowId == windowId && openContainer.getCanCraft(Minecraft.getMinecraft().player)) {
			if (Minecraft.getMinecraft().currentScreen instanceof IRecipeShownListener) {
				GuiRecipeBook gui = getRecipeBook(Minecraft.getMinecraft().currentScreen);
				((IGuiRecipeBook) gui).doSetupGhostRecipe(recipe, openContainer.inventorySlots);
			}
		}
	}

	private static GuiRecipeBook getRecipeBook(Object holder) {
		try {
			for (Field field : holder.getClass().getDeclaredFields()) {
				if (field.getType() == GuiRecipeBook.class) {
					field.setAccessible(true);
					return (GuiRecipeBook) field.get(holder);
				}
			}
			System.err.println("No recipe book gui field in " + holder.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

}

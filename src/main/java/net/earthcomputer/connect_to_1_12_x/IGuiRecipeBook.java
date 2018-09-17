package net.earthcomputer.connect_to_1_12_x;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.crafting.IRecipe;

public interface IGuiRecipeBook {

	void doSetupGhostRecipe(IRecipe recipe, List<Slot> slots);
	
}

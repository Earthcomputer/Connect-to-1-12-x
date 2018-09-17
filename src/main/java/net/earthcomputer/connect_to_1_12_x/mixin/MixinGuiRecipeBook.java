package net.earthcomputer.connect_to_1_12_x.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.earthcomputer.connect_to_1_12_x.CPacketPlaceRecipe;
import net.earthcomputer.connect_to_1_12_x.IGuiRecipeBook;
import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.inventory.Slot;
import net.minecraft.item.crafting.IRecipe;

@Mixin(GuiRecipeBook.class)
public class MixinGuiRecipeBook implements IGuiRecipeBook {

	@Shadow
	@Final
	private GhostRecipe ghostRecipe;

	@Shadow
	@Final
	private RecipeBookPage recipeBookPage;

	@Shadow
	private void setupGhostRecipe(IRecipe recipe, List<Slot> slots) {
	}

	@Override
	public void doSetupGhostRecipe(IRecipe recipe, List<Slot> slots) {
		setupGhostRecipe(recipe, slots);
	}

	@Shadow
	private boolean isOffsetNextToMainGUI() {
		return false;
	}

	@Shadow
	private void setVisible(boolean visible) {
	}

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/recipebook/GuiRecipeBook;setContainerRecipe(Lnet/minecraft/item/crafting/IRecipe;Lnet/minecraft/client/gui/recipebook/RecipeList;)V"), cancellable = true)
	public void redirectSetContainerRecipe(int x, int y, int button, CallbackInfoReturnable<Boolean> ci) {
		if (MultiConnectHelper.getProtocolVersion() >= PacketLists.PROTOCOL_1_12_1) {
			IRecipe recipe = recipeBookPage.getLastClickedRecipe();
			RecipeList recipeList = recipeBookPage.getLastClickedRecipeList();
			if (!recipeList.isCraftable(recipe) && ghostRecipe.getRecipe() == recipe) {
				ci.setReturnValue(Boolean.FALSE);
			} else {
				ghostRecipe.clear();
				Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlaceRecipe(
						Minecraft.getMinecraft().player.openContainer.windowId, recipe,
						GuiScreen.isShiftKeyDown()));

				if (!isOffsetNextToMainGUI() && button == 0)
					setVisible(false);
				ci.setReturnValue(Boolean.TRUE);
			}

			ci.cancel();
		}
	}

}

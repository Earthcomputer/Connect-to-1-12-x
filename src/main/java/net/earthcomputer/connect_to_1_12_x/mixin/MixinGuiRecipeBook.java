package net.earthcomputer.connect_to_1_12_x.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.earthcomputer.connect_to_1_12_x.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.stats.RecipeBook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@Mixin(GuiRecipeBook.class)
public class MixinGuiRecipeBook implements IGuiRecipeBook {

    private static final Logger LOGGER = LogManager.getLogger();

    @Unique
    private Container container;

    @Shadow
    @Final
    private GhostRecipe ghostRecipe;

    @Shadow
    @Final
    private RecipeBookPage recipeBookPage;

    @Shadow
    private InventoryCrafting craftingSlots;

    @Shadow
    private Minecraft mc;

    @Shadow
    private RecipeBook recipeBook;

    @Shadow
    private RecipeItemHelper stackedContents = new RecipeItemHelper();

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

    @Shadow
    public void toggleVisibility() {
    }

    @Inject(method = "func_194303_a", at = @At("RETURN"))
    public void onInitialize(int width, int height, Minecraft mc, boolean p_194303_4_, InventoryCrafting p_194303_5_, CallbackInfo ci) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof GuiContainer) {
            this.container = ((GuiContainer) gui).inventorySlots;
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/recipebook/RecipeList;isCraftable(Lnet/minecraft/item/crafting/IRecipe;)Z"), cancellable = true)
    public void redirectSetContainerRecipe(int x, int y, int button, CallbackInfoReturnable<Boolean> ci) {
        if (MultiConnectHelper.getProtocolVersion() <= PacketLists.PROTOCOL_1_12) {
            this.setContainerRecipe(this.recipeBookPage.getLastClickedRecipe(), this.recipeBookPage.getLastClickedRecipeList());
            if (!this.isOffsetNextToMainGUI() && button == 0)
            {
                this.setVisible(false);
            }
            ci.setReturnValue(Boolean.TRUE);
        }
    }

    @Unique
    private void setContainerRecipe(IRecipe recipe, RecipeList recipes) {
        boolean flag = recipes.isCraftable(recipe);
        InventoryCraftResult inventorycraftresult = null;

        if (this.container instanceof ContainerWorkbench) {
            inventorycraftresult = ((ContainerWorkbench) this.container).craftResult;
        } else if (this.container instanceof ContainerPlayer) {
            inventorycraftresult = ((ContainerPlayer) this.container).craftResult;
        }

        if (inventorycraftresult != null) {
            if (!flag && this.ghostRecipe.getRecipe() == recipe) {
                return;
            }

            if (!this.testClearCraftingGrid() && !this.mc.player.isCreative()) {
                return;
            }

            if (flag) {
                this.handleRecipeClicked(recipe, this.container.inventorySlots, this.container.windowId, inventorycraftresult);
            } else {
                List<CPacketRecipePlacement.ItemMove> list2 = this.clearCraftingGrid(inventorycraftresult);
                this.setupGhostRecipe(recipe, this.container.inventorySlots);

                if (!list2.isEmpty()) {
                    short short1 = this.mc.player.openContainer.getNextTransactionID(this.mc.player.inventory);
                    this.mc.getConnection().sendPacket(new CPacketRecipePlacement(this.container.windowId, list2, Lists.newArrayList(), short1));

                    if (this.recipeBook.isFilteringCraftable()) {
                        this.mc.player.inventory.markDirty();
                    }
                }
            }

            if (!this.isOffsetNextToMainGUI()) {
                this.toggleVisibility();
            }
        }
    }

    @Unique
    private void handleRecipeClicked(IRecipe p_193950_1_, List<Slot> p_193950_2_, int p_193950_3_, InventoryCraftResult p_193950_4_) {
        boolean flag = p_193950_1_.matches(this.craftingSlots, this.mc.world);
        int i = this.stackedContents.getBiggestCraftableStack(p_193950_1_, (IntList) null);

        if (flag) {
            boolean flag1 = true;

            for (int j = 0; j < this.craftingSlots.getSizeInventory(); ++j) {
                ItemStack itemstack = this.craftingSlots.getStackInSlot(j);

                if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                    flag1 = false;
                }
            }

            if (flag1) {
                return;
            }
        }

        int i1 = this.getStackSize(i, flag);
        IntList intlist = new IntArrayList();

        if (this.stackedContents.canCraft(p_193950_1_, intlist, i1)) {
            int j1 = i1;
            IntListIterator lvt_10_1_ = intlist.iterator();

            while (lvt_10_1_.hasNext()) {
                int k = ((Integer) lvt_10_1_.next()).intValue();
                int l = RecipeItemHelper.unpack(k).getMaxStackSize();

                if (l < j1) {
                    j1 = l;
                }
            }

            if (this.stackedContents.canCraft(p_193950_1_, intlist, j1)) {
                List<CPacketRecipePlacement.ItemMove> list2 = this.clearCraftingGrid(p_193950_4_);
                List<CPacketRecipePlacement.ItemMove> list3 = Lists.<CPacketRecipePlacement.ItemMove>newArrayList();
                this.placeRecipe(p_193950_1_, p_193950_2_, j1, intlist, list3);
                short short1 = this.mc.player.openContainer.getNextTransactionID(this.mc.player.inventory);
                this.mc.getConnection().sendPacket(new CPacketRecipePlacement(this.container.windowId, list2, Lists.newArrayList(), short1));
                this.mc.player.inventory.markDirty();
            }
        }
    }

    @Unique
    private List<CPacketRecipePlacement.ItemMove> clearCraftingGrid(InventoryCraftResult p_193954_1_) {
        this.ghostRecipe.clear();
        InventoryPlayer inventoryplayer = this.mc.player.inventory;
        List<CPacketRecipePlacement.ItemMove> list2 = Lists.<CPacketRecipePlacement.ItemMove>newArrayList();

        for (int i = 0; i < this.craftingSlots.getSizeInventory(); ++i) {
            ItemStack itemstack = this.craftingSlots.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                while (itemstack.getCount() > 0) {
                    int j = inventoryplayer.storeItemStack(itemstack);

                    if (j == -1) {
                        j = inventoryplayer.getFirstEmptyStack();
                    }

                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.setCount(1);

                    if (inventoryplayer.add(j, itemstack1)) {
                        itemstack1.grow(1);
                    } else {
                        LOGGER.error("Can't find any space for item in inventory");
                    }

                    this.craftingSlots.decrStackSize(i, 1);
                    int k = i + 1;
                    list2.add(new CPacketRecipePlacement.ItemMove(itemstack1.copy(), k, j));
                }
            }
        }

        this.craftingSlots.clear();
        p_193954_1_.clear();
        return list2;
    }

    @Unique
    private int getStackSize(int p_193943_1_, boolean p_193943_2_) {
        int i = 1;

        if (GuiScreen.isShiftKeyDown()) {
            i = p_193943_1_;
        } else if (p_193943_2_) {
            i = 64;

            for (int j = 0; j < this.craftingSlots.getSizeInventory(); ++j) {
                ItemStack itemstack = this.craftingSlots.getStackInSlot(j);

                if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                    i = itemstack.getCount();
                }
            }

            if (i < 64) {
                ++i;
            }
        }

        return i;
    }

    @Unique
    private void placeRecipe(IRecipe p_193013_1_, List<Slot> p_193013_2_, int p_193013_3_, IntList p_193013_4_, List<CPacketRecipePlacement.ItemMove> p_193013_5_) {
        int i = this.craftingSlots.getWidth();
        int j = this.craftingSlots.getHeight();

        if (p_193013_1_ instanceof ShapedRecipes) {
            ShapedRecipes shapedrecipes = (ShapedRecipes) p_193013_1_;
            i = shapedrecipes.getWidth();
            j = shapedrecipes.getHeight();
        }

        int j1 = 1;
        Iterator<Integer> iterator = p_193013_4_.iterator();

        for (int k = 0; k < this.craftingSlots.getWidth() && j != k; ++k) {
            for (int l = 0; l < this.craftingSlots.getHeight(); ++l) {
                if (i == l || !iterator.hasNext()) {
                    j1 += this.craftingSlots.getWidth() - l;
                    break;
                }

                Slot slot = p_193013_2_.get(j1);
                ItemStack itemstack = RecipeItemHelper.unpack(((Integer) iterator.next()).intValue());

                if (itemstack.isEmpty()) {
                    ++j1;
                } else {
                    for (int i1 = 0; i1 < p_193013_3_; ++i1) {
                        CPacketRecipePlacement.ItemMove cpacketrecipeplacement$itemmove = this.findSpot(j1, slot, itemstack);

                        if (cpacketrecipeplacement$itemmove != null) {
                            p_193013_5_.add(cpacketrecipeplacement$itemmove);
                        }
                    }

                    ++j1;
                }
            }

            if (!iterator.hasNext()) {
                break;
            }
        }
    }

    @Nullable
    @Unique
    private CPacketRecipePlacement.ItemMove findSpot(int p_193946_1_, Slot p_193946_2_, ItemStack p_193946_3_) {
        InventoryPlayer inventoryplayer = this.mc.player.inventory;
        int i = inventoryplayer.findSlotMatchingUnusedItem(p_193946_3_);

        if (i == -1) {
            return null;
        } else {
            ItemStack itemstack = inventoryplayer.getStackInSlot(i).copy();

            if (itemstack.isEmpty()) {
                LOGGER.error("Matched: " + p_193946_3_.getUnlocalizedName() + " with empty item.");
                return null;
            } else {
                if (itemstack.getCount() > 1) {
                    inventoryplayer.decrStackSize(i, 1);
                } else {
                    inventoryplayer.removeStackFromSlot(i);
                }

                itemstack.setCount(1);

                if (p_193946_2_.getStack().isEmpty()) {
                    p_193946_2_.putStack(itemstack);
                } else {
                    p_193946_2_.getStack().grow(1);
                }

                return new CPacketRecipePlacement.ItemMove(itemstack, p_193946_1_, i);
            }
        }
    }

    @Unique
    private boolean testClearCraftingGrid() {
        InventoryPlayer inventoryplayer = this.mc.player.inventory;

        for (int i = 0; i < this.craftingSlots.getSizeInventory(); ++i) {
            ItemStack itemstack = this.craftingSlots.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                int j = inventoryplayer.storeItemStack(itemstack);

                if (j == -1) {
                    j = inventoryplayer.getFirstEmptyStack();
                }

                if (j == -1) {
                    return false;
                }
            }
        }

        return true;
    }
}

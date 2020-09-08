package piman.recievermod.items.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import piman.recievermod.init.ModRecipeSerializers;
import piman.recievermod.items.accessories.ItemAccessory;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.util.Reference;
import piman.recievermod.util.handlers.RegistryEventHandler;

import java.util.ArrayList;
import java.util.List;

public class AccessoryRecipe extends SpecialRecipe {

    public AccessoryRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    private int matchGun(CraftingInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() instanceof ItemGun) {
                return i;
            }
        }
        return -1;
    }

    private List<Integer> findValidAccessories(CraftingInventory inv, ItemGun gun) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() instanceof ItemAccessory && ((ItemAccessory) inv.getStackInSlot(i).getItem()).getSlot() == i && gun.acceptsAccessory(((ItemAccessory) inv.getStackInSlot(i).getItem()).getType())) {
                list.add(i);
            }
        }
        return list;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        int i = matchGun(inv);
        if (i == 4) {
            List<Integer> validAccessorySlots = findValidAccessories(inv, (ItemGun) inv.getStackInSlot(i).getItem());
            for (int j = 0; j < inv.getSizeInventory(); j++) {
                ItemStack stack = inv.getStackInSlot(j);
                stacks.set(j, validAccessorySlots.contains(j) ? stack.hasContainerItem() ? stack.getContainerItem() : ItemStack.EMPTY : stack);
            }
        }
        else {
            ItemStack stack = inv.getStackInSlot(i).copy();
            ((ItemGun) stack.getItem()).removeAccessory(stack, i);
            for (int j = 0; j < inv.getSizeInventory(); j++) {
                stacks.set(j, j == i ? stack : inv.getStackInSlot(j));
            }
        }
        return stacks;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        int i;
        if ((i = matchGun(inv)) == -1) return false;
        if (i == 4) {
            return findValidAccessories(inv, (ItemGun) inv.getStackInSlot(4).getItem()).size() > 0;
        }
        else {
            return ((ItemGun) inv.getStackInSlot(i).getItem()).hasAccessory(inv.getStackInSlot(i), i);
        }
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int i = matchGun(inv);
        if (i == 4) {
            ItemStack gunStack = inv.getStackInSlot(4);
            ItemStack gunStackCopy = gunStack.copy();
            List<Integer> validSlots = findValidAccessories(inv, (ItemGun) gunStack.getItem());
            for (Integer i2 : validSlots) {
                ((ItemGun) gunStackCopy.getItem()).setAccessory(gunStackCopy, (ItemAccessory) inv.getStackInSlot(i2).getItem());
            }
            return gunStackCopy;
        }
        else {
            return new ItemStack(((ItemGun) inv.getStackInSlot(i).getItem()).getAccessory(inv.getStackInSlot(i), i));
        }
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     *
     * @param width
     * @param height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ACCESSORY;
    }
}

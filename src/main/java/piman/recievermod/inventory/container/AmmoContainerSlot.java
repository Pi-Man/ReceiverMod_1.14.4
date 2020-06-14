package piman.recievermod.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import piman.recievermod.items.bullets.ItemBullet;

public class AmmoContainerSlot extends Slot {
    public AmmoContainerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void putStack(ItemStack stack) {
        super.putStack(stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemBullet;
    }

    @Override
    public int getSlotStackLimit() {
        return Integer.MAX_VALUE;
    }
}

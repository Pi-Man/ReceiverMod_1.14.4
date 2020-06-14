package piman.recievermod.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import piman.recievermod.init.ModContainerTypes;
import piman.recievermod.items.bullets.ItemBulletMediumCasing;

import javax.annotation.Nonnull;

public class ContainerBulletCrafter extends Container {

	private IInventory inventory;

	private IIntArray intArray;
	
	public ContainerBulletCrafter(int id, PlayerInventory inventoryplayer, IInventory inventory, IIntArray intArray) {
        super(ModContainerTypes.BULLET_CRAFTER, id);
        this.inventory = inventory;
		int i = 0;
		this.addSlot(new Slot(inventory, i++, 21, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof ItemBulletMediumCasing;
			}
		});
		this.addSlot(new Slot(inventory, i++, 39, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.REDSTONE;
			}
		});
		this.addSlot(new Slot(inventory, i++, 57, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.GUNPOWDER;
			}
		});
		this.addSlot(new Slot(inventory, i++, 75, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.IRON_INGOT;
			}
		});
		this.addSlot(new Slot(inventory, i++, 131, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
			@Nonnull
            @Override
			public ItemStack onTake(PlayerEntity thePlayer, @Nonnull ItemStack stack) {
				this.onCrafting(stack);
				return super.onTake(thePlayer, stack);
			}
		});
		
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventoryplayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventoryplayer, x, 8 + x * 18, 142));
        }

        this.intArray = intArray;
        this.trackIntArray(intArray);
	}

    public ContainerBulletCrafter(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(5), new IntArray(2));
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn)
    {
        return this.inventory.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index <= 4)
            {
                if (!this.mergeItemStack(itemstack1, 5, 41, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else
            {
            	boolean flag = false;
            	int i;
            	for (i = 0; i < 5; i++) {
            		Slot otherslot = this.inventorySlots.get(i);
            		if (otherslot.isItemValid(itemstack1)) {
            			flag = true;
            			break;
            		}
            	}
            	if (flag) {
            		if (!this.mergeItemStack(itemstack1, i, i+1, false)) {
            			return ItemStack.EMPTY;
            		}
            	}
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 32, 41, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 5, 32, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public int getCookProgressionScaled() {
        return this.intArray.get(1) == 0 ? 27 : this.intArray.get(0) * 27 / this.intArray.get(1);
    }
}

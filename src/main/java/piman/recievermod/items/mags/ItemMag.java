package piman.recievermod.items.mags;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.init.ModItemGroups;
import piman.recievermod.items.IItemInit;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.IAnimationController;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageAddToInventory;
import piman.recievermod.network.messages.MessageUpdateNBT;

public class ItemMag extends Item {
	
	protected int maxAmmo;
	
	protected Supplier<Item> ammo;

	public ItemMag(Item.Properties properties, int maxAmmo, Supplier<Item> ammo) {
		super(properties.group(ModItemGroups.GUNS).maxStackSize(1));

		this.maxAmmo = maxAmmo;
		this.ammo = ammo;

		ItemPropertyWrapper bullets = IAnimationController.listCountProperty("bullets", 8, true);
	
		this.addPropertyOverride(bullets.getName(), bullets.getOverride());
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("ID: " + stack.getOrCreateTag().getString("UUID")));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof PlayerEntity && worldIn.isRemote) {
						
			PlayerEntity player = (PlayerEntity) entityIn;

			worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {

				CompoundNBT nbt;

				if (itemData.getItemData().contains(stack.getOrCreateTag().getString("UUID"))) {
					nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				}
				else {
					nbt = new CompoundNBT();
					itemData.getItemData().put(stack.getOrCreateTag().getString("UUID"), nbt);
				}

				CompoundNBT old = nbt.copy();
				old.remove("prev");
				nbt.put("prev", old);

				if (!nbt.contains("bullets", 9)) {
					nbt.put("bullets", new ListNBT());
				}

				if (player.getHeldItemMainhand().equals(stack)) {

					if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet)) {
						if (nbt.getList("bullets", 8).size() < this.maxAmmo) {

							ItemStack ammo = findAmmo(player);

							if (!ammo.isEmpty()) {
								nbt.getList("bullets", 8).add(new StringNBT(ammo.getItem().getRegistryName().toString()));
								NetworkHandler.sendToServer(new MessageAddToInventory(ammo, -1));
							}
						}
					}

					if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveBullet)) {
						if (nbt.getList("bullets", 8).size() > 0) {
							nbt.getList("bullets", 8).remove(nbt.getList("bullets", 8).size() - 1);
							NetworkHandler.sendToServer(new MessageAddToInventory(this.ammo.get(), 1));
						}
					}
					//Main.LOGGER.info("Sending nbt at slot {}", itemSlot);
				}
				NetworkHandler.sendToServer(new MessageUpdateNBT(stack, itemSlot, nbt));
			});
		}
	}
	
	public ItemStack findAmmo(PlayerEntity player) {
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);

            if (this.isBullet(itemstack))
            {
                return itemstack;
            }
        }

        return ItemStack.EMPTY;
	}
	
	public boolean isBullet(ItemStack stack) {
        return stack.getItem() == this.ammo.get();
    }
	
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        boolean flag = false;

        if (oldStack.getItem() instanceof ItemMag && newStack.getItem() instanceof ItemMag) {
            flag = oldStack.getOrCreateTag().getString("UUID").equals(newStack.getOrCreateTag().getString("UUID"));
        }

        return slotChanged || !flag;
    }
}

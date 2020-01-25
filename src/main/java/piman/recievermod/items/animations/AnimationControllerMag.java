package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageAddToInventory;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.SoundsHandler;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerMag implements IAnimationController {

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.stringProperty("mag", true));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

		boolean flag = player.getHeldItemMainhand().equals(stack);

		CompoundNBT baseTag = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(IItemData::getItemData).orElse(new CompoundNBT());

		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet) && nbt.getString("mag").isEmpty()) {
			System.out.println("Add Mag Pressed");

			int magslot = gun.findMag(player);

			if (magslot != -1) {
				ItemStack mag = player.inventory.getStackInSlot(magslot);
				//System.out.println("Mag Found: " + mag + mag.getTagCompound());
				NetworkHandler.sendToServer(new MessageAddToInventory(mag, -1, magslot));
				CompoundNBT magTag = baseTag.getCompound(mag.getOrCreateTag().getString("UUID"));
				nbt.put("Bullets", magTag.getList("Bullets", 8));
				nbt.putString("mag", magTag.getString("UUID"));
				NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGIN));
			}
		}
		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveMag)) {

			if (!nbt.getString("mag").isEmpty()) {
				CompoundNBT magNBT = new CompoundNBT();

				magNBT.put("Bullets", nbt.getList("Bullets", 8));
				magNBT.putString("UUID", nbt.getString("mag"));
				nbt.putInt("Bullets", 0);
				nbt.putString("mag", "");

				ItemStack mag = new ItemStack(gun.mag);

				mag.getOrCreateTag().putString("UUID", magNBT.getString("UUID"));
				baseTag.put(magNBT.getString("UUID"), magNBT);

				NetworkHandler.sendToServer(new MessageAddToInventory(mag, 1, player.inventory.getSizeInventory() - 1));

				NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGOUT));
			} else if (gun.isMag(player.getHeldItemOffhand())) {
				TreeMap<Integer, Pair<ItemStack, Integer>> mags = new TreeMap<Integer, Pair<ItemStack, Integer>>();
				for (int i = 0; i < player.inventory.getSizeInventory() - 1; i++) {
					ItemStack itemstack = player.inventory.getStackInSlot(i);
					if (gun.isMag(itemstack)) {
						mags.put(baseTag.getCompound(itemstack.getOrCreateTag().getString("UUID")).getInt("Bullets"), Pair.of(itemstack, i));
					}
				}
				if (!mags.isEmpty()) {
					int slot = mags.lastEntry().getValue().getRight();
					ItemStack oldstack = player.getHeldItemOffhand();
					ItemStack newstack = mags.lastEntry().getValue().getLeft();
					NetworkHandler.sendToServer(new MessageAddToInventory(newstack, -1, slot));
					NetworkHandler.sendToServer(new MessageAddToInventory(oldstack, -1, player.inventory.getSizeInventory() - 1));
					NetworkHandler.sendToServer(new MessageAddToInventory(oldstack, 1, slot));
					NetworkHandler.sendToServer(new MessageAddToInventory(newstack, 1, player.inventory.getSizeInventory() - 1));
				}
			}
		}
	}

}

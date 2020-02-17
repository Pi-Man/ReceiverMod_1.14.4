package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageAddToInventory;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.SoundsHandler;

public class AnimationControllerMag implements IAnimationController {
	
	public class MagAddEvent extends AnimationEvent {

		public MagAddEvent(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
			super(stack, world, player, itemSlot, isSelected, nbt, gun);
		}
		
	}
	
	public class MagRemoveEvent extends AnimationEvent {

		public MagRemoveEvent(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
			super(stack, world, player, itemSlot, isSelected, nbt, gun);
		}
		
	}
	
	private boolean onMagAdd(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

		MagAddEvent event = new MagAddEvent(stack, world, player, itemSlot, isSelected, nbt, gun);
		
		if (MinecraftForge.EVENT_BUS.post(event)) return false;
		
		return true;
		
	}
	
	private boolean onMagRemoved(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

		MagRemoveEvent event = new MagRemoveEvent(stack, world, player, itemSlot, isSelected, nbt, gun);
		
		if (MinecraftForge.EVENT_BUS.post(event)) return false;
		
		return true;
		
	}
	
	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.stringProperty("mag", true));
		list.add(IAnimationController.listCountProperty("bullets", 8, true));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

		boolean flag = player.getHeldItemMainhand().equals(stack);

		CompoundNBT baseTag = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(IItemData::getItemData).orElse(new CompoundNBT());

		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet) && nbt.getString("mag").isEmpty()) {
			System.out.println("Add Mag Pressed");

			int magslot = gun.findMag(player);

			if (magslot != -1 && onMagAdd(stack, worldIn, player, itemSlot, isSelected, nbt, gun)) {
				ItemStack mag = player.inventory.getStackInSlot(magslot);
				//System.out.println("Mag Found: " + mag + mag.getTagCompound());
				NetworkHandler.sendToServer(new MessageAddToInventory(mag, -1, magslot));
				CompoundNBT magTag = baseTag.getCompound(mag.getOrCreateTag().getString("UUID"));
				nbt.put("bullets", magTag.getList("bullets", 8));
				nbt.putString("mag", magTag.getString("UUID"));
				NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGIN));
			}
		}
		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveMag)) {

			if (!nbt.getString("mag").isEmpty()) {
				if (onMagRemoved(stack, worldIn, player, itemSlot, isSelected, nbt, gun)) {
					CompoundNBT magNBT = new CompoundNBT();
	
					magNBT.put("bullets", nbt.getList("bullets", 8));
					magNBT.putString("UUID", nbt.getString("mag"));
					nbt.putInt("bullets", 0);
					nbt.putString("mag", "");
	
					ItemStack mag = new ItemStack(gun.mag.get());
	
					mag.getOrCreateTag().putString("UUID", magNBT.getString("UUID"));
					baseTag.put(magNBT.getString("UUID"), magNBT);
	
					NetworkHandler.sendToServer(new MessageAddToInventory(mag, 1, player.inventory.getSizeInventory() - 1));
	
					NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGOUT));
				}
			} else if (gun.isMag(player.getHeldItemOffhand())) {
				TreeMap<Integer, Pair<ItemStack, Integer>> mags = new TreeMap<Integer, Pair<ItemStack, Integer>>();
				for (int i = 0; i < player.inventory.getSizeInventory() - 1; i++) {
					ItemStack itemstack = player.inventory.getStackInSlot(i);
					if (gun.isMag(itemstack)) {
						mags.put(baseTag.getCompound(itemstack.getOrCreateTag().getString("UUID")).getInt("bullets"), Pair.of(itemstack, i));
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

package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageEject;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.SoundsHandler;

public class AnimationControllerSlide implements IAnimationController {

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.integerProperty("slide", true));
	    list.add(IAnimationController.integerProperty("check", false));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

		boolean flag = player.getHeldItemMainhand().equals(stack);

		if (nbt.getBoolean("fired")) {
			nbt.putInt("slide", 4);
		}

		if (nbt.getInt("slide") < 3 && flag && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if (nbt.getInt("check") < 4) {
				nbt.putInt("check", nbt.getInt("check") + 1);
			}
		}
		else {
			if (nbt.getInt("check") > 0) {
				nbt.putInt("check", 0);
			}
		}

		if (flag && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if (nbt.getInt("slide") == 0) NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_1911_SLIDEBACK));
			if (nbt.getInt("slide") < 4 && nbt.getInt("check") < 3) {
				nbt.putInt("slide", nbt.getInt("slide") + 1);
			}
		}
//		if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
//			if (nbt.getInt("slide") < 2) {
//				nbt.putInt("slide", nbt.getInt("slide") + 1);
//			}
//			else if ((nbt.getInt("slide") == 2 || nbt.getInt("slide") == 5) && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock)) {
//				//System.out.println("Half Lock");
//				nbt.putInt("slide", 5);
//				nbt.getCompound("prev").putInt("slide", 5);
//			}
//			else if (nbt.getInt("slide") == 5) {
//				nbt.putInt("slide", 2);
//			}
//			else if (nbt.getInt("slide") < 4) {
//				nbt.putInt("slide", nbt.getInt("slide") + 1);
//			}
//		}
		if (nbt.getInt("slide") == 4) {
			nbt.putBoolean("hammer", true);
			if (nbt.getCompound("prev").getInt("slide") < 4) {
				if (!nbt.getString("BulletChambered").isEmpty()) {
					NetworkHandler.sendToServer(new MessageEject(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("BulletChambered"))))));
					nbt.putString("BulletChambered", "");
				}
			}
			if (nbt.getInt("slide") == 4 && !nbt.getString("mag").isEmpty() && nbt.getList("bullets", 8).size() == 0) {
				nbt.putBoolean("AutoSlideLock", true);
			}
		}
		if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if ((nbt.getInt("slide") == 4 && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock)) || nbt.getBoolean("AutoSlideLock")) {
				nbt.putBoolean("AutoSlideLock", true);
			}
			if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock)) {
				nbt.putBoolean("AutoSlideLock", false);
			}
			if (nbt.getInt("slide") == 4 && nbt.getBoolean("AutoSlideLock")) {
				nbt.putInt("slide", 4);
				//NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_1911_SLIDEFORWARD));
			}
			if (nbt.getInt("slide") > 0 && !nbt.getBoolean("AutoSlideLock")) {
				nbt.putInt("slide", 0);
				NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_1911_SLIDEFORWARD));
			}
		}
//		if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
//			if (nbt.getInt("slide") == 5) {
//				nbt.putInt("slide", 2);
//			}
//			if (nbt.getInt("slide") == 4) {
//				if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) && nbt.getString("BulletChambered").isEmpty() && !nbt.getString("mag").isEmpty() && nbt.getList("Bullets", 8).size() == 0) {
//					nbt.putBoolean("AutoSlideLock", true);
//				}
//				else {
//					nbt.putBoolean("AutoSlideLock", false);
//				}
//				nbt.putInt("slide", 3);
//			}
//			if (nbt.getInt("slide") == 3 && (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) || nbt.getBoolean("AutoSlideLock")) && !KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock)) {
//				//System.out.println("Full Lock");
//				nbt.putBoolean("AutoSlideLock", true);
//			}
//			else {
//				nbt.putInt("slide", 0);
//			}
//		}
		if (nbt.getInt("slide") == 0) {
			nbt.putBoolean("AutoSlideLock", false);
		}
		if (nbt.getBoolean("fired")) {
			nbt.putInt("slide", 4);
		}
		if (nbt.getCompound("prev").getInt("slide") > 2 && nbt.getCompound("prev").getInt("slide") < 5 && nbt.getList("bullets", 8).size() > 0 && nbt.getString("BulletChambered").isEmpty() && (nbt.getInt("slide") < nbt.getCompound("prev").getInt("slide"))) {
			nbt.putString("BulletChambered", nbt.getList("bullets", 8).getString(nbt.getList("bullets", 8).size() - 1));
			nbt.getList("bullets", 8).remove(nbt.getList("bullets", 8).size() - 1);
			System.out.println("pickup");
		}
		//System.out.println(nbt);
	}

}

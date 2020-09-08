package piman.recievermod.items.animations;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.AnimationControllerMag.MagAddEvent;
import piman.recievermod.items.animations.AnimationControllerMag.MagRemoveEvent;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageAddToInventory;
import piman.recievermod.network.messages.MessageEject;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.SoundsHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimationControllerGarandAction implements IAnimationController {
	
	private final ItemGun itemgun;
	
	private final Random rand = new Random();
	
	public AnimationControllerGarandAction(ItemGun itemgun) {
		this.itemgun = itemgun;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMagAdd(MagAddEvent event) {
		if (event.getGun() == itemgun) {
			if (event.getNbt().getInt("slide") != 4) {
				event.setCanceled(true);
			}
			else {
				if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
					event.getNbt().putBoolean("AutoSlideLock", false);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMagRemove(MagRemoveEvent event) {
		if (event.getGun() == itemgun) {
			if (event.getNbt().getInt("slide") != 4) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onShootEvent(AnimationControllerShoot.ShootEvent.Post event) {
		if (event.getGun() == this.itemgun) {
			event.getNbt().putInt("slide", 4);
			event.getNbt().getCompound("prev").putInt("slide", 0);
			NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_M1GARAND_ACTIONBACK));
		}
	}

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
		
		CompoundNBT baseTag = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(IItemData::getItemData).orElse(new CompoundNBT());

		if (nbt.getInt("slide") < 4 && flag && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
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
			if (nbt.getInt("slide") == 0) NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_M1GARAND_ACTIONBACK));
			if (nbt.getInt("slide") < 4 && nbt.getInt("check") < 4) {
				nbt.putInt("slide", nbt.getInt("slide") + 1);
			}
		}
		if (flag && KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.RemoveBullet) && nbt.getInt("slide") == 4 && nbt.getList("bullets", 8).size() > 0) {
			if (nbt.getList("bullets", 8).size() != 8 || rand.nextInt(3) != 0) {
				nbt.putBoolean("AutoSlideLock", false);
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
			if (nbt.getList("bullets", 8).size() == 0) {
				if (!nbt.getBoolean("AutoSlideLock") && !nbt.getString("mag").isEmpty() && nbt.getCompound("prev").getInt("slide") < 4) {
					CompoundNBT magNBT = new CompoundNBT();

					magNBT.put("bullets", nbt.getList("bullets", 8));
					magNBT.putString("UUID", nbt.getString("mag"));
					nbt.putInt("bullets", 0);
					nbt.putString("mag", "");

					ItemStack mag = new ItemStack(gun.mag.get());

					mag.getOrCreateTag().putString("UUID", magNBT.getString("UUID"));
					baseTag.put(magNBT.getString("UUID"), magNBT);

					NetworkHandler.sendToServer(new MessageEject(mag));

					NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_M1GARAND_CLIPEJECT));
				}
				nbt.putBoolean("AutoSlideLock", true);
			}
			if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet) && !nbt.getString("mag").isEmpty() && !nbt.getCompound("prev").getString("mag").isEmpty() && nbt.getList("bullets", 8).size() < 8) {
				ItemStack ammo = gun.mag.get().findAmmo(player);

				if (!ammo.isEmpty()) {
					nbt.getList("bullets", 8).add(new StringNBT(ammo.getItem().getRegistryName().toString()));
					NetworkHandler.sendToServer(new MessageAddToInventory(ammo, -1));
					if (nbt.getList("bullets", 8).size() < 8) {
						nbt.putBoolean("AutoSlideLock", false);
					}
					else {
						nbt.putBoolean("AutoSlideLock", true);
					}
				}
			}
		}
		if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock)) {
				nbt.putBoolean("AutoSlideLock", false);
			}
			if (nbt.getInt("slide") == 4 && nbt.getBoolean("AutoSlideLock")) {
				nbt.putInt("slide", 4);
				if (flag && KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.RemoveBullet)) NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_M1GARAND_ACTIONCATCH));
			}
			if (nbt.getInt("slide") > 0 && !nbt.getBoolean("AutoSlideLock")) {
				if (nbt.getList("bullets", 8).size() > 0 && nbt.getString("BulletChambered").isEmpty()) {
					nbt.putString("BulletChambered", nbt.getList("bullets", 8).getString(nbt.getList("bullets", 8).size() - 1));
					nbt.getList("bullets", 8).remove(nbt.getList("bullets", 8).size() - 1);
				}
				nbt.putInt("slide", 0);
				if (!nbt.getBoolean("fired")) NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_M1GARAND_ACTIONFORWARD));
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
		//System.out.println(nbt);
	}

}

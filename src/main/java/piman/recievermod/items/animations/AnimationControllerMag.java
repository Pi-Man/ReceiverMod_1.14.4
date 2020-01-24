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
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			if (worldIn.isRemote) {
				worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
					CompoundNBT baseTag = itemData.getItemData();
					if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet) && nbt.getString("mag").isEmpty()) {
						System.out.println("Add Clip Pressed");

						int clipslot = gun.findClip(player);

						if (clipslot != -1) {
							ItemStack clip = player.inventory.getStackInSlot(clipslot);
							//System.out.println("Clip Found: " + clip + clip.getTagCompound());
							NetworkHandler.sendToServer(new MessageAddToInventory(clip, -1, clipslot));
							CompoundNBT clipTag = baseTag.getCompound(clip.getOrCreateTag().getString("UUID"));
							nbt.put("Bullets", clipTag.getList("Bullets", 8));
							nbt.putString("mag", clipTag.getString("UUID"));
							NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGIN));
						}
					}
					if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveClip)) {

						if (!nbt.getString("mag").isEmpty()) {
							CompoundNBT clipNBT = new CompoundNBT();

							clipNBT.put("Bullets", nbt.getList("Bullets", 8));
							clipNBT.putString("UUID", nbt.getString("mag"));
							nbt.putInt("Bullets", 0);
							nbt.putString("mag", "");

							ItemStack clip = new ItemStack(gun.mag);

							clip.getOrCreateTag().putString("UUID", clipNBT.getString("UUID"));
							baseTag.put(clipNBT.getString("UUID"), clipNBT);

							NetworkHandler.sendToServer(new MessageAddToInventory(clip, 1, player.inventory.getSizeInventory() - 1));

							NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_MAGOUT));
						} else if (gun.isClip(player.getHeldItemOffhand())) {
							TreeMap<Integer, Pair<ItemStack, Integer>> mags = new TreeMap<Integer, Pair<ItemStack, Integer>>();
							for (int i = 0; i < player.inventory.getSizeInventory() - 1; i++) {
								ItemStack itemstack = player.inventory.getStackInSlot(i);
								if (gun.isClip(itemstack)) {
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
				});
			}
		}
	}

}

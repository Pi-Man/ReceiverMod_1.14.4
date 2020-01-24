package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.FlashHandler;
import piman.recievermod.util.SoundsHandler;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerShoot implements IAnimationController {
	
	public final Condition condition;
	
	public AnimationControllerShoot(Condition condition) {
		this.condition = condition;
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.booleanProperty("fired", false));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			nbt.putBoolean("fired", false);
			if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && nbt.getBoolean("hammer") && (!nbt.getBoolean("held") || nbt.getBoolean("Auto"))) {
				//boolean flag = gun.Shoot(nbt, (LivingEntity) entityIn, ModConfig.glockdamage, nbt.getBoolean("ads") ? 0 : 10, 0, 1, condition.apply(nbt));
				boolean flag = gun.Shoot(nbt, (LivingEntity) entityIn, 10, nbt.getBoolean("ads") ? 0 : 10, 0, 1, condition.apply(nbt));
				if (flag) {
					if (!player.isCreative()) {
						//NetworkHandler.sendToServer(new MessageEject(new ItemStack(ModItems.BULLET9MMCASING)));
					}
					if (nbt.getBoolean("Auto") && !KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.LeftClick)) {
						FlashHandler.CreateFlash(new BlockPos(player.posX, player.posY + 1, player.posZ), player.dimension.getId(), 1);
					}
					else {
						FlashHandler.CreateFlash(new BlockPos(player.posX, player.posY + 1, player.posZ), player.dimension.getId(), 10);
					}
					nbt.putBoolean("fired", true);
				}
				else {
					NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_GLOCK_DRY));
				}
				nbt.putBoolean("held", true);
			}
			else if (KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.LeftClick)) {
				nbt.putBoolean("held", false);
			}
		}
	}

	public interface Condition {
		boolean apply(CompoundNBT nbt);
	}
	
}



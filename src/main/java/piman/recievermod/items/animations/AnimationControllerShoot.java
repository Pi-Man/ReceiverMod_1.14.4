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
import piman.recievermod.util.CapUtils;
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
		
		list.add(new ItemPropertyWrapper("fired", new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
				if (worldIn == null) {
					worldIn = Minecraft.getInstance().world;
				}
				
				if (worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
					return 0.0F;
				}
				
				CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldnbt = nbt.getCompound("prev");
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
	            float j = (oldnbt.getBoolean("fired") ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean("fired") ? 1.0F : 0.0F) * pt;
				
				return j;
			}
		}));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			nbt.putBoolean("fired", false);
			if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && nbt.getBoolean("hammer") && (!nbt.getBoolean("held") || nbt.getBoolean("Auto"))) {
				//boolean flag = gun.Shoot(nbt, (LivingEntity) entityIn, ModConfig.glockdamage, nbt.getBoolean("ADS") ? 0 : 10, 0, 1, condition.apply(nbt));
				boolean flag = gun.Shoot(nbt, (LivingEntity) entityIn, 10, nbt.getBoolean("ADS") ? 0 : 10, 0, 1, condition.apply(nbt));
				if (flag) {
					if (!player.isCreative()) {
						//NetworkHandler.sendToServer(new MessageEject(new ItemStack(ModItems.BULLET9MMCASING)));
					}
					if (nbt.getBoolean("Auto") && !KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.LeftClick)) {
						FlashHandler.CreateFlash(new BlockPos(player.posX, player.posY + 1, player.posZ), player.dimension.getId(), 1);
					}
					else {
						FlashHandler.CreateFlash(new BlockPos(player.posX, player.posY + 1, player.posZ), player.dimension.getId(), 2);
					}
					nbt.putBoolean("fired", true);
				}
				else {
					NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.Sounds.GLOCK_DRY));
				}
				nbt.putBoolean("held", true);
			}
			else if (KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.LeftClick)) {
				nbt.putBoolean("held", false);
			}
		}
	}

	public static interface Condition {
		public boolean apply(CompoundNBT nbt);
	}
	
}



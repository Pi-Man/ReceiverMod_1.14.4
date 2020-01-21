package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.util.CapUtils;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerADS implements IAnimationController {

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list= new ArrayList<>();
		
		list.add(new ItemPropertyWrapper("ads", new IItemPropertyGetter() {
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
				
	            float j = (oldnbt.getBoolean("ADS") ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean("ADS") ? 1.0F : 0.0F) * pt;
				
				return j;
			}
		}));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		nbt.putBoolean("ADS", KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RightClick));
	}

}

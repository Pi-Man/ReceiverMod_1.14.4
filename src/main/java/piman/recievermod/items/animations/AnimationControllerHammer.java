package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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

public class AnimationControllerHammer implements IAnimationController {
	
	private final boolean doubleAction;
	
	public AnimationControllerHammer(boolean doubleAction) {
		this.doubleAction = doubleAction;
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(new ItemPropertyWrapper("hammer", new IItemPropertyGetter() {
	        @Override
	        public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
	        {
	        	if (worldIn == null) {
	        		worldIn = Minecraft.getInstance().world;
	        	}
	        	
	        	if (worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
	        		return 0.0F;
	        	}
	        	CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldnbt = nbt.getCompound("prev");
				
				if (oldnbt == null) {
					return 0.0F;
				}
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
	            float j = (oldnbt.getBoolean("hammer") ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean("hammer") ? 1.0F : 0.0F) * pt;
	                        
	            return j;
	        }
	    }));
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (nbt.getBoolean("hammer") && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && (!nbt.getBoolean("held") || nbt.getBoolean("Auto"))) {
			nbt.putBoolean("hammer", false);
		}
		else if ((doubleAction && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.LeftClick)) || KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Safety)) {
			nbt.putBoolean("hammer", true);
		}
	}

}

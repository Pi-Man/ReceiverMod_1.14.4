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
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerADS implements IAnimationController {

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list= new ArrayList<>();
		
		list.add(IAnimationController.booleanProperty("ads", false));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		boolean flag = player.getHeldItemMainhand().equals(stack);
		nbt.putBoolean("ads", flag && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RightClick));
	}

}

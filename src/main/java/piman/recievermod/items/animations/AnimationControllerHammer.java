package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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

public class AnimationControllerHammer implements IAnimationController {
	
	private final boolean doubleAction;
	
	public AnimationControllerHammer(boolean doubleAction) {
		this.doubleAction = doubleAction;
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.booleanProperty("hammer", true));

		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		boolean flag = player.getHeldItemMainhand().equals(stack);
		if (nbt.getBoolean("hammer") && flag && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && (!nbt.getBoolean("held") || nbt.getBoolean("Auto"))) {
			nbt.putBoolean("hammer", false);
		}
		else if ((doubleAction && flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.LeftClick)) || (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Safety))) {
			nbt.putBoolean("hammer", true);
		}
	}

}

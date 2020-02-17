package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;

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

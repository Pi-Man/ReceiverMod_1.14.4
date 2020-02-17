package piman.recievermod.items.animations;

import java.util.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;

public class AnimationControllerFireSelect implements IAnimationController {
	
	private final LinkedHashSet<Modes> options;
	
	public AnimationControllerFireSelect(Modes... modes) {
		options = new LinkedHashSet<>(Arrays.asList(modes));
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.integerProperty("mode", true));
		list.add(IAnimationController.integerProperty("modeid", true));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		boolean flag = player.getHeldItemMainhand().equals(stack);
		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Safety)) {
			nbt.putInt("modeid", (nbt.getInt("mode") + 1)%options.size());
			nbt.putInt("mode", options.toArray(new Modes[0])[nbt.getInt("modeid")].ordinal());
		}
	}
	
	public enum Modes {
		SAFETY,
		SEMI,
		BURST,
		AUTO;
	}

}

package piman.recievermod.items.animations;

import java.util.*;

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

import javax.annotation.Nonnull;

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

package piman.recievermod.items.animations;

import java.util.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;

public class AnimationControllerFireSelect implements IAnimationController {
	
	private final LinkedHashSet<Modes> options;
	private final ItemGun itemgun;
	private final int maxBurstCount;
	
	public AnimationControllerFireSelect(ItemGun itemgun, Modes... modes) {
		options = new LinkedHashSet<>(Arrays.asList(modes));
		if (options.contains(Modes.BURST)) {
			throw new IllegalArgumentException("Specified Burst Mode without specifying Burst Count");
		}
		this.itemgun = itemgun;
		MinecraftForge.EVENT_BUS.register(this);
		maxBurstCount = 0;
	}

	public AnimationControllerFireSelect(ItemGun itemgun, int maxBurstCount, Modes... modes) {
		options = new LinkedHashSet<>(Arrays.asList(modes));
		if (!options.contains(Modes.BURST)) {
			throw new IllegalArgumentException("Specified Burst Count without specifying Burst Mode");
		}
		this.itemgun = itemgun;
		MinecraftForge.EVENT_BUS.register(this);
		this.maxBurstCount = maxBurstCount;
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(IAnimationController.integerProperty("mode", true));
		list.add(IAnimationController.integerProperty("modeindex", true));
		if (this.options.contains(Modes.BURST)) {
			list.add(IAnimationController.integerProperty("burstcount", true));
		}
		
		return list;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHammerDownEvent(AnimationControllerHammer.HammerDownEvent event) {
		if (event.getGun() == this.itemgun) {
			Modes mode = Modes.values()[event.getNbt().getInt("mode")];
			if (mode == Modes.SAFETY) {
				event.setCanceled(true);
			}
			else if (mode == Modes.SEMI) {
				if (event.getNbt().getBoolean("held")) {
					event.setCanceled(true);
				}
			}
			else if (mode == Modes.BURST) {
				int burstCount = event.getNbt().getInt("burstcount");
				if (event.getNbt().getBoolean("held")) {
					if (burstCount >= maxBurstCount - 1) {
						event.setCanceled(true);
					}
					else {
						event.getNbt().putInt("burstcount", burstCount + 1);
					}
				}
				else {
					event.getNbt().putInt("burstcount", 0);
				}
			}
		}
	}

	@Override
	public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		boolean flag = player.getHeldItemMainhand().equals(stack);
		if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Safety)) {
			nbt.putInt("modeindex", (nbt.getInt("modeindex") + 1)%options.size());
			nbt.putInt("mode", options.toArray(new Modes[0])[nbt.getInt("modeindex")].ordinal());
		}
	}
	
	public enum Modes {
		SEMI,
		BURST,
		AUTO,
		SAFETY
	}

}

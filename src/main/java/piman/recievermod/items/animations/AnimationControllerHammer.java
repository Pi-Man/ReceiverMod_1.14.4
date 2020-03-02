package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;

public class AnimationControllerHammer implements IAnimationController {
	
	private final boolean doubleAction;
	private final ItemGun itemGun;
	
	public AnimationControllerHammer(ItemGun itemGun, boolean doubleAction) {
		this.doubleAction = doubleAction;
		this.itemGun = itemGun;
		MinecraftForge.EVENT_BUS.register(this);
	}

	private boolean onHammerHit(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		HammerHitEvent event = new HammerHitEvent(stack, worldIn, player, itemSlot, isSelected, nbt, gun);
		return !MinecraftForge.EVENT_BUS.post(event);
	}

	private boolean onHammerDown(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		HammerDownEvent event = new HammerDownEvent(stack, worldIn, player, itemSlot, isSelected, nbt, gun);
		return !MinecraftForge.EVENT_BUS.post(event);
	}

	@SubscribeEvent
	public void onShootEvent(AnimationControllerShoot.ShootEvent.Post event) {
		if (event.getGun() == this.itemGun) {
			event.getNbt().putBoolean("held", true);
		}
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

		if (flag) {
			if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Hammer)) {
				nbt.putBoolean("hammer", true);
			}
			if (KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.Hammer) && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && onHammerDown(stack, worldIn, player, itemSlot, isSelected, nbt, gun)) {
				nbt.putBoolean("hammer", false);
			}
			if (nbt.getBoolean("hammer") && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick) && !KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.Hammer) && onHammerDown(stack, worldIn, player, itemSlot, isSelected, nbt, gun) && onHammerHit(stack, worldIn, player, itemSlot, isSelected, nbt, gun)) {
				nbt.putBoolean("hammer", false);
			}
			else if (doubleAction && !nbt.getBoolean("hammer") && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.LeftClick)) {
				nbt.putBoolean("hammer", true);
			}
			if (KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.LeftClick)) {
				nbt.putBoolean("held", false);
			}
		}

	}

	public static class HammerHitEvent extends AnimationEvent {
		public HammerHitEvent(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
			super(stack, world, player, itemSlot, isSelected, nbt, gun);
		}
	}

	public static class HammerDownEvent extends AnimationEvent {
		public HammerDownEvent(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
			super(stack, world, player, itemSlot, isSelected, nbt, gun);
		}
	}

}

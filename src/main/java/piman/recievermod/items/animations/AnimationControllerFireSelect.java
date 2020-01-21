package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import piman.recievermod.util.CapUtils;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerFireSelect implements IAnimationController {
	
	private final HashSet<Modes> options;
	
	public AnimationControllerFireSelect(Modes... modes) {
		options = new HashSet<>(Arrays.asList(modes));
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(new ItemPropertyWrapper("mode", new IItemPropertyGetter() {
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
				
				Modes[] modes = options.toArray(new Modes[0]);
				
				int newVal = modes[nbt.getInt("mode")].ordinal();
				int oldVal = modes[oldnbt.getInt("mode")].ordinal();
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
				float f = (newVal - oldVal) * pt + oldVal;
				
				return f;
			}
		}));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.Safety)) {
				nbt.putInt("mode", (nbt.getInt("mode") + 1)%options.size());
			}
		}
	}
	
	public enum Modes {
		SAFETY,
		SEMI,
		AUTO;
	}

}

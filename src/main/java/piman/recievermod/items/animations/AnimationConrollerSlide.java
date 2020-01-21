package piman.recievermod.items.animations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageEject;
import piman.recievermod.util.CapUtils;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationConrollerSlide implements IAnimationController {

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(new ItemPropertyWrapper("slide", new IItemPropertyGetter()
	    {
	        @Override
			@OnlyIn(Dist.CLIENT)
	        public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
	        {
	        	if (worldIn == null) {
	        		worldIn = Minecraft.getInstance().world;
	        	}

				LazyOptional<CompoundNBT> optional = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(IItemData::getItemData);
	        	
	        	if (worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
	        		return 0.0F;
	        	}
	        	CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldnbt = nbt.getCompound("prev");
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
				int oldval = oldnbt.getInt("SlideFrame") == 5 ? 2 : oldnbt.getInt("SlideFrame");
				
				int newval = nbt.getInt("SlideFrame") == 5 ? 2 : nbt.getInt("SlideFrame");
				
	            float j = oldval * (1 - pt) + newval * pt;
	                        
	            return j / 10.0F;
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("check", new IItemPropertyGetter()
	    {
	        @Override
			@OnlyIn(Dist.CLIENT)
	        public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
	        {
	        	if (entityIn == null || worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
	        		return 0.0F;
	        	}
	        	CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
	        	CompoundNBT oldnbt = nbt.getCompound("prev");
	        	
	        	float oldval = oldnbt.getInt("SlideFrame") == 5 ? 0.3F : oldnbt.getInt("SlideFrame") < 3 ? oldnbt.getInt("SlideFrame") / 10.0F : 0.0F;
	        	
	        	float newval = nbt.getInt("SlideFrame") == 5 ? 0.3F : nbt.getInt("SlideFrame") < 3 ? nbt.getInt("SlideFrame") / 10.0F : 0.0F;
	        	
	        	float pt = RenderPartialTickHandler.renderPartialTick;
	        	
	        	if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock)) {
	        		newval = 0F;
	        	}
	        	if (!(KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) || KeyInputHandler.isKeyUnpressed(KeyInputHandler.KeyPresses.SlideLock) && !KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock))) {
	        		oldval = 0F;
	        	}
	            return (1 - pt) * oldval + pt * newval;
	        }
	    }));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		if (nbt.getBoolean("fired")) {
			nbt.putInt("SlideFrame", 4);
		}
		if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if (nbt.getInt("SlideFrame") < 2) {
				nbt.putInt("SlideFrame", nbt.getInt("SlideFrame") + 1);
			}
			else if ((nbt.getInt("SlideFrame") == 2 || nbt.getInt("SlideFrame") == 5) && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock)) {
				//System.out.println("Half Lock");
				nbt.putInt("SlideFrame", 5);
				nbt.getCompound("prev").putInt("SlideFrame", 5);
			}
			else if (nbt.getInt("SlideFrame") == 5) {
				nbt.putInt("SlideFrame", 2);
			}
			else if (nbt.getInt("SlideFrame") < 4) {
				nbt.putInt("SlideFrame", nbt.getInt("SlideFrame") + 1);
			}
		}
		if (nbt.getInt("SlideFrame") == 4) {
			nbt.putBoolean("hammer", true);
			if (nbt.getCompound("prev").getInt("SlideFrame") < 4) {
				if (!nbt.getString("BulletChambered").isEmpty()) {
					NetworkHandler.sendToServer(new MessageEject(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("BulletChambered"))))));
					nbt.putString("BulletChambered", "");
				}
			}
		}
		if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.RemoveBullet)) {
			if (nbt.getInt("SlideFrame") == 5) {
				nbt.putInt("SlideFrame", 2);
			}
			if (nbt.getInt("SlideFrame") == 4) {
				if (!KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) && nbt.getString("BulletChambered").isEmpty() && !nbt.getString("mag").isEmpty() && nbt.getList("Bullets", 8).size() == 0) {
					nbt.putBoolean("AutoSlideLock", true);
				}
				else {
					nbt.putBoolean("AutoSlideLock", false);
				}
				nbt.putInt("SlideFrame", 3);
			}
			if (nbt.getInt("SlideFrame") == 3 && (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.SlideLock) || nbt.getBoolean("AutoSlideLock")) && !KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock)) {
				//System.out.println("Full Lock");
				nbt.putBoolean("AutoSlideLock", true);
			}
			else {
				nbt.putInt("SlideFrame", 0);
			}
		}
		if (nbt.getInt("SlideFrame") == 0) {
			nbt.putBoolean("AutoSlideLock", false);
		}
		if (nbt.getBoolean("fired")) {
			nbt.putInt("SlideFrame", 4);
		}
		if (nbt.getCompound("prev").getInt("SlideFrame") > 2 && nbt.getCompound("prev").getInt("SlideFrame") < 5 && nbt.getList("Bullets", 8).size() > 0 && nbt.getString("BulletChambered").isEmpty() && (nbt.getInt("SlideFrame") < nbt.getCompound("prev").getInt("SlideFrame"))) {
			nbt.putString("BulletChambered", nbt.getList("Bullets", 8).getString(nbt.getList("Bullets", 8).size() - 1));
			nbt.getList("Bullets", 8).remove(nbt.getList("Bullets", 8).size() - 1);
			System.out.println("pickup");
		}
		//System.out.println(nbt);
	}

}

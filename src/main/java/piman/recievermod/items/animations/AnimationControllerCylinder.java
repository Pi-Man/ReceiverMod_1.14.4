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
import piman.recievermod.Main;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageAddToInventory;
import piman.recievermod.util.CapUtils;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public class AnimationControllerCylinder implements IAnimationController {
	
	private final double friction;
	
	public AnimationControllerCylinder(double friction) {
		this.friction = friction;
	}

	@Override
	public List<ItemPropertyWrapper> getProperties() {
		
		List<ItemPropertyWrapper> list = new ArrayList<>();
		
		list.add(new ItemPropertyWrapper("spin",new IItemPropertyGetter()
	    {
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
	        	
	        	float nextspin = 0;//(float) ((Item44Magnum) stack.getItem()).getSpin(nbt);
	        	
	        	float prevspin = 0;//(float) ((Item44Magnum) stack.getItem()).getSpin(oldnbt);

	        	float partialTicks = RenderPartialTickHandler.renderPartialTick;
	        	
	        	float spin = prevspin * (1 - partialTicks) + nextspin * partialTicks;
	        	        	
	        	return spin;
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("open", new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
	        	if (worldIn == null) {
	        		worldIn = Minecraft.getInstance().world;
	        	}
	        	
	        	if (worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
	        		return 0.0F;
	        	}
	        	CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldnbt = (CompoundNBT) nbt.get("prev");
				
				if (oldnbt == null) {
					return 0.0F;
				}
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
	            float j = (oldnbt.getBoolean("open") ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean("open") ? 1.0F : 0.0F) * pt;
	                        
	            return j;
			}
		}));
	    
	    list.add(new ItemPropertyWrapper("eject", new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
	        	if (worldIn == null) {
	        		worldIn = Minecraft.getInstance().world;
	        	}
	        	
	        	if (worldIn == null || !CapUtils.hasCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTag()) {
	        		return 0.0F;
	        	}
	        	CompoundNBT nbt = CapUtils.getCap(worldIn, ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldnbt = (CompoundNBT) nbt.get("prev");
				
				if (oldnbt == null) {
					return 0.0F;
				}
				
				float pt = RenderPartialTickHandler.renderPartialTick;
				
	            float j = (oldnbt.getBoolean("eject") ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean("eject") ? 1.0F : 0.0F) * pt;
	                        
	            return j;
			}
		}));
	    
		list.add(new ItemPropertyWrapper("bullet1", new IItemPropertyGetter() {
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
	        	
	        	return nbt.getInt("bullet1");
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("bullet2", new IItemPropertyGetter() {
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
	        	
	        	return nbt.getInt("bullet2");
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("bullet3", new IItemPropertyGetter()
	    {
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
	        	
	        	return nbt.getInt("bullet3");
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("bullet4", new IItemPropertyGetter()
	    {
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
	        	
	        	return nbt.getInt("bullet4");
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("bullet5", new IItemPropertyGetter()
	    {
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
	        	
	        	return nbt.getInt("bullet5");
	        }
	    }));
	    
	    list.add(new ItemPropertyWrapper("bullet6", new IItemPropertyGetter()
	    {
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
	        	
	        	return nbt.getInt("bullet6");
	        }
	    }));
		
		return list;
	}

	@Override
	public void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
		double theta = nbt.getDouble("theta");
		double dtheta = nbt.getDouble("dtheta");
		double prevtheta = nbt.getCompound("prev").getDouble("theta");
		double prevdtheta = nbt.getCompound("prev").getDouble("dtheta");
					
		if (entityIn instanceof PlayerEntity) {
		
			PlayerEntity player = (PlayerEntity) entityIn;
			
			if (stack.equals(player.getHeldItemMainhand())) {
												
				if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.Shift)) {
					dtheta += KeyInputHandler.getScroll();
				}
				
				KeyInputHandler.cancleScroll(KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.Shift));
				
				if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveClip)) {
					nbt.putBoolean("open", !nbt.getBoolean("open"));
				}

				{
					int n = (int) -Math.round(theta) + 2;
					
					while (n < 1) {
						n += 6;
					}
					
					while (n > 6) {
						n -= 6;
					}
					
					if (nbt.getString("BulletChambered").equals(gun.casing.getRegistryName().toString())) {
						setBullet(n, 2, nbt);
					}
					
					if (getBullet(n, nbt) == 1) {
						nbt.putString("BulletChambered", gun.ammo.getRegistryName().toString());
					}
					else {
						nbt.putString("BulletChambered", "");
					}
				}

				if (nbt.getBoolean("hammer") && KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.LeftClick)) {
					theta += 1;
				}
				
				if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.AddBullet) && nbt.getBoolean("open")) {
					
					int k = gun.findAmmo(player);
					
					if (k != -1) {
					
						int n = (int) -Math.round(theta) + 2;
						
						while (n < 1) {
							n += 6;
						}
						
						while (n > 6) {
							n -= 6;
						}
						
						int i;
						
						for (i = 0; getBullet(n--, nbt) != 0 && i < 6; i++) {
							if (n < 1) {
								n += 6;
							}
						};
						if (i < 6) {
							setBullet(n + 1, 1, nbt);
							ItemStack bullet = player.inventory.getStackInSlot(k);
							NetworkHandler.sendToServer(new MessageAddToInventory(bullet, -1, k));
						}
					}
				}
				
				if (KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveBullet) && nbt.getBoolean("open")) {
					int n = (int) -Math.round(theta) + 2;
					
					while (n < 1) {
						n += 6;
					}
					
					while (n > 6) {
						n -= 6;
					}
					
					int i;
					
					for (i = 0; getBullet(n--, nbt) == 0 && i < 6; i++) {
						if (n < 1) {
							n += 6;
						}
					};
					
					if (i < 6) {
						if (getBullet(n + 1, nbt) == 1) {
							NetworkHandler.sendToServer(new MessageAddToInventory(gun.ammo,  1));
						}
						else {
							NetworkHandler.sendToServer(new MessageAddToInventory(gun.casing,  1));
						}
						setBullet(n + 1, 0, nbt);
					}
				}
			}
		}
		
		double b = Math.sqrt(Math.abs(2*friction*dtheta));
		
		double velocity = b - friction/2;
		
		if (b/friction < 1) {
			dtheta = 0;
			theta -= prevdtheta;
			theta = Math.round(theta);
			
			if (Math.abs(theta) >=6) {
				prevtheta -= 6* Main.sign(theta);
				theta -= 6*Main.sign(theta);
			}
			
		}
		else {
			dtheta -= velocity * Main.sign(dtheta);
			theta -= velocity * Main.sign(dtheta);
		}
					
		nbt.putDouble("theta", theta);
		nbt.putDouble("dtheta", dtheta);
		nbt.getCompound("prev").putDouble("theta", prevtheta);
		nbt.getCompound("prev").putDouble("dtheta", prevdtheta);
	}
	
	public int getBullet(int n, CompoundNBT nbt) {
		
		switch(n) {
			case 1:
				return nbt.getInt("bullet1");
			case 2:
				return nbt.getInt("bullet2");
			case 3:
				return nbt.getInt("bullet3");
			case 4:
				return nbt.getInt("bullet4");
			case 5:
				return nbt.getInt("bullet5");
			case 6:
				return nbt.getInt("bullet6");
			default:
				return 0;
		}
			
	}
	
	public void setBullet(int n, int flag, CompoundNBT nbt) {
		
		switch(n) {
			case 1:
				nbt.putInt("bullet1", flag);
				break;
			case 2:
				nbt.putInt("bullet2", flag);
				break;
			case 3:
				nbt.putInt("bullet3", flag);
				break;
			case 4:
				nbt.putInt("bullet4", flag);
				break;
			case 5:
				nbt.putInt("bullet5", flag);
				break;
			case 6:
				nbt.putInt("bullet6", flag);
				break;
		}
	}

}

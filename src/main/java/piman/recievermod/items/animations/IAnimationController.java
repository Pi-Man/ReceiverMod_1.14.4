package piman.recievermod.items.animations;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

public interface IAnimationController {
	
	List<ItemPropertyWrapper> getProperties();
	
	void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun);
	
	public class AnimationEvent extends Event {
		
		private final ItemStack stack;
		private final World world;
		private final PlayerEntity player;
		private final int itemSlot;
		private final boolean isSelected;
		private final CompoundNBT nbt;
		private final ItemGun gun;
		
		public AnimationEvent(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {
			this.stack = stack;
			this.world = world;
			this.player = player;
			this.itemSlot = itemSlot;
			this.isSelected = isSelected;
			this.nbt = nbt;
			this.gun = gun;
		}
		
		public ItemStack getStack() {
			return stack;
		}
		
		public World getWorld() {
			return world;
		}
		
		public PlayerEntity getPlayer() {
			return player;
		}
		
		public int getItemSlot() {
			return itemSlot;
		}
		
		public boolean isSelected() {
			return isSelected;
		}
		
		public CompoundNBT getNbt() {
			return nbt;
		}
		
		public ItemGun getGun() {
			return gun;
		}
		
		@Override
		public boolean isCancelable() {
			return true;
		}
		
	}

	static ItemPropertyWrapper booleanProperty(String name, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null) {
				return 0F;
			}

			return worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(iItemData ->  {

				CompoundNBT nbt = iItemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldNBT = nbt.getCompound("prev");

				float pt = RenderPartialTickHandler.renderPartialTick;

				return (oldNBT.getBoolean(name) ? 1F : 0F) * (1 - pt) + (nbt.getBoolean(name) ? 1F : 0F) * pt;

			}).orElse(0F);
		});
	}

	static ItemPropertyWrapper stringProperty(String name, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null) {
				return 0F;
			}

			return worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(iItemData ->  {

				CompoundNBT nbt = iItemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldNBT = nbt.getCompound("prev");

				float pt = RenderPartialTickHandler.renderPartialTick;

				return (oldNBT.getString(name).isEmpty() ? 0F : 1F) * (1 - pt) + (nbt.getString(name).isEmpty() ? 0F : 1F) * pt;

			}).orElse(0F);
		});
	}

	static ItemPropertyWrapper floatProperty(String name, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null) {
				return 0F;
			}

			return worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(iItemData ->  {

				CompoundNBT nbt = iItemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldNBT = nbt.getCompound("prev");

				float pt = RenderPartialTickHandler.renderPartialTick;

				return oldNBT.getFloat(name) * (1 - pt) + nbt.getFloat(name) * pt;

			}).orElse(0F);
		});
	}

	static ItemPropertyWrapper integerProperty(String name, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null) {
				return 0F;
			}

			return worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(iItemData ->  {

				CompoundNBT nbt = iItemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldNBT = nbt.getCompound("prev");

				float pt = RenderPartialTickHandler.renderPartialTick;

				return oldNBT.getInt(name) * (1 - pt) + nbt.getInt(name) * pt;

			}).orElse(0F);
		});
	}
	
	static ItemPropertyWrapper listCountProperty(String name, int type, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null) {
				return 0F;
			}

			return worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).map(iItemData ->  {

				CompoundNBT nbt = iItemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
				CompoundNBT oldNBT = nbt.getCompound("prev");

				float pt = RenderPartialTickHandler.renderPartialTick;

				return oldNBT.getList(name, type).size() * (1.0f - pt) + nbt.getList(name, type).size() * pt;

			}).orElse(0F);
		});
	}

}

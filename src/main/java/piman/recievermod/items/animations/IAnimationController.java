package piman.recievermod.items.animations;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.util.handlers.RenderPartialTickHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAnimationController {
	
	List<ItemPropertyWrapper> getProperties();
	
	void update(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun);

	static ItemPropertyWrapper booleanProperty(String name, boolean showInInvenotry) {
		return new ItemPropertyWrapper(name, (stack, worldIn, entity) -> {
			if (worldIn == null) {
				if (showInInvenotry) {
					worldIn = Minecraft.getInstance().world;
				}
			}

			if (!stack.hasTag() || worldIn == null || entity == null) {
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

			if (!stack.hasTag() || worldIn == null || entity == null) {
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

			if (!stack.hasTag() || worldIn == null || entity == null) {
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

			if (!stack.hasTag() || worldIn == null || entity == null) {
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

}

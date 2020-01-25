package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public interface IItemData {
	
	public void setItemData(CompoundNBT nbt);
	
	@Nonnull
    public CompoundNBT getItemData();

}

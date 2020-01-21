package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.CompoundNBT;

public interface IItemData {
	
	public void setItemData(CompoundNBT nbt);
	
	public CompoundNBT getItemData();

}

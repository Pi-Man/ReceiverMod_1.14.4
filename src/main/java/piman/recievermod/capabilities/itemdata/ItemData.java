package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.CompoundNBT;

public class ItemData implements IItemData {

	private CompoundNBT nbt = new CompoundNBT();
	
	@Override
	public void setItemData(CompoundNBT nbt) {
		this.nbt = nbt;
	}

	@Override
	public CompoundNBT getItemData() {
		return this.nbt;
	}

}

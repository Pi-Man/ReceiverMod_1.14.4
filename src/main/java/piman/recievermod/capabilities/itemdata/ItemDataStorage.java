package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ItemDataStorage implements IStorage<IItemData> {

	@Override
	public INBT writeNBT(Capability<IItemData> capability, IItemData instance, Direction side) {
		return instance.getItemData();
	}

	@Override
	public void readNBT(Capability<IItemData> capability, IItemData instance, Direction side, INBT nbt) {
		if (!nbt.toString().equals("{}")) {
			instance.setItemData((CompoundNBT) nbt);
		}
	}

}

package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class ItemDataProvider implements ICapabilitySerializable<INBT> {

	public static Capability<IItemData> ITEMDATA_CAP;

	@CapabilityInject(IItemData.class)
	public static void init(Capability<IItemData> capIn) {
		ITEMDATA_CAP = capIn;
	}

	private IItemData instance = new ItemData();

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
		return LazyOptional.of(() -> (T) instance);
	}

	@Override
	public INBT serializeNBT() {
		return ITEMDATA_CAP.getStorage().writeNBT(ITEMDATA_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		ITEMDATA_CAP.getStorage().readNBT(ITEMDATA_CAP, this.instance, null, nbt);
	}

}

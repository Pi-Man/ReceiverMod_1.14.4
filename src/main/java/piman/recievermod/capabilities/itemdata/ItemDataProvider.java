package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class ItemDataProvider implements ICapabilitySerializable<INBT> {

	@CapabilityInject(IItemData.class)
	public static Capability<IItemData> ITEMDATA_CAP;

	private LazyOptional<IItemData> instance = LazyOptional.of(ITEMDATA_CAP::getDefaultInstance);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
		return ITEMDATA_CAP.orEmpty(capability, instance);
	}

	@Override
	public INBT serializeNBT() {
		return ITEMDATA_CAP.getStorage().writeNBT(ITEMDATA_CAP, this.instance.orElse(ITEMDATA_CAP.getDefaultInstance()), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		ITEMDATA_CAP.getStorage().readNBT(ITEMDATA_CAP, this.instance.orElse(ITEMDATA_CAP.getDefaultInstance()), null, nbt);
	}

}

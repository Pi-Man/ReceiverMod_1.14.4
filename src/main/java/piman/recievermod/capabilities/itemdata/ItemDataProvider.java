package piman.recievermod.capabilities.itemdata;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;

public class ItemDataProvider implements ICapabilitySerializable<INBT> {

	@CapabilityInject (IItemData.class)
	public static final Capability<IItemData> ITEMDATA_CAP = null;
	
	private IItemData instance = ITEMDATA_CAP.getDefaultInstance();

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		return LazyOptional.of(new NonNullSupplier<T>() {
			@Nonnull
			@Override
			public T get() {
				return (T) instance;
			}
		});
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

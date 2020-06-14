package piman.recievermod.capabilities.iteminventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.inventory.AmmoContainerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemInventory implements ICapabilitySerializable<INBT> {

    private AmmoContainerInventory inventory;

    private LazyOptional<IItemHandler> optional = LazyOptional.of(() -> new InvWrapper(inventory));

    public ItemInventory(int size) {
        inventory = new AmmoContainerInventory(size);
    }

    /**
     * Retrieves the Optional handler for the capability requested on the specific side.
     * The return value <strong>CAN</strong> be the same for multiple faces.
     * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
     * be notified if the requested capability get lost.
     *
     * @param cap
     * @param side
     * @return The requested an optional holding the requested capability.
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return inventory.serialize();
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        inventory.deserialize((CompoundNBT) nbt);
    }

    public static CompoundNBT saveAllItems(CompoundNBT tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        ListNBT listnbt = new ListNBT();

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte)i);
                writeItemStack(compoundnbt, itemstack);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || saveEmpty) {
            tag.put("Items", listnbt);
        }

        return tag;
    }

    public static void loadAllItems(CompoundNBT tag, NonNullList<ItemStack> list) {
        ListNBT listnbt = tag.getList("Items", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 0 && j < list.size()) {
                list.set(j, readItemStack(compoundnbt));
            }
        }

    }

    public static CompoundNBT writeItemStack(CompoundNBT nbt, ItemStack stack) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        nbt.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        nbt.putInt("Count", stack.getCount());
        if (stack.getTag() != null) {
            nbt.put("tag", stack.getTag());
        }
        return nbt;
    }

    private static ItemStack readItemStack(CompoundNBT compound) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("id")));
        int count = compound.getInt("Count");
        ItemStack stack = new ItemStack(item, count);
        if (compound.contains("tag", 10)) {
            CompoundNBT tag = compound.getCompound("tag");
            item.updateItemStackNBT(compound);
            stack.setTag(tag);
        }


        if (item.isDamageable()) {
            stack.setDamage(stack.getDamage());
        }

        return stack;
    }
}

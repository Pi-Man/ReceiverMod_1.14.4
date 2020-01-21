package piman.recievermod.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import piman.recievermod.init.ModItems;

import java.util.UUID;

public class ItemBase extends Item {

    public ItemBase(Item.Properties properties) {
        super(properties.group(ItemGroup.REDSTONE));
        //ModItems.ITEMS.add(this);
    }

    public CompoundNBT checkNBTTags(ItemStack stack) {

        CompoundNBT nbt = stack.getOrCreateTag();

        if (!nbt.contains("UUID") && EffectiveSide.get() == LogicalSide.SERVER) {
            UUID id = MathHelper.getRandomUUID(this.random );
            nbt.putString("UUID", id.toString());
            System.out.println("Set UUID for: " + stack.getItem());
        }

        return nbt;
    }

}

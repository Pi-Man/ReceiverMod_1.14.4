package piman.recievermod.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import piman.recievermod.capabilities.iteminventory.ItemInventory;
import piman.recievermod.init.ModItemGroups;
import piman.recievermod.inventory.container.AmmoContainer;

import javax.annotation.Nullable;

public class ItemAmmoContainer extends Item {

    public ItemAmmoContainer(Properties properties) {
        super(properties.maxStackSize(1).group(ModItemGroups.TOOLS));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.openContainer(new SimpleNamedContainerProvider(AmmoContainer.getFactory(handIn), new StringTextComponent("ammo_container.key")));
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemInventory(1);
    }
}

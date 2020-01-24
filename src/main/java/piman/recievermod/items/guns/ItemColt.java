package piman.recievermod.items.guns;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.IItemInit;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.*;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageUpdateNBT;
import piman.recievermod.util.SoundsHandler;

public class ItemColt extends ItemGun implements IItemInit {

    public ItemColt(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void Init() {
        this.drift = 10;
        this.spreadX = 0.5;
        this.spreadY = 0.5;
        this.ammo = ModItems.BULLET45;
        this.casing = ModItems.BULLET45CASING;
        this.mag = ModItems._CLIP_COLT;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(nbt->nbt.getInt("slide") == 0));
        this.animationControllers.add(new AnimationControllerHammer(false));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationConrollerSlide());

        List<ItemPropertyWrapper> properties = new ArrayList<>();
        animationControllers.forEach(controller -> properties.addAll(controller.getProperties()));
        properties.forEach(property -> addPropertyOverride(property.getName(), property.getOverride()));
    }

    @Override
    public SoundEvent getShootSound() {
        return SoundsHandler.ITEM_1911_SHOT;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (entityIn instanceof PlayerEntity && ((PlayerEntity) entityIn).getHeldItemMainhand().equals(stack)) {

            if (worldIn.isRemote) {

                CompoundNBT tag = stack.getOrCreateTag();

                worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(cap -> {
                    CompoundNBT baseTag = cap.getItemData();

                    CompoundNBT nbt = baseTag.getCompound(tag.getString("UUID"));

                    CompoundNBT oldnbt = nbt.copy();
                    oldnbt.remove("prev");
                    nbt.put("prev", oldnbt);

                    animationControllers.forEach(controller -> controller.update(stack, worldIn, entityIn, itemSlot, isSelected, nbt, (ItemGun) stack.getItem()));

                    NetworkHandler.sendToServer(new MessageUpdateNBT(stack, itemSlot, nbt));
                });
            }
        }
    }

    @Override
    public float getDefaultZoomFactor(ItemStack stack) {
        return 0.9F;
    }
}

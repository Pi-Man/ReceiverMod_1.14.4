package piman.recievermod.items.guns;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemColt extends ItemGun {

    public ItemColt(Item.Properties properties) {
        super(properties);
        this.drift = 5;
        this.spreadX = 0.5;
        this.spreadY = 0.5;
        this.ammo = () -> ModItems.BULLET45;
        this.casing = () -> ModItems.BULLET45CASING;
        this.mag = () -> ModItems.COLT_MAG;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(this, nbt->nbt.getInt("slide") == 0));
        this.animationControllers.add(new AnimationControllerHammer(this, false));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationControllerSlide(this));
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SAFETY, AnimationControllerFireSelect.Modes.SEMI));

    }

    @Override
    public SoundEvent getShootSound() {
        return SoundsHandler.ITEM_1911_SHOT;
    }

    @Override
    public float getDefaultZoomFactor(ItemStack stack) {
        return 0.9F;
    }
}

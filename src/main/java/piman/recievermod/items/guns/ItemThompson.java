package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemThompson extends ItemGun {

    public ItemThompson(Properties properties) {
        super(properties);

        this.drift = 1;
        this.spreadX = 0.2;
        this.spreadY = 0.2;

        this.ammo = () -> ModItems.BULLET45;
        this.mag = () -> ModItems.THOMPSON_MAG;

        this.animationControllers.add(new AnimationControllerShoot(this, nbt -> !nbt.getBoolean("AutoSlideLock")));
        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SEMI, AnimationControllerFireSelect.Modes.AUTO));
        this.animationControllers.add(new AnimationControllerSlide(this));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationControllerHammer(this, false));
    }

    @Override
    public SoundEvent getShootSound() {
        return SoundsHandler.ITEM_1911_SHOT;
    }

    @Override
    public float getDefaultZoomFactor(ItemStack stack) {
        return 0.9f;
    }
}

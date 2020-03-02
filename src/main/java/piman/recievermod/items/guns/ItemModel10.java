package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemModel10 extends ItemGun {
    public ItemModel10(Properties properties) {
        super(properties);

        this.ammo = () -> ModItems.BULLET38SPECIAL;
        //TODO speedloader
        this.mag = null;

        this.drift = 5;
        this.spreadX = 0.5;
        this.spreadY = 0.5;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(this, nbt -> !nbt.getBoolean("open")));
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SEMI));
        this.animationControllers.add(new AnimationControllerHammer(this, true));
        this.animationControllers.add(new AnimationControllerCylinder(this, 0.01));
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

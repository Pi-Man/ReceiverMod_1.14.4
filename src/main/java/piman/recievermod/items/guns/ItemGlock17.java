package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemGlock17 extends ItemGun {

    public ItemGlock17(Properties properties) {
        super(properties);

        this.ammo = () -> ModItems.BULLET9MM;
        this.mag = () -> ModItems.GLOCK17_MAG;

        this.drift = 5;
        this.spreadX = 0.5;
        this.spreadY = 0.5;
        this.accuracy = 0.16f;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(this, nbt -> nbt.getInt("slide") == 0));
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SEMI, AnimationControllerFireSelect.Modes.AUTO));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationControllerHammer(this, true));
        this.animationControllers.add(new AnimationControllerSlide(this));

    }

    @Override
    public SoundEvent getShootSound() {
        return SoundsHandler.ITEM_GLOCK_SHOT;
    }

    @Override
    public float getDefaultZoomFactor(ItemStack stack) {
        return 0.9f;
    }
}

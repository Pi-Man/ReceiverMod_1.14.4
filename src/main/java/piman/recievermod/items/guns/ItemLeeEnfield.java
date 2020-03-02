package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemLeeEnfield extends ItemGun {
    public ItemLeeEnfield(Properties properties) {
        super(properties);
        this.ammo = () -> ModItems.BULLET30_06;
        this.mag = () -> ModItems.M1_CLIP;

        this.drift = 1;
        this.spreadX = 0.2;
        this.spreadY = 0.2;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerHammer(this, false));
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SAFETY, AnimationControllerFireSelect.Modes.SEMI));
        this.animationControllers.add(new AnimationControllerShoot(this, nbt -> nbt.getInt("boltup") == 0));
        this.animationControllers.add(new AnimationControllerBolt());
        this.animationControllers.add(new AnimationControllerMag());
    }

    @Override
    public SoundEvent getShootSound() {
        return SoundsHandler.ITEM_RIFLE_SHOT;
    }

    @Override
    public float getDefaultZoomFactor(ItemStack stack) {
        return 0.9f;
    }
}

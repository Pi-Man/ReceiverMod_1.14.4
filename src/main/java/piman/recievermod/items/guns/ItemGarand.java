package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

public class ItemGarand extends ItemGun {

	public ItemGarand(Properties properties) {
		super(properties);

        this.drift = 2;
        this.spreadX = 0.2;
        this.spreadY = 0.2;
        this.accuracy = 0.016f;

        this.ammo = () -> ModItems.BULLET30_06;
        this.casing = () -> ModItems.BULLET30_06CASING;
        this.mag = () -> ModItems.M1_CLIP;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(this, nbt->nbt.getInt("slide") == 0));
        this.animationControllers.add(new AnimationControllerFireSelect(this, AnimationControllerFireSelect.Modes.SAFETY, AnimationControllerFireSelect.Modes.SEMI));
        this.animationControllers.add(new AnimationControllerHammer(this, false));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationControllerGarandAction(this));

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

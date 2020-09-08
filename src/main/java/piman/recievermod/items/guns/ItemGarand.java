package piman.recievermod.items.guns;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.accessories.ItemAccessory;
import piman.recievermod.items.animations.*;
import piman.recievermod.util.SoundsHandler;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

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
    public boolean acceptsAccessory(ItemAccessory.AccessoryType type) {
        return type == ItemAccessory.AccessoryType.SCOPE;
    }

    @Override
    public Matrix4f getAccessoryTransform(ItemAccessory.AccessoryType type) {
	    Matrix4f m = new Matrix4f();
	    m.setIdentity();
	    switch (type) {
            case SCOPE:
                Vector3f translation = new Vector3f();
                translation.set(0f, 0.1875f, -0.1375f);
                m.setTranslation(translation);
        }
        return m;
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

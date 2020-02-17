package piman.recievermod.items.guns;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.AnimationControllerADS;
import piman.recievermod.items.animations.AnimationControllerGarandAction;
import piman.recievermod.items.animations.AnimationControllerHammer;
import piman.recievermod.items.animations.AnimationControllerMag;
import piman.recievermod.items.animations.AnimationControllerShoot;
import piman.recievermod.util.SoundsHandler;

public class ItemGarand extends ItemGun {

	public ItemGarand(Properties properties) {
		super(properties);
        this.drift = 2;
        this.spreadX = 0.2;
        this.spreadY = 0.2;
        this.ammo = () -> ModItems.BULLET45;
        this.casing = () -> ModItems.BULLET45CASING;
        this.mag = () -> ModItems.M1_CLIP;

        this.animationControllers.add(new AnimationControllerADS());
        this.animationControllers.add(new AnimationControllerShoot(nbt->nbt.getInt("slide") == 0));
        this.animationControllers.add(new AnimationControllerHammer(false));
        this.animationControllers.add(new AnimationControllerMag());
        this.animationControllers.add(new AnimationControllerGarandAction(this));

        List<ItemPropertyWrapper> itemProperties = new ArrayList<>();
        animationControllers.forEach(controller -> itemProperties.addAll(controller.getProperties()));
        itemProperties.forEach(property -> addPropertyOverride(property.getName(), property.getOverride()));
	}
	
	@Override
	public void Init() {

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

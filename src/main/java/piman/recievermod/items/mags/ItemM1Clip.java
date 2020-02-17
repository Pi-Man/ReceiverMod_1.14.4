package piman.recievermod.items.mags;

import piman.recievermod.init.ModItems;

public class ItemM1Clip extends ItemMag {

	public ItemM1Clip(Properties properties) {
		super(properties);
	}

	@Override
	public void Init() {
		this.ammo = ModItems.BULLET30_06;
		this.maxAmmo = 8;
	}

}

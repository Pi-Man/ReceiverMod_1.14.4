package piman.recievermod.items.mags;

import net.minecraft.item.Item;
import piman.recievermod.init.ModItems;

public class ItemMagColt extends ItemMag {

	public ItemMagColt(Item.Properties properties) {
		super(properties);
	}
	
	@Override
	public void Init() {
		this.ammo = ModItems.BULLET45;
		this.maxAmmo = 7;
	}
	
}

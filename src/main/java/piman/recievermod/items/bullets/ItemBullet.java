package piman.recievermod.items.bullets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import piman.recievermod.init.ModItemGroups;
import piman.recievermod.items.ItemBase;

public abstract class ItemBullet extends Item {
	
	private final String model;
	
	public ItemBullet(Item.Properties properties, String model) {
		super(properties.group(ModItemGroups.TOOLS));
		this.model = model;
	}
	
	public abstract void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life);
	
}

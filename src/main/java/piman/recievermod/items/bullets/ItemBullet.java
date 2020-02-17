package piman.recievermod.items.bullets;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import piman.recievermod.init.ModItemGroups;

public abstract class ItemBullet extends Item {
	
	protected final Supplier<Item> casing;

	public ItemBullet(Item.Properties properties, Supplier<Item> casing) {
		super(properties.group(ModItemGroups.TOOLS));
		this.casing = casing;
	}
	
	public Item getCasing() {
		return casing.get();
	}
	
	public abstract void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life);

}

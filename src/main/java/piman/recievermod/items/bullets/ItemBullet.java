package piman.recievermod.items.bullets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import piman.recievermod.init.ModItemGroups;
import piman.recievermod.items.ItemBase;

public abstract class ItemBullet extends Item {

	public ItemBullet(Item.Properties properties) {
		super(properties.group(ModItemGroups.TOOLS));
	}
	
	public abstract void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life);

}

package piman.recievermod.items.bullets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ItemBulletMediumCasing extends ItemBullet {

	public ItemBulletMediumCasing(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life) {}

}

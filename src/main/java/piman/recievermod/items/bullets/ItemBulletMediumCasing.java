package piman.recievermod.items.bullets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import piman.recievermod.Main;
import piman.recievermod.util.SoundsHandler;

public class ItemBulletMediumCasing extends ItemBullet {

	public ItemBulletMediumCasing(Item.Properties properties) {
		super(properties, null);
	}

	@Override
	public Item getCasing() {
		Main.LOGGER.warn("getting casing from casing");
		return this;
	}

	@Override
	public void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life) {
		world.playSound(player, player.posX, player.posY, player.posZ, SoundsHandler.ITEM_GLOCK_DRY, SoundCategory.PLAYERS, 1.0f, 1.0f);
	}

}

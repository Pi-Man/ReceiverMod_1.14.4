package piman.recievermod.items.bullets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import piman.recievermod.entities.EntityBullet;
import piman.recievermod.util.SoundsHandler;
import piman.recievermod.util.clientUtils.TransformationBuilder;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.function.Supplier;

public class ItemBulletLarge extends ItemBullet {

	private final float caliber;
	private final float velocity;

	public ItemBulletLarge(Properties properties, float caliber, float velocity, Supplier<Item> casing) {
		super(properties, casing);
		this.caliber = caliber;
		this.velocity = velocity;
	}

	@Override
	public void fire(World world, PlayerEntity player, float entityAccuracy, float gunAccuracy, int life) {
    	if (!world.isRemote) {
        	
    		EntityBullet bullet = new EntityBullet(player, world);
    		
			double a = Item.random.nextDouble() * (gunAccuracy + entityAccuracy) / 360 * Math.PI;
			double b = Item.random.nextDouble() * Math.PI * 2;
			
			float z = (float) (velocity * Math.cos(a));
			float y = (float) (velocity * Math.sin(a) * Math.sin(b));
			float x = (float) (velocity * Math.sin(a) * Math.cos(b));
    		
    		TransformationBuilder transform = new TransformationBuilder().add(null, new Vector3f(player.rotationPitch, player.rotationYaw, 0), null, null, 0);
    		
    		Matrix4f m1 = TRSRTransformation.blockCornerToCenter(transform.build()).getMatrixVec();
    		Matrix4f m2 = new Matrix4f();
    		m2.setColumn(0, x, y, z, 1);
    		m1.mul(m2);
    		float[] floats = new float[4];
    		m1.getColumn(0, floats);
    		
    		bullet.posX = player.posX;
    		bullet.posY = player.posY + player.getEyeHeight();
    		bullet.posZ = player.posZ;

    		bullet.setMotion(floats[0], floats[1], floats[2]);

    		bullet.setDamage(Math.round(caliber * caliber * velocity * velocity * 20/38));
    		
    		world.addEntity(bullet);
            
            world.playSound(null, player.posX, player.posY, player.posZ, SoundsHandler.ITEM_RIFLE_SHOT, SoundCategory.PLAYERS, 1, 1);
            
    	}
	}
}

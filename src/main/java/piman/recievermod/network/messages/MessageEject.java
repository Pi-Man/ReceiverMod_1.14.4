package piman.recievermod.network.messages;

import java.util.Random;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class MessageEject extends MessageBase<MessageEject> {

    private ItemStack itemstack;

    public MessageEject() {}

	public MessageEject (ItemStack itemstack) {
		this.itemstack = itemstack;
	}

	@Override
	public MessageEject fromBytes(PacketBuffer buf) {
		ItemStack itemstack = buf.readItemStack();
        return new MessageEject(itemstack);
    }

	@Override
	public void toBytes(MessageEject message, PacketBuffer buf) {
		buf.writeItemStack(message.itemstack);
	}

	@Override
	public void handleClientSide(MessageEject message, PlayerEntity player) {
		
	}

	@Override
	public void handleServerSide(MessageEject message, PlayerEntity player) {
		
		Random rand = new Random();
		
        if (!message.itemstack.isEmpty()){
            double d0 = player.posY - 0.3 + (double)player.getEyeHeight();
            ItemEntity entityitem = new ItemEntity(player.world, player.posX, d0, player.posZ, message.itemstack);
            entityitem.setPickupDelay(15);

            double motionX, motionY, motionZ;

            float f2 = 0.2F;
            motionZ = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * f2);
            motionX = -(double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * f2);
            motionY = (double)(-MathHelper.sin(-45 * 0.017453292F) * f2 + 0.1F);
            float f3 = rand.nextFloat() * ((float)Math.PI * 2F);
            f2 = 0.02F * rand.nextFloat();
            motionX += Math.cos((double)f3) * (double)f2;
            motionY += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
            motionZ += Math.sin((double)f3) * (double)f2;

            entityitem.setMotion(motionX, motionY, motionZ);

            player.world.addEntity(entityitem);
        }
    }
}

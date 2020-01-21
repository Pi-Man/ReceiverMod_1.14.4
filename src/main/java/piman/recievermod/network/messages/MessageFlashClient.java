package piman.recievermod.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import piman.recievermod.util.FlashHandler;

public class MessageFlashClient extends MessageBase<MessageFlashClient> {
		
	private boolean update;
	
	private int duration;
	
	private int dimension;
	
	private int x;
	
	private int y;
	 
	private int z;

	public MessageFlashClient(){}
	
	public MessageFlashClient(boolean update, int dimension) {
		this(update, BlockPos.ZERO, dimension, 0);
	}
	
	public MessageFlashClient(boolean update, BlockPos pos, int dimension, int duration) {
		this.update = update;
		this.duration = duration;
		this.dimension = dimension;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	@Override
	public MessageFlashClient fromBytes(PacketBuffer buf) {

		update = buf.readBoolean();
		duration = buf.readInt();
		dimension = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

		return new MessageFlashClient();
	}

	@Override
	public void toBytes(MessageFlashClient message, PacketBuffer buf) {
		
		buf.writeBoolean(message.update);
		buf.writeInt(message.duration);
		buf.writeInt(message.dimension);
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		
	}

	@Override
	public void handleClientSide(MessageFlashClient message, PlayerEntity player) {
				
		if (message.dimension == player.dimension.getId()) {
		
			if (message.update) {
				FlashHandler.Update(message.dimension);
				//System.out.println("Updating Flashes");
			}
			else {
				FlashHandler.AddFlash(new BlockPos(message.x, message.y, message.z), message.dimension, message.duration);
				//System.out.println("Creating Flash");
			}
		}
	}

	@Override
	public void handleServerSide(MessageFlashClient message, PlayerEntity player) {

	}

}

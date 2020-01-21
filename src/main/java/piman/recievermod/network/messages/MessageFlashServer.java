package piman.recievermod.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import piman.recievermod.network.NetworkHandler;

public class MessageFlashServer extends MessageBase<MessageFlashServer> {
			
	private int dimension;
	
	private int duration;
	
	private int x, y, z;

	public MessageFlashServer(){}
	
	public MessageFlashServer(BlockPos pos, int dimension, int duration) {
		this.dimension = dimension;
		this.duration = duration;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	@Override
	public MessageFlashServer fromBytes(PacketBuffer buf) {

		MessageFlashServer message = new MessageFlashServer();

		message.dimension = buf.readInt();
		message.duration = buf.readInt();
		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();

		return message;
	}

	@Override
	public void toBytes(MessageFlashServer message, PacketBuffer buf) {
		
		buf.writeInt(message.dimension);
		buf.writeInt(message.duration);
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		
	}

	@Override
	public void handleClientSide(MessageFlashServer message, PlayerEntity player) {
		
	}

	@Override
	public void handleServerSide(MessageFlashServer message, PlayerEntity player) {
		
		NetworkHandler.sendToAll(new MessageFlashClient(false, new BlockPos(message.x, message.y, message.z), message.dimension, message.duration));
		//System.out.println("Sending Flash Message");
	}

}

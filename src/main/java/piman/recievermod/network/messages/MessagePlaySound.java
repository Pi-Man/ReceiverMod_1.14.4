package piman.recievermod.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import piman.recievermod.util.SoundsHandler;

public class MessagePlaySound extends MessageBase<MessagePlaySound> {

	private int sound;
	
	public MessagePlaySound() {}
	
	public MessagePlaySound(int sound) {
		this.sound = sound;
	}
	
	public MessagePlaySound(SoundsHandler.Sounds sound) {
		this.sound = sound.ordinal();
	}
	
	@Override
	public MessagePlaySound fromBytes(PacketBuffer buf) {
		MessagePlaySound message = new MessagePlaySound();
		message.sound = buf.readInt();
		return message;
	}

	@Override
	public void toBytes(MessagePlaySound message, PacketBuffer buf) {
		buf.writeInt(message.sound);
	}

	@Override
	public void handleClientSide(MessagePlaySound message, PlayerEntity player) {}

	@Override
	public void handleServerSide(MessagePlaySound message, PlayerEntity player) {
		player.world.playSound(null, player.posX, player.posY, player.posZ, SoundsHandler.getSoundEvent(message.sound), SoundCategory.PLAYERS, 1, 1);
	}

}

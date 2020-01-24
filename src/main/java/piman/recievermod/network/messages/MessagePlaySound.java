package piman.recievermod.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class MessagePlaySound extends MessageBase<MessagePlaySound> {

	private String sound;
	
	public MessagePlaySound() {}

	public MessagePlaySound(SoundEvent sound) {
		this.sound = ForgeRegistries.SOUND_EVENTS.getKey(sound).toString();
	}
	
	@Override
	public MessagePlaySound fromBytes(PacketBuffer buf) {
		MessagePlaySound message = new MessagePlaySound();
		message.sound = buf.readString();
		return message;
	}

	@Override
	public void toBytes(MessagePlaySound message, PacketBuffer buf) {
		buf.writeString(message.sound);
	}

	@Override
	public void handleClientSide(MessagePlaySound message, PlayerEntity player) {}

	@Override
	public void handleServerSide(MessagePlaySound message, PlayerEntity player) {
		player.world.playSound(null, player.posX, player.posY, player.posZ, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(message.sound)), SoundCategory.PLAYERS, 1, 1);
	}

}

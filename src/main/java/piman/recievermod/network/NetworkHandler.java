package piman.recievermod.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import piman.recievermod.network.messages.*;
import piman.recievermod.util.Reference;

import java.lang.reflect.InvocationTargetException;

public class NetworkHandler {
	
	private static SimpleChannel INSTANCE;
	private static int i = 0;

	public static void init() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "main_network"), () -> "1.0.0", "1.0.0"::equals, "1.0.0"::equals);
		
		int i = 0;

		registerMessage(MessageUpdateNBT.class);
		registerMessage(MessagePlaySound.class);
		registerMessage(MessageEject.class);
		registerMessage(MessageShoot.class);
		registerMessage(MessageFlashServer.class);
		registerMessage(MessageFlashClient.class);
		registerMessage(MessageEntityPosVelUpdate.class);
		registerMessage(MessageDamageParticles.class);

//		INSTANCE.registerMessage(MessageAddToInventory.class, MessageAddToInventory.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageUpdateNBT.class, MessageUpdateNBT.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageUpdateNBT.class, MessageUpdateNBT.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessagePlaySound.class, MessagePlaySound.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageGetPlayerNBT.class, MessageGetPlayerNBT.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageSendEntityData.class, MessageSendEntityData.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageEject.class, MessageEject.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageShoot.class, MessageShoot.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageFlashServer.class, MessageFlashServer.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageFlashClient.class, MessageFlashClient.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageEntityPosVelUpdate.class, MessageEntityPosVelUpdate.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageDamageParticles.class, MessageDamageParticles.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageSpawnEntity.class, MessageSpawnEntity.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageSendItemUUID.class, MessageSendItemUUID.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageSyncConfig.class, MessageSyncConfig.class, i++, Side.CLIENT);
		
	}

	public static <T extends MessageBase<T>> void registerMessage(Class<T> messageClass) {
		try {
			T messageInstance = messageClass.getConstructor().newInstance();
			INSTANCE.registerMessage(i++, messageClass, messageInstance::toBytes, messageInstance::fromBytes, messageInstance::onMessage);
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendToServer(MessageBase<?> message) {
		INSTANCE.sendToServer(message);
	}
		
	public static void sendToClient(MessageBase<?> message, ServerPlayerEntity player) {
		INSTANCE.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
	}
	
	public static void sendToAll(MessageBase<?> message) {
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}

	public static void sendToAllTracking(MessageBase<?> message, Entity entity) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}
}

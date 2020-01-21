package piman.recievermod.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public abstract class MessageBase<T extends MessageBase<T>> {

	public MessageBase() {}

	public MessageBase(PacketBuffer buffer) {
		this.fromBytes(buffer);
	}

	public void onMessage(T message, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayerEntity player = ctx.get().getSender();
			
			if (player == null) {
				return;
			}
			
		    ctx.get().enqueueWork(new Runnable() {
		    	
		    	@Override
		    	public void run() {
		    		handleServerSide(message, player);
		    	}
		    	
		    });

		}
		else {

			ctx.get().enqueueWork(new Runnable() {

				@Override
				public void run() {
					handleClientSide(message, Minecraft.getInstance().player);
				}
				
			});

		}
	}

	public abstract T fromBytes(PacketBuffer buf);

	public abstract void toBytes(T message, PacketBuffer buf);
	
	public abstract void handleClientSide(T message, PlayerEntity player);
	
	public abstract void handleServerSide(T message, PlayerEntity player);
}

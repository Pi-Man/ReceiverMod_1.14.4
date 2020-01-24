package piman.recievermod.util.handlers;

import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageFlashClient;

@Mod.EventBusSubscriber
public class ServerEventHandler {

    @SubscribeEvent
    public static void ServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

            for (World world : ServerLifecycleHooks.getCurrentServer().getWorlds()) {
                NetworkHandler.sendToAll(new MessageFlashClient(true, world.dimension.getType().getId()));
            }

        }
    }

}

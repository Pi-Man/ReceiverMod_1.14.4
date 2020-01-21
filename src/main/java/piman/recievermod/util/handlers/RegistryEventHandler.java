package piman.recievermod.util.handlers;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.Main;
import piman.recievermod.client.renderer.model.ModelLoaderRegistry;
import piman.recievermod.init.ModItems;
import piman.recievermod.client.renderer.model.BBGunLoader;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelRegistryEvent event) {
        Main.LOGGER.info("Registering Model Loaders");
        ModelLoaderRegistry.registerLoader(new BBGunLoader());
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.getItemArray());
    }

}

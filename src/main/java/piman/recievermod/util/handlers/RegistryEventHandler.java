package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.Main;
import piman.recievermod.client.renderer.model.JsonGunLoader;
import piman.recievermod.client.renderer.model.ModelLoaderRegistry;
import piman.recievermod.init.ModEntities;
import piman.recievermod.init.ModItems;
import piman.recievermod.client.renderer.model.BBGunLoader;
import piman.recievermod.items.IItemInit;
import piman.recievermod.util.SoundsHandler;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelRegistryEvent event) {
    	ModelLoaderRegistry.clearModelCache(Minecraft.getInstance().getResourceManager());
        Main.LOGGER.info("Registering Model Loaders");
        ModelLoaderRegistry.registerLoader(new BBGunLoader());
        ModelLoaderRegistry.registerLoader(new JsonGunLoader());
        ModelLoaderRegistry.registerItems(ModItems.MODELS);
        ModItems.ITEMS.forEach(item -> {if (item instanceof IItemInit) ((IItemInit)item).Init();});
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.getItemArray());
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event) {
        ModEntities.register();
    }

    @SubscribeEvent
    public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
        SoundsHandler.registerSounds();
    }

}

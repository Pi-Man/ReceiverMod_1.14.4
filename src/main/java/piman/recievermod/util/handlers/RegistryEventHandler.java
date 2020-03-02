package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
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
import piman.recievermod.util.Reference;
import piman.recievermod.util.SoundsHandler;
import piman.recievermod.world.gen.feature.structure.UndergroundPieces;
import piman.recievermod.world.gen.feature.structure.UndergroundStructure;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {

    public static IStructurePieceType UGTP;
    public static IStructurePieceType UGEV;
    public static IStructurePieceType UGSR;
    public static IStructurePieceType UGBR;
    public static IStructurePieceType UGKT;
    public static IStructurePieceType UGLB;
    public static IStructurePieceType UGCR;
    public static IStructurePieceType UGSC;

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelRegistryEvent event) {
    	ModelLoaderRegistry.clearModelCache(Minecraft.getInstance().getResourceManager());
        Main.LOGGER.info("Registering Model Loaders");
        ModelLoaderRegistry.registerLoader(new BBGunLoader());
        ModelLoaderRegistry.registerLoader(new JsonGunLoader());
        ModelLoaderRegistry.registerItems(ModItems.MODELS);
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

    @SubscribeEvent
    public static void onFeatureRegister(RegistryEvent.Register<Feature<?>> event) {
        System.out.println("FeatureRegister");
        event.getRegistry().register(new UndergroundStructure(NoFeatureConfig::deserialize).setRegistryName(Reference.MOD_ID, "underground_structure"));
        UGTP = IStructurePieceType.register(UndergroundPieces.TestPiece::new, "UGTP");
        UGEV = IStructurePieceType.register(UndergroundPieces.Elevator::new, "UGEV");
        UGSR = IStructurePieceType.register(UndergroundPieces.ShootingRange::new, "UGSR");
        UGBR = IStructurePieceType.register(UndergroundPieces.Barracks::new, "UGBR");
        UGKT = IStructurePieceType.register(UndergroundPieces.Kitchen::new, "UGKT");
        UGLB = IStructurePieceType.register(UndergroundPieces.Lab::new, "UGLB");
        UGCR = IStructurePieceType.register(UndergroundPieces.CorridorJunction::new, "UGCR");
        UGSC = IStructurePieceType.register(UndergroundPieces.Stairs::new, "UGSC");
    }

}

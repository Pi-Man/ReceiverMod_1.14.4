package piman.recievermod;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemData;
import piman.recievermod.capabilities.itemdata.ItemDataStorage;
import piman.recievermod.init.ModEntities;
import piman.recievermod.init.ModItems;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.util.Reference;
import piman.recievermod.util.StructureRegistry;
import piman.recievermod.world.gen.feature.structure.UndergroundPieces;

import javax.annotation.Nonnull;
import java.util.Map;

@Mod(Reference.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {

    public static Main instance;

    public Main() {
        NetworkHandler.init();
        instance = this;
    }

//    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
//    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    //todo move
//    @SubscribeEvent
//    public static void PreInit(FMLPreInitializationEvent event) {
//
////        NetworkHandler.init();
//
//        proxy.preInit();
//
//        MapGenStructureIO.registerStructure(MapGenCustomStructureTest.Start.class, "Tower");
//        WorldGenStructureComponentTest.registerScatteredFeaturePieces();
//
//        GameRegistry.registerWorldGenerator(new WorldGenWastelandTower(), 0);
//
////        CapabilityManager.INSTANCE.register(IItemData.class, new ItemDataStorage(), ItemData::new);
////        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
//        GameRegistry.registerTileEntity(TileEntityBulletCrafter.class, new ResourceLocation(Reference.MOD_ID, "bullet_crafter"));
//    }

    @SubscribeEvent
    public static void dataGens(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new ItemModelProvider(gen, Reference.MOD_ID, event.getExistingFileHelper()) {
                @Override
                protected void registerModels() {
                    for (Map.Entry<Item, ResourceLocation> entry : ModItems.MODELS.entrySet()) {
                        this.getBuilder(entry.getKey().getRegistryName().getPath()).parent(this.getExistingFile(entry.getValue()));
                    }
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Bullet Model Gen";
                }
            });
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void cinit(FMLClientSetupEvent event) {
        ModEntities.registerRenderers();
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IItemData.class, new ItemDataStorage(), ItemData::new);

        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            Structure<NoFeatureConfig> structure = (Structure<NoFeatureConfig>)ForgeRegistries.FEATURES.getValue(new ResourceLocation(Reference.MOD_ID, "underground_structure"));
            biome.addStructure(structure, NoFeatureConfig.NO_FEATURE_CONFIG);
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, new ConfiguredFeature<>(structure, NoFeatureConfig.NO_FEATURE_CONFIG));
        }

        //soundsHandler = new SoundsHandler();
        //todo update
        //NetworkRegistry.INSTANCE.registerGuiHandler(this.instance, new GuiHandler());
    }

    //todo move
//    @SubscribeEvent
//    public static void PostInit(FMLPostInitializationEvent event) {
//
//        proxy.postInit();
//    }



    //HELPER FUNCTIONS

    public static int sign(double x) {
        return (int) (Math.abs(x)/x);
    }

    public static double invLerp(double a, double b, double c) {
        return (c - a) / (b - a);
    }

    public static double lerp(double a, double b, double k) {
        return (1 - k)*a + k*b;
    }

    public static RayTraceResult rayTraceEntity(Vec3d vec3d1, Vec3d vec3d2, Entity entity) {

        double x1 = vec3d1.x;
        double x2 = vec3d2.x;
        double y1 = vec3d1.y;
        double y2 = vec3d2.y;
        double z1 = vec3d1.z;
        double z2 = vec3d2.z;

        double x;
        double y;
        double z;

        double k = 0;

        AxisAlignedBB entityAABB = entity.getBoundingBox();

        if (entityAABB == null) {
            LOGGER.error("AABB is NULL");
            return null;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        if (dx >= 0) {
            k = invLerp(x1, x2, entityAABB.minX);
            x = entityAABB.minX;
            y = lerp(y1, y2, k);
            z = lerp(z1, z2, k);
            if (y >= entityAABB.maxY) {
                if (dy > 0) {
                    //LOGGER.error("Ray Not in EntityAABB");
                    return null;
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z >= entityAABB.maxZ) {
                        k = invLerp(z1, z2, entityAABB.maxZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.maxZ;
                        if (x > entityAABB.maxX || y < entityAABB.minY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        //LOGGER.info("Created Ray Trace Result");
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z < entityAABB.minZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    //LOGGER.info("Created Ray Trace Result");
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ) {
                        k = invLerp(z1, z2, entityAABB.minZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.minZ;
                        if (x > entityAABB.maxX || y < entityAABB.minY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.minZ && z < entityAABB.maxZ) {
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ || z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
            }
            if (y < entityAABB.minY) {
                if (dy < 0) {
                    //LOGGER.error("Ray Not in EntityAABB");
                    return null;
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z >= entityAABB.maxZ) {
                        k = invLerp(z1, z2, entityAABB.maxZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.maxZ;
                        if (x > entityAABB.maxX || y > entityAABB.maxY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z < entityAABB.minZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ) {
                        k = invLerp(z1, z2, entityAABB.minZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.minZ;
                        if (x > entityAABB.maxX || y > entityAABB.maxY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.minZ && z < entityAABB.maxZ) {
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ || z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
            }
            if (y >= entityAABB.minY && y < entityAABB.maxY) {
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(z1, z2, entityAABB.minZ);
                    x = lerp(x1, x2, k);
                    y = lerp(y1, y2, k);
                    z = entityAABB.minZ;
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (y < entityAABB.minY || y > entityAABB.maxY) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(z1, z2, entityAABB.maxZ);
                    x = lerp(x1, x2, k);
                    y = lerp(y1, y2, k);
                    z = entityAABB.maxZ;
                    if (x > entityAABB.maxX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (y < entityAABB.minY || y > entityAABB.maxY) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
            }
        }
        if (dx < 0) {
            k = invLerp(x1, x2, entityAABB.maxX);
            x = entityAABB.maxX;
            y = lerp(y1, y2, k);
            z = lerp(z1, z2, k);
            if (y >= entityAABB.maxY) {
                if (dy > 0) {
                    //LOGGER.error("Ray Not in EntityAABB");
                    return null;
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z >= entityAABB.maxZ) {
                        k = invLerp(z1, z2, entityAABB.maxZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.maxZ;
                        if (x < entityAABB.minX || y < entityAABB.minY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z < entityAABB.minZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ) {
                        k = invLerp(z1, z2, entityAABB.minZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.minZ;
                        if (x < entityAABB.minX || y < entityAABB.minY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.minZ && z < entityAABB.maxZ) {
                    k = invLerp(y1, y2, entityAABB.maxY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.maxY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ || z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
            }
            if (y < entityAABB.minY) {
                if (dy < 0) {
                    //LOGGER.error("Ray Not in EntityAABB");
                    return null;
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z >= entityAABB.maxZ) {
                        k = invLerp(z1, z2, entityAABB.maxZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.maxZ;
                        if (x < entityAABB.minX || y > entityAABB.maxY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z < entityAABB.minZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ) {
                        k = invLerp(z1, z2, entityAABB.minZ);
                        x = lerp(x1, x2, k);
                        y = lerp(y1, y2, k);
                        z = entityAABB.minZ;
                        if (x < entityAABB.minX || y > entityAABB.maxY) {
                            //LOGGER.error("Ray Not in EntityAABB");
                            return null;
                        }
                        return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                    }
                    if (z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.minZ && z < entityAABB.maxZ) {
                    k = invLerp(y1, y2, entityAABB.minY);
                    x = lerp(x1, x2, k);
                    y = entityAABB.minY;
                    z = lerp(z1, z2, k);
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (z < entityAABB.minZ || z > entityAABB.maxZ) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
            }
            if (y >= entityAABB.minY && y < entityAABB.maxY) {
                if (z < entityAABB.minZ) {
                    if (dz < 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(z1, z2, entityAABB.minZ);
                    x = lerp(x1, x2, k);
                    y = lerp(y1, y2, k);
                    z = entityAABB.minZ;
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (y < entityAABB.minY || y > entityAABB.maxY) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                if (z >= entityAABB.maxZ) {
                    if (dz > 0) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    k = invLerp(z1, z2, entityAABB.maxZ);
                    x = lerp(x1, x2, k);
                    y = lerp(y1, y2, k);
                    z = entityAABB.maxZ;
                    if (x < entityAABB.minX) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    if (y < entityAABB.minY || y > entityAABB.maxY) {
                        //LOGGER.error("Ray Not in EntityAABB");
                        return null;
                    }
                    return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
                }
                return new EntityRayTraceResult(entity, new Vec3d(x, y, z));
            }
        }
        LOGGER.error("Ray Not in EntityAABB catch");
        return null;
    }
}

package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.client.renderer.model.ModelLoaderRegistry;
import piman.recievermod.util.Reference;

import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEventHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        System.out.println("Setup Event");

    }

    public static class Particles {

        public static final ResourceLocation
                ONE_LOCATION = new ResourceLocation(Reference.MOD_ID, "one"),
                TWO_LOCATION = new ResourceLocation(Reference.MOD_ID, "two"),
                THREE_LOCATION = new ResourceLocation(Reference.MOD_ID, "three"),
                FOUR_LOCATION = new ResourceLocation(Reference.MOD_ID, "four"),
                FIVE_LOCATION = new ResourceLocation(Reference.MOD_ID, "five"),
                SIX_LOCATION = new ResourceLocation(Reference.MOD_ID, "six"),
                SEVEN_LOCATION = new ResourceLocation(Reference.MOD_ID, "seven"),
                EIGHT_LOCATION = new ResourceLocation(Reference.MOD_ID, "eight"),
                NINE_LOCATION = new ResourceLocation(Reference.MOD_ID, "nine"),
                ZERO_LOCATION = new ResourceLocation(Reference.MOD_ID, "zero");

        public static TextureAtlasSprite
                ONE,
                TWO,
                THREE,
                FOUR,
                FIVE,
                SIX,
                SEVEN,
                EIGHT,
                NINE,
                ZERO;
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
//        System.out.println("Texture Stitching");
        //System.out.println(event.getMap().getBasePath());

        if (event.getMap().getBasePath().equals("textures")) {

            for (Map.Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
                ModelLoaderRegistry.getModelOrLogError(entry, "Could Not Load Model");
            }

            for (Map.Entry<ResourceLocation, IUnbakedModel> entry : ModelLoaderRegistry.getUnbakedModels().entrySet()) {
                entry.getValue().getTextures(ModelLoaderRegistry.getUnbakedModels()::get, new HashSet<>()).forEach(event::addSprite);
            }

        }

        if (event.getMap().getBasePath().equals("textures/particle")) {
            event.addSprite(Particles.ONE_LOCATION);
            event.addSprite(Particles.TWO_LOCATION);
            event.addSprite(Particles.THREE_LOCATION);
            event.addSprite(Particles.FOUR_LOCATION);
            event.addSprite(Particles.FIVE_LOCATION);
            event.addSprite(Particles.SIX_LOCATION);
            event.addSprite(Particles.SEVEN_LOCATION);
            event.addSprite(Particles.EIGHT_LOCATION);
            event.addSprite(Particles.NINE_LOCATION);
            event.addSprite(Particles.ZERO_LOCATION);
        }

    }

    @SubscribeEvent
    public static void onTextureStitchPost(TextureStitchEvent.Post event) {
        if (event.getMap().getBasePath().equals("textures/particle")) {
            Particles.ONE = event.getMap().getSprite(Particles.ONE_LOCATION);
            Particles.TWO = event.getMap().getSprite(Particles.TWO_LOCATION);
            Particles.THREE = event.getMap().getSprite(Particles.THREE_LOCATION);
            Particles.FOUR = event.getMap().getSprite(Particles.FOUR_LOCATION);
            Particles.FIVE = event.getMap().getSprite(Particles.FIVE_LOCATION);
            Particles.SIX = event.getMap().getSprite(Particles.SIX_LOCATION);
            Particles.SEVEN = event.getMap().getSprite(Particles.SEVEN_LOCATION);
            Particles.EIGHT = event.getMap().getSprite(Particles.EIGHT_LOCATION);
            Particles.NINE = event.getMap().getSprite(Particles.NINE_LOCATION);
            Particles.ZERO = event.getMap().getSprite(Particles.ZERO_LOCATION);
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {

        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();
        for (Map.Entry<ResourceLocation, IBakedModel> entry : map.entrySet()) {
            if (ModelLoaderRegistry.loaded(entry.getKey())) {
                IUnbakedModel unbakedModel = ModelLoaderRegistry.getLoaded(entry.getKey());
                IBakedModel bakedModel = unbakedModel.bake(event.getModelLoader(), Minecraft.getInstance().getTextureMap()::getSprite, ModelRotation.X0_Y0, DefaultVertexFormats.ITEM);
                entry.setValue(bakedModel);
            }
        }

    }

}

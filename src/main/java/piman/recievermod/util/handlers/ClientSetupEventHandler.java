package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.client.renderer.model.ModelLoaderRegistry;

import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEventHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        System.out.println("Setup Event");

    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        System.out.println("Texture Stitching");
        System.out.println(event.getMap().getBasePath());

        if (event.getMap().getBasePath().equals("textures")) {

            for (ResourceLocation location : ForgeRegistries.ITEMS.getKeys()) {
                ModelLoaderRegistry.getModelOrLogError(new ModelResourceLocation(location, "inventory"), "Could Not Load Model");
            }

            for (Map.Entry<ResourceLocation, IUnbakedModel> entry : ModelLoaderRegistry.getUnbakedModels().entrySet()) {
                entry.getValue().getTextures(ModelLoaderRegistry.getUnbakedModels()::get, new HashSet<>()).forEach(event::addSprite);
            }

        }

    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        System.out.println("Model Bake Event");

        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();

        for (Map.Entry<ResourceLocation, IBakedModel> entry : map.entrySet()) {
            if (ModelLoaderRegistry.loaded(entry.getKey())) {
                IUnbakedModel unbakedModel = ModelLoaderRegistry.getModelOrMissing(entry.getKey());
                IBakedModel bakedModel = unbakedModel.bake(event.getModelLoader(), Minecraft.getInstance().getTextureMap()::getSprite, ModelRotation.X0_Y0, DefaultVertexFormats.ITEM);
                entry.setValue(bakedModel);
            }
        }

    }

}

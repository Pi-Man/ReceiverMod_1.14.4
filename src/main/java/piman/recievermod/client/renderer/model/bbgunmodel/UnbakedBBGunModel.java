package piman.recievermod.client.renderer.model.bbgunmodel;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import piman.recievermod.client.renderer.model.animator.Animator;
import piman.recievermod.client.renderer.model.BakedGunModel;
import piman.recievermod.client.renderer.model.GunOverrideHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UnbakedBBGunModel implements net.minecraft.client.renderer.model.IUnbakedModel {

    private Animator animator;
    private ModelBlock model;
    private ResourceLocation baseLocation;

    public UnbakedBBGunModel(ResourceLocation resourceLocation) {
        String basePath = "models/item/" + resourceLocation.getPath().split("\\.")[0];

        ResourceLocation animationLocation = new ResourceLocation(resourceLocation.getNamespace(), basePath + ".json");

        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(animationLocation);
            this.animator = Animator.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            this.animator = new Animator();
            e.printStackTrace();
        }

        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(resourceLocation.getNamespace(), "models/item/" + resourceLocation.getPath()));
            this.model = ModelBlock.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return animator.getDependencies();
    }

    public Collection<ResourceLocation> getDependenciesForMap() {
        return animator.getDependenciesForMap();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {

        Set<ResourceLocation> set = new HashSet<>();

        for (Map.Entry<String, String> entry : model.textures.entrySet()) {
            set.add(new ResourceLocation(entry.getValue()));
        }

        return set;
    }

    /**
     * @param bakery
     * @param spriteGetter Where textures will be looked up when baking
     * @param sprite       Transforms to apply while baking. Usually will be an instance of {@link IModelState}.
     * @param format
     */
    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {

        System.out.println("Baking Whole Model");

        if (format != DefaultVertexFormats.ITEM) {
            throw new IllegalArgumentException("Vertex Format Must be ITEM");
        }

        List<IBakedModel> models = new ArrayList<>();

        models.add(bakePart(spriteGetter, sprite, this.baseLocation));

        for (ResourceLocation location : this.getDependenciesForMap()) {
            models.add(bakePart(spriteGetter, sprite, location));
        }

        return new BakedGunModel(this, models, new HashMap<>(), spriteGetter.apply(new ResourceLocation(model.textures.get("0"))), format, new GunOverrideHandler(animator), new HashMap<>());
    }

    public IBakedModel bakePart(Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, ResourceLocation location) {
        return new SimpleBakedBBModel.Builder(model, ItemOverrideList.EMPTY, spriteGetter).build();
    }
}

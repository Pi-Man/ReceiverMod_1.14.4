package piman.recievermod.client.renderer.model.jsongunmodel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import piman.recievermod.Main;
import piman.recievermod.client.renderer.model.BakedGunModel;
import piman.recievermod.client.renderer.model.GunOverrideHandler;
import piman.recievermod.client.renderer.model.animator.Animator;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UnbakedJsonGunModel implements IUnbakedModel {

    private Animator animator;
    private BlockModel model;
    private List<BlockModel> submodels = new ArrayList<>();

    public UnbakedJsonGunModel(ResourceLocation location) {

        try {
            IResource iResource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(location.getNamespace(), "models/item/" + location.getPath() + ".json"));
            Reader reader = new InputStreamReader(iResource.getInputStream(), StandardCharsets.UTF_8);
            animator = Animator.deserialize(reader);
            reader.close();
        }
        catch (IOException e) {
            animator = new Animator();
            e.printStackTrace();
        }

        try {
            IResource iResource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(location.getNamespace(), "models/item/" + location.getPath() + ".json"));
            Reader reader = new InputStreamReader(iResource.getInputStream(), StandardCharsets.UTF_8);
            model = BlockModel.deserialize(reader);
            for (ResourceLocation location1 : this.getDependenciesForMap()) {
                IResource iResource1 = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(location1.getNamespace(), "models/item/" + location1.getPath() + ".json"));
                Reader reader1 = new InputStreamReader(iResource1.getInputStream(), StandardCharsets.UTF_8);
                submodels.add(BlockModel.deserialize(reader1));
            }
        }
        catch (IOException e) {
            Main.LOGGER.error("Could Not Load Model: " + location, e);
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
        return model.getTextures(modelGetter, missingTextureErrors);
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

        List<TRSRTransformation> newTransforms = new ArrayList<>();
        for (@SuppressWarnings("unused") BlockPart part : model.getElements()) newTransforms.add(TRSRTransformation.identity());

        ItemCameraTransforms transforms = model.getAllTransforms();
        Map<ItemCameraTransforms.TransformType, TRSRTransformation> tMap = Maps.newEnumMap(ItemCameraTransforms.TransformType.class);
        tMap.putAll(PerspectiveMapWrapper.getTransforms(transforms));
        tMap.putAll(PerspectiveMapWrapper.getTransforms(sprite.getState()));
        //IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap), sprite.getState().apply(Optional.empty()));

        models.add(this.bakeNormal(bakery, model, sprite.getState(), sprite.getState(), newTransforms, format, spriteGetter, false));

        for (BlockModel model : this.submodels) {
            newTransforms.clear();
            for (@SuppressWarnings("unused") BlockPart part : model.getElements()) newTransforms.add(TRSRTransformation.identity());
            models.add(this.bakeNormal(bakery, model, sprite.getState(), sprite.getState(), newTransforms, format, spriteGetter, false));
        }

        return new BakedGunModel(this, models, tMap, spriteGetter.apply(new ResourceLocation(this.model.resolveTextureName("particle"))), format, new GunOverrideHandler(animator), new HashMap<>());
    }

    private IBakedModel bakeNormal(ModelBakery bakery, BlockModel model, IModelState perState, final IModelState modelState, List<TRSRTransformation> newTransforms, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked) {
        final TRSRTransformation baseState = modelState.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
        SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model, model.getOverrides(bakery, model, bakedTextureGetter, format))).setTexture(particle);
        for (int i = 0; i < model.getElements().size(); i++) {
            if (modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(Integer.toString(i))))).isPresent()) {
                continue;
            }
            BlockPart part = model.getElements().get(i);
            TRSRTransformation transformation = baseState;
            if (newTransforms.get(i) != null) {
                transformation = transformation.compose(newTransforms.get(i));
                BlockPartRotation rot = part.partRotation;
                if (rot == null) rot = new BlockPartRotation(new Vector3f(), Direction.Axis.Y, 0, false);
                part = new BlockPart(part.positionFrom, part.positionTo, part.mapFaces, rot, part.shade);
            }
            for (Map.Entry<Direction, BlockPartFace> e : part.mapFaces.entrySet()) {
                TextureAtlasSprite textureatlassprite1 = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName(e.getValue().texture)));

                if (e.getValue().cullFace == null || !TRSRTransformation.isInteger(transformation.getMatrixVec())) {
                    builder.addGeneralQuad(BlockModel.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), new BasicState(transformation, uvLocked)));
                } else {
                    builder.addFaceQuad(baseState.rotateTransform(e.getValue().cullFace), BlockModel.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), new BasicState(transformation, uvLocked)));
                }
            }
        }

        return builder.build();
    }
}

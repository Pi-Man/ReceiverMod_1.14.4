package piman.recievermod.client.renderer.model.bbgunmodel;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import piman.recievermod.util.clientUtils.BakedQuadBuilder;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedBBModel implements IBakedModel {

    protected final List<BakedQuad> generalQuads;
    protected final Map<Direction, List<BakedQuad>> faceQuads;
    protected final boolean ambientOcclusion;
    protected final boolean gui3d;
    protected final TextureAtlasSprite texture;
    protected final ItemCameraTransforms cameraTransforms;
    protected final ItemOverrideList itemOverrideList;

    public SimpleBakedBBModel(List<BakedQuad> generalQuadsIn, Map<Direction, List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn, TextureAtlasSprite textureIn, ItemCameraTransforms cameraTransformsIn, ItemOverrideList itemOverrideListIn) {
        this.generalQuads = generalQuadsIn;
        this.faceQuads = faceQuadsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.texture = textureIn;
        this.cameraTransforms = cameraTransformsIn;
        this.itemOverrideList = itemOverrideListIn;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return side == null ? this.generalQuads : new ArrayList<>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return this.gui3d;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.texture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.cameraTransforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.itemOverrideList;
    }
	
	@OnlyIn(Dist.CLIENT)
	public static class Builder {
		
		private final ModelBlock model;
		private final ItemOverrideList overrides;
		private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
		
		public Builder(ModelBlock model, ItemOverrideList overrides, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			this.model = model;
			this.overrides = overrides;
			this.bakedTextureGetter = bakedTextureGetter;
		}
		
		public IBakedModel build() {
			
			List<BakedQuad> quads = new ArrayList<>();

			List<ModelGroup> groupStack = new ArrayList<>();

			Map<UUID, ModelElement> map = new HashMap<>(model.getElements());

			for (ModelGroup group : model.getGroups()) {
                groupStack.add(0, group);
                buildFromGroup(groupStack, map, quads);
                groupStack.remove(0);
            }

			for (Entry<UUID, ModelElement> entry : map.entrySet()) {
			    buildFromElement(groupStack, entry.getValue(), quads);
            }
			
			return new SimpleBakedBBModel(quads, null, model.ambientOcclusion, true, bakedTextureGetter.apply(new ResourceLocation(model.textures.get("0"))), model.getAllTransforms(), overrides);
		}

        private void buildFromGroup(List<ModelGroup> groupStack, Map<UUID, ModelElement> elementMap, List<BakedQuad> quads) {
            ModelGroup group = groupStack.get(0);
            for (ModelGroup newGroup : group.subGroups) {
                groupStack.add(0, newGroup);
                buildFromGroup(groupStack, elementMap, quads);
                groupStack.remove(0);
            }
            for (UUID elementUUID : group.elements) {
                ModelElement element = elementMap.remove(elementUUID);
                buildFromElement(groupStack, element, quads);
            }
        }

        private void buildFromElement(List<ModelGroup> groupStack, ModelElement element, List<BakedQuad> quads) {

            Vector3f[] points = new Vector3f[8];

            points[0] = new Vector3f(element.start.x, element.end.y, element.end.z);
            points[1] = new Vector3f(element.end.x, element.end.y, element.end.z);
            points[2] = new Vector3f(element.end.x, element.end.y, element.start.z);
            points[3] = new Vector3f(element.start.x, element.end.y, element.start.z);

            points[4] = new Vector3f(element.start.x, element.start.y, element.start.z);
            points[5] = new Vector3f(element.end.x, element.start.y, element.start.z);
            points[6] = new Vector3f(element.end.x, element.start.y, element.end.z);
            points[7] = new Vector3f(element.start.x, element.start.y, element.end.z);

            for (Entry<Direction, ElementFace> entry : element.faces.entrySet()) {

                Direction facing = entry.getKey();

                ElementFace face = entry.getValue();

                int[] ints = getPointIndexes(facing);

                BakedQuadBuilder builder = new BakedQuadBuilder().setPosition(points[ints[0]], 0).setPosition(points[ints[1]], 1).setPosition(points[ints[2]], 2).setPosition(points[ints[3]], 3).applyRotation(element.rotation, element.origin).setColor(0xffffffff).setTexture(bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName(face.texture)))).setUV(face.uv);

                for (ModelGroup group : groupStack) {
                    builder.applyRotation(group.rotation, group.origin);
                }

                quads.add(builder.build());
            }

        }

        private int[] getPointIndexes(Direction facing) {

		    switch (facing) {

                case DOWN:
                    return new int[]{4, 5, 6, 7};
                case UP:
                    return new int[]{0, 1, 2, 3};
                case NORTH:
                    return new int[]{5, 4, 3, 2};
                case SOUTH:
                    return new int[]{7, 6, 1, 0};
                case WEST:
                    return new int[]{4, 7, 0, 3};
                case EAST:
                    return new int[]{6, 5, 2, 1};
                default:
                    return new int[0];
            }

        }
		
	}

}

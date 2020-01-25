package piman.recievermod.client.renderer.model;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import piman.recievermod.client.renderer.model.jsongunmodel.UnbakedJsonGunModel;
import piman.recievermod.util.Reference;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JsonGunLoader implements ICustomModelLoader {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    /**
     * Checks if given model should be loaded by this loader.
     * Reading file contents is inadvisable, if possible decision should be made based on the location alone.
     *
     * @param modelLocation The path, either to an actual file or a {@link ModelResourceLocation}.
     */
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getNamespace().equals(Reference.MOD_ID) && (modelLocation.getPath().startsWith("_gun_") || modelLocation.getPath().startsWith("_mag_") || modelLocation.getPath().startsWith("_clip_"));
    }

    /**
     * @param modelLocation The model to (re)load, either path to an
     *                      actual file or a {@link ModelResourceLocation}.
     */
    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new UnbakedJsonGunModel(modelLocation);
    }
}

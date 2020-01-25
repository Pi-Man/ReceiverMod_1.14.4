package piman.recievermod.client.renderer.model;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import piman.recievermod.Main;
import piman.recievermod.client.renderer.model.bbgunmodel.UnbakedBBGunModel;
import piman.recievermod.util.Reference;

import javax.annotation.Nonnull;

public class BBGunLoader implements ICustomModelLoader {

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

    }

    /**
     * Checks if given model should be loaded by this loader.
     * Reading file contents is inadvisable, if possible decision should be made based on the location alone.
     *
     * @param modelLocation The path, either to an actual file or a {@link ModelResourceLocation}.
     */
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getNamespace().equals(Reference.MOD_ID) && modelLocation.getPath().endsWith(".bbmodel");
    }

    /**
     * @param modelLocation The model to (re)load, either path to an
     *                      actual file or a {@link ModelResourceLocation}.
     */
    @Nonnull
    @Override
    public IUnbakedModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
        Main.LOGGER.info("Loading Model: {}", modelLocation);
        return new UnbakedBBGunModel(modelLocation);
    }
}

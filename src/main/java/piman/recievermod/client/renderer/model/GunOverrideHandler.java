package piman.recievermod.client.renderer.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import piman.recievermod.client.renderer.model.animator.Animator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class GunOverrideHandler extends ItemOverrideList {

    private Animator animator;

    public GunOverrideHandler(Animator animator) {
        this.animator = animator;
    }

    @Nullable
    @Override
    public IBakedModel getModelWithOverrides(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {

        if (model instanceof BakedGunModel) {

            BakedGunModel gunModel = (BakedGunModel) model;
            gunModel.setSubTransforms(animator.getSubTransforms(stack, worldIn, entityIn));

            Map<ItemCameraTransforms.TransformType, TRSRTransformation> baseTransforms = animator.getBaseTransforms(stack, worldIn, entityIn);
            PerspectiveMapWrapper.getTransforms(gunModel.getItemCameraTransforms()).forEach((type, transform) -> baseTransforms.put(type, transform.compose(baseTransforms.get(type))));
            gunModel.setCameraTransforms(ImmutableMap.copyOf(baseTransforms));

        }

        return model;
    }
}

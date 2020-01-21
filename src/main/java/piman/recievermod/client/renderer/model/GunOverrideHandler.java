package piman.recievermod.client.renderer.model;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import piman.recievermod.client.renderer.model.animator.Animator;

import javax.annotation.Nullable;

public class GunOverrideHandler extends ItemOverrideList {

    private Animator animator;

    public GunOverrideHandler(Animator animator) {
        this.animator = animator;
    }

    @Nullable
    @Override
    public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {

        if (model instanceof BakedGunModel) {
            BakedGunModel gunModel = (BakedGunModel) model;
            gunModel.setSubTransforms(animator.getSubTransforms(stack, worldIn, entityIn));
        }

        return model;
    }
}

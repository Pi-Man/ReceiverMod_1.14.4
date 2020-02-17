package piman.recievermod.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.client.renderer.RenderBullet;
import piman.recievermod.entities.EntityBullet;
import piman.recievermod.util.Reference;

@ObjectHolder(Reference.MOD_ID)
public class ModEntities {

    public static final EntityType<EntityBullet> ENTITYBULLET = null;

    public static void register() {
        ForgeRegistries.ENTITIES.register(EntityType.Builder.<EntityBullet>create(EntityBullet::new, EntityClassification.MISC).disableSummoning().immuneToFire().setUpdateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(true).size(0.25F, 0.25F).build("entity_bullet").setRegistryName(Reference.MOD_ID, "entitybullet"));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, RenderBullet::new);
    }

}

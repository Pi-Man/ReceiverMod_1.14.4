package piman.recievermod.util.handlers;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageDamageParticles;

@Mod.EventBusSubscriber
public class CommonEventHandler {

    @SubscribeEvent
    public static void damageEvent(LivingDamageEvent event) {
        if(!event.getEntityLiving().world.isRemote) {
            LivingEntity entity = event.getEntityLiving();
            //boolean showAll = ModConfig.damageParticles;
            //if (entity instanceof EntityDummyTarget || showAll) {
                float damage = event.getAmount();
                NetworkHandler.sendToAll(new MessageDamageParticles(damage, entity.posX, entity.posY + entity.getHeight() + 0.1, entity.posZ));
            //}
        }
    }

}

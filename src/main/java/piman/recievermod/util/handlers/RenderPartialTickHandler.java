package piman.recievermod.util.handlers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class RenderPartialTickHandler {
	
	public static float renderPartialTick;
	
	@SubscribeEvent
	public static void OnRenderPartialTick(TickEvent.RenderTickEvent event) {
		renderPartialTick = event.renderTickTime;
	}

}

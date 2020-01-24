package piman.recievermod.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;

@EventBusSubscriber
public class CapabilityHandler {
	
	public static final ResourceLocation ITEM_CAP = new ResourceLocation(Reference.MOD_ID, "item");
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<World> event) {
		event.addCapability(ITEM_CAP, new ItemDataProvider());
	}
}

package piman.recievermod.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageFlashServer;

public class FlashHandler {

	static private Map<BlockPos, Integer> flashcache = new HashMap<>();

	@OnlyIn(Dist.CLIENT)
	public static void Update(int dimension) {
		List<BlockPos> remove = new ArrayList<>();
		for (Entry<BlockPos, Integer> entry : flashcache.entrySet()) {
			
			BlockPos pos = entry.getKey();
			int duration = entry.getValue();

			World world = Minecraft.getInstance().world;
						
			if (duration > 1) {
				entry.setValue(duration - 1);
			}
			else {
				Minecraft.getInstance().world.getChunk(pos).getWorldLightManager().checkBlock(pos);
				remove.add(pos);
			}
		}
		remove.forEach(flashcache::remove);
	}

	public static void CreateFlash(BlockPos pos, int dimension, int duration) {
		NetworkHandler.sendToServer(new MessageFlashServer(pos, dimension, duration));
	}

	@OnlyIn(Dist.CLIENT)
	public static void AddFlash(BlockPos pos, int dimension, int duration) {
		
		World world = Minecraft.getInstance().world;
		
		BlockPos pos2 = pos.add(0, -1, 0);

		if (world.isAreaLoaded(pos2, 0)) {
			world.getChunk(pos2).getWorldLightManager().func_215573_a(pos2, 15);
		}
		
		flashcache.put(pos, duration);
	}
	
}

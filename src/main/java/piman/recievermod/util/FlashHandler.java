package piman.recievermod.util;

import java.util.HashMap;
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

	static private Map<BlockPos, Integer> flashcache = new HashMap<BlockPos, Integer>();
	
	public static void Update(int dimension) {
		for (Entry<BlockPos, Integer> entry : flashcache.entrySet()) {
			
			BlockPos pos = entry.getKey();
			int duration = entry.getValue();

			World world = Minecraft.getInstance().world;
						
			if (duration > 1) {
				entry.setValue(duration - 1);
			}
			else {
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						BlockPos pos2 = pos.add(i, 0, k);
						if (!flashcache.containsKey(pos2)) {
							//Main.proxy.getWorld(dimension).checkLight(pos2);
						}
					}
				}
				//Main.proxy.getWorld(dimension).checkLight(pos);
				flashcache.remove(pos);
			}
		}
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
			//world.getChunk(pos2).getWorldLightManager().checkBlock(pos2);

		}
		
		flashcache.put(pos, duration);
	}
	
}

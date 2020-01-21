package piman.recievermod.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import piman.recievermod.Main;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageFlashServer;

public class FlashHandler {

	static private Map<BlockPos, Integer> flashcache = new HashMap<BlockPos, Integer>();
	
	public static void Update(int dimension) {
		for (Entry<BlockPos, Integer> entry : flashcache.entrySet()) {
			
			BlockPos pos = entry.getKey();
			int duration = entry.getValue();
						
			if (duration > 1) {
				entry.setValue(duration - 1);
			}
			else {
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						BlockPos pos2 = pos.add(i, 0, k);
						if (!flashcache.containsKey(pos2)) {
							Main.proxy.getWorld(dimension).checkLight(pos2);
						}
					}
				}
				Main.proxy.getWorld(dimension).checkLight(pos);
				flashcache.remove(pos);
			}
		}
	}

	public static void CreateFlash(BlockPos pos, int dimension, int duration) {
		NetworkHandler.sendToServer(new MessageFlashServer(pos, dimension, duration));
	}
	
	public static void AddFlash(BlockPos pos, int dimension, int duration) {
		
		World world = Main.proxy.getWorld(dimension);
		
		BlockPos pos2 = pos.add(0, -1, 0);
world.getLight()
		world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
		world.checkLight(pos2);
		
		flashcache.put(pos, duration);
	}
	
}

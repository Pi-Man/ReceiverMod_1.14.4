package piman.recievermod.keybinding;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class Keybinds {
	
	public static KeyBinding addBullet = new KeyBinding("key.addbullet", GLFW.GLFW_KEY_Z, "key.categories.guncontrols");
	public static KeyBinding removeBullet = new KeyBinding("key.removebullet", GLFW.GLFW_KEY_R, "key.categories.guncontrols");
	public static KeyBinding removeClip = new KeyBinding("key.removeclip", GLFW.GLFW_KEY_G, "key.categories.guncontrols");
	public static KeyBinding slideLock = new KeyBinding("key.slidelock", GLFW.GLFW_KEY_T, "key.categories.guncontrols");
	public static KeyBinding safety = new KeyBinding("key.saftey", GLFW.GLFW_KEY_V, "key.categories.guncontrols");
	
	public static void register() {
		
		ClientRegistry.registerKeyBinding(addBullet);
		ClientRegistry.registerKeyBinding(removeBullet);
		ClientRegistry.registerKeyBinding(removeClip);
		ClientRegistry.registerKeyBinding(slideLock);
		ClientRegistry.registerKeyBinding(safety);
		
	}	
}

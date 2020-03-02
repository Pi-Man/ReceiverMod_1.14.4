package piman.recievermod.keybinding;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import piman.recievermod.Main;
import piman.recievermod.items.guns.ItemGun;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class KeyInputHandler {
		
	@SubscribeEvent
	public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().player != null) {
			if (event.phase == TickEvent.Phase.START) {
				prevScreen = currentScreen;
				currentScreen = Minecraft.getInstance().currentScreen;
				checkKeys();
			}
			else if (event.phase == TickEvent.Phase.END){
				scroll = 0;
			}
		}
	}

	@SubscribeEvent
	public static void onInput(InputEvent event) {
		
		PlayerEntity player = Minecraft.getInstance().player;
		
		if (player != null && player.getHeldItemMainhand().getItem() instanceof ItemGun && !gs.keyBindSprint.isKeyDown()) {
						
			KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), false);
			while (gs.keyBindAttack.isPressed());
			
			KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), false);
			while (gs.keyBindUseItem.isPressed());
			
		}
	}
	
	@SubscribeEvent
	public static void resetScrollCancle(InputUpdateEvent event) {
		scrollCancle = false;
	}
	
	@SubscribeEvent
	public static void onMouseEvent(InputEvent.MouseScrollEvent event) {
		
		if (scrollCancle) {
			event.setCanceled(true);
		}
		
		scroll += Main.sign(event.getScrollDelta());
	}
	
	static Screen currentScreen = null;
	
	static Screen prevScreen = null;
	
	static GameSettings gs = Minecraft.getInstance().gameSettings;
	
	static KeyBinding Keys[] = new KeyBinding[] {
			Keybinds.addBullet, 
			Keybinds.removeBullet, 
			Keybinds.removeMag, 
			Keybinds.slideLock, 
			Keybinds.safety,
			Keybinds.hammer,
			gs.keyBindAttack, 
			gs.keyBindUseItem,
			gs.keyBindSneak};
	
	static int scroll = 0;
	
	static final int NUMKEYS = Keys.length;
	
	static final int OLD = 1;
	
	static final int NEW = 0;
	
	private static boolean States[][] = new boolean[2][NUMKEYS];
	
	private static boolean scrollCancle = false;

	private static long windowId = Minecraft.getInstance().mainWindow.getHandle();
		
	private static void checkKeys() {
						
		for(int i = 0; i < NUMKEYS; i++) {
			
			getStates()[OLD][i] = getStates()[NEW][i];
			
			InputMappings.Input key = Keys[i].getKey();
			
			if (key.getType() == InputMappings.Type.MOUSE) {
				getStates()[NEW][i] = GLFW.glfwGetMouseButton(windowId, key.getKeyCode()) != 0 && !gs.keyBindSprint.isKeyDown();
			}
			else {
				getStates()[NEW][i] = GLFW.glfwGetKey(windowId, key.getKeyCode()) != 0;
			}
		}
		
	}
	
	public static boolean isKeyPressed(KeyPresses key) {
		
		if (prevScreen != null) {
			return false;
		}
				
		return (getStates()[NEW][key.ordinal()] && !getStates()[OLD][key.ordinal()]);
		
	}
	
	public static boolean isKeyDown(KeyPresses key) {
		
		if (prevScreen != null) {
			return false;
		}
		
		return (getStates()[NEW][key.ordinal()]);
	}
	
	public static boolean isKeyUnpressed(KeyPresses key) {
		
		if (prevScreen != null) {
			return false;
		}
		
		return(!getStates()[NEW][key.ordinal()] && getStates()[OLD][key.ordinal()]);
	}
	
	public static int getScroll() {
		return scroll;
	}
	
	public static void cancleScroll(boolean cancle) {
		scrollCancle = cancle;
	}
	
	public static boolean[][] getStates() {
		return States;
	}

	public static void setStates(boolean states[][]) {
		States = states;
	}

	public enum KeyPresses {
		AddBullet,
		RemoveBullet,
		RemoveMag,
		SlideLock,
		Safety,
		Hammer,
		LeftClick,
		RightClick,
		Shift;
	}
}
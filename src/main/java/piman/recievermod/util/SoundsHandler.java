package piman.recievermod.util;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundsHandler {
	
	public static SoundEvent getSoundEvent(int sound) {
		return SOUNDS[sound];
	}
	
	public static SoundEvent getSoundEvent(Sounds sound) {
		return SOUNDS[sound.ordinal()];
	}
	
	private static final SoundEvent[] SOUNDS = {
		registerSound("item.rifle.shot"),
		registerSound("item.rifle.boltup"),
		registerSound("item.rifle.boltback"),
		registerSound("item.rifle.boltforward"),
		registerSound("item.rifle.boltdown"),
		registerSound("item.1911.shot"),
		registerSound("item.1911.slideforward"),
		registerSound("item.1911.slideback"),
		registerSound("item.glock.dry"),
		registerSound("item.glock.magin"),
		registerSound("item.glock.magout"),
		registerSound("item.glock.shot"),
		registerSound("item.glock.slideback"),
		registerSound("item.glock.slideforward"),
		registerSound("entity.turret.target")
	};
	
	public static enum Sounds {
		RIFLE_SHOOT, 
		RIFLE_BOLT_UP, 
		RIFLE_BOLT_BACK, 
		RIFLE_BOLT_FORWARD, 
		RIFLE_BOLT_DOWN, 
		COLT_1911_SHOT, 
		COLT_1911_SLIDE_FORWARD, 
		COLT_1911_SLIDE_BACK,
		GLOCK_DRY,
		GLOCK_MAG_IN,
		GLOCK_MAG_OUT,
		GLOCK_SHOT,
		GLOCK_SLIDEBACK,
		GLOCK_SLIDEFORWARD,
		TURRET_TARGET;
	}

	private static SoundEvent registerSound(String name) {
		ResourceLocation location = new ResourceLocation(Reference.MOD_ID, name);
		SoundEvent event = new SoundEvent(location);
		event.setRegistryName(name);
		ForgeRegistries.SOUND_EVENTS.register(event);
		return event;
	}
	
	public static class LoopingEntitySound extends LocatableSound implements ITickableSound{
		
		private Entity entity;
		
		private boolean finish = false;
		
		public LoopingEntitySound(Entity entity, SoundEvent sound, SoundCategory category) {
			super(sound, category);
			this.entity = entity;
			this.repeat = true;
			this.repeatDelay = 0;
		}

		@Override
		public void tick() {
			this.x = (float) entity.posX;
			this.y = (float) entity.posY;
			this.z = (float) entity.posZ;
			this.pitch = 1;
			this.volume = 1;
			finish = !entity.isAlive();
		}

		@Override
		public boolean isDonePlaying() {
			return finish;
		}
	}
}

package piman.recievermod.util;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Reference.MOD_ID)
public class SoundsHandler {

	public static final SoundEvent ITEM_RIFLE_SHOT = null;
	public static final SoundEvent ITEM_RIFLE_BOLTUP = null;
	public static final SoundEvent ITEM_RIFLE_BOLTBACK = null;
	public static final SoundEvent ITEM_RIFLE_BOLTFORWARD = null;
	public static final SoundEvent ITEM_RIFLE_BOLTDOWN = null;
	public static final SoundEvent ITEM_1911_SHOT = null;
	public static final SoundEvent ITEM_1911_SLIDEFORWARD = null;
	public static final SoundEvent ITEM_1911_SLIDEBACK = null;
	public static final SoundEvent ITEM_GLOCK_DRY = null;
	public static final SoundEvent ITEM_GLOCK_MAGIN = null;
	public static final SoundEvent ITEM_GLOCK_MAGOUT = null;
	public static final SoundEvent ITEM_GLOCK_SHOT = null;
	public static final SoundEvent ITEM_GLOCK_SLIDEBACK = null;
	public static final SoundEvent ITEM_GLOCK_SLIDEFORWARD = null;
	public static final SoundEvent ENTITY_TURRET_TARGET = null;

	public static void registerSounds() {
		registerSound("item.rifle.shot");
		registerSound("item.rifle.boltup");
		registerSound("item.rifle.boltback");
		registerSound("item.rifle.boltforward");
		registerSound("item.rifle.boltdown");
		registerSound("item.1911.shot");
		registerSound("item.1911.slideforward");
		registerSound("item.1911.slideback");
		registerSound("item.glock.dry");
		registerSound("item.glock.magin");
		registerSound("item.glock.magout");
		registerSound("item.glock.shot");
		registerSound("item.glock.slideback");
		registerSound("item.glock.slideforward");
		registerSound("entity.turret.target");
	}

	private static void registerSound(String name) {
		ResourceLocation location = new ResourceLocation(Reference.MOD_ID, name);
		SoundEvent event = new SoundEvent(location);
		event.setRegistryName(name.replace('.', '_'));
		ForgeRegistries.SOUND_EVENTS.register(event);
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

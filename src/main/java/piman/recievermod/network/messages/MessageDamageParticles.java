package piman.recievermod.network.messages;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import piman.recievermod.client.particles.NumberParticle;
import piman.recievermod.util.handlers.ClientSetupEventHandler;

public class MessageDamageParticles extends MessageBase<MessageDamageParticles> {
	
	private double damage;
	private double posX;
	private double posY;
	private double posZ;
	
	public MessageDamageParticles() {}
	
	public MessageDamageParticles(double damage, double posX, double posY, double posZ) {
		this.damage = damage;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public MessageDamageParticles fromBytes(PacketBuffer buf) {
		double damage = buf.readDouble();
		double posX = buf.readDouble();
		double posY = buf.readDouble();
		double posZ = buf.readDouble();

		return new MessageDamageParticles(damage, posX, posY, posZ);
	}

	@Override
	public void toBytes(MessageDamageParticles message, PacketBuffer buf) {
		buf.writeDouble(message.damage);
		buf.writeDouble(message.posX);
		buf.writeDouble(message.posY);
		buf.writeDouble(message.posZ);
	}

	@Override
	public void handleClientSide(MessageDamageParticles message, PlayerEntity player) {
		//if (ModConfig.damageParticles) {
		{
			
			Random rand = new Random();
		
	    	char[] damage = ((Integer) (int)message.damage).toString().toCharArray();
	    	
	    	double vx, vz;
	    				
			double x1 = message.posX;
			double x2 = player.posX;
			double y1 = message.posY;
			double y2 = player.posY;
			double z1 = message.posZ;
			double z2 = player.posZ;
			
			double dx = x2 - x1;
			double dy = y2 - y1;
			double dz = z2 - z1;
			
			double length = Math.sqrt(dz*dz + dx*dx);
			double distance = Math.sqrt(dy*dy + length*length);
			
//			if (distance > ModConfig.damageParticlesDistance) {
//				return;
//			}
			
    		float scale = (float) (0.4 * distance);
			
			double rx, rz;
			
			double r = rand.nextGaussian() / 10;
			double vy = 0.1 + rand.nextGaussian() / 50;
			
			rx = dz/length*0.2*r*scale;
			rz = dx/length*0.2*r*scale;
			
			vx = dx/length*0.1 + rx;
			vz = dz/length*0.1 - rz;
	
	    	for (int i = damage.length - 1; i >=0; i--) {
	    		
	    		
	    		double x = message.posX;
	    		double z = message.posZ;
	    					
				x += dz/length*0.2*i*scale - dz/length*0.1*scale*(damage.length - 1);
				z -= dx/length*0.2*i*scale - dx/length*0.1*scale*(damage.length - 1);
	        	
	    		TextureAtlasSprite textureatlassprite;
	    		
	    		int d = damage[i] - '0';
	    		    		
	    		switch(d) {
	    		case 0:
	    			textureatlassprite = ClientSetupEventHandler.Particles.ZERO;
	    			break;
	    		case 1:
	    			textureatlassprite = ClientSetupEventHandler.Particles.ONE;
	    			break;
	    		case 2:
	    			textureatlassprite = ClientSetupEventHandler.Particles.TWO;
	    			break;
	    		case 3:
	    			textureatlassprite = ClientSetupEventHandler.Particles.THREE;
	    			break;
	    		case 4:
	    			textureatlassprite = ClientSetupEventHandler.Particles.FOUR;
	    			break;
	    		case 5:
	    			textureatlassprite = ClientSetupEventHandler.Particles.FIVE;
	    			break;
	    		case 6:
	    			textureatlassprite = ClientSetupEventHandler.Particles.SIX;
	    			break;
	    		case 7:
	    			textureatlassprite = ClientSetupEventHandler.Particles.SEVEN;
	    			break;
	    		case 8:
	    			textureatlassprite = ClientSetupEventHandler.Particles.EIGHT;
	    			break;
	    		case 9:
	    			textureatlassprite = ClientSetupEventHandler.Particles.NINE;
	    			break;
				default:
					textureatlassprite = null;
	    		}
	    		NumberParticle particlenumber = new NumberParticle(player.world, x, message.posY, z, vx, vy, vz, scale, textureatlassprite);
	    		Minecraft.getInstance().particles.addEffect(particlenumber);
	    	}
		}
	}

	@Override
	public void handleServerSide(MessageDamageParticles message, PlayerEntity player) {
		
	}

}

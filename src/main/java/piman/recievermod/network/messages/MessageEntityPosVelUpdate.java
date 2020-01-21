package piman.recievermod.network.messages;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import piman.recievermod.Main;

public class MessageEntityPosVelUpdate extends MessageBase<MessageEntityPosVelUpdate> {
	
	private double x, y, z, dx, dy, dz;
	
	private int id, dimension;
	
	public MessageEntityPosVelUpdate() {}
	
	public MessageEntityPosVelUpdate(Entity entity) {
		this.x = entity.posX;
		this.y = entity.posY;
		this.z = entity.posZ;
		this.dx = entity.getMotion().x;
		this.dy = entity.getMotion().y;
		this.dz = entity.getMotion().z;
		this.id = entity.getEntityId();
		this.dimension = entity.dimension.getId();
	}

	public MessageEntityPosVelUpdate(double x, double y, double z, double dx, double dy, double dz, int id, int dimension) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.id = id;
		this.dimension = dimension;
	}

	@Override
	public MessageEntityPosVelUpdate fromBytes(PacketBuffer buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		dx = buf.readDouble();
		dy = buf.readDouble();
		dz = buf.readDouble();
		id = buf.readInt();
		dimension = buf.readInt();
		return new MessageEntityPosVelUpdate(x, y, z, dx, dy, dz, id, dimension);
	}

	@Override
	public void toBytes(MessageEntityPosVelUpdate message, PacketBuffer buf) {
		buf.writeDouble(message.x);
		buf.writeDouble(message.y);
		buf.writeDouble(message.z);
		buf.writeDouble(message.dx);
		buf.writeDouble(message.dy);
		buf.writeDouble(message.dz);
		buf.writeInt(message.id);
		buf.writeInt(message.dimension);
	}

	@Override
	public void handleClientSide(MessageEntityPosVelUpdate message, PlayerEntity player) {
		if (player.dimension.getId() == message.dimension) {
			Entity entity = player.world.getEntityByID(message.id);
			if (entity != null) {
				entity.posX = message.x;
				entity.posY = message.y;
				entity.posZ = message.z;
				entity.setMotion(message.dx, message.dy, message.dz);
			}
			else {
				Main.LOGGER.info("Entity Not Found");
			}
		}
	}

	@Override
	public void handleServerSide(MessageEntityPosVelUpdate message, PlayerEntity player) {
		
	}

}

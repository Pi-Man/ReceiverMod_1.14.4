package piman.recievermod.network.messages;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import piman.recievermod.Main;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.util.CapUtils;

public class MessageUpdateNBT extends MessageBase<MessageUpdateNBT> {
	
	private ItemStack stack;
	private int slot;
	private CompoundNBT nbt;

	public MessageUpdateNBT() {}

	public MessageUpdateNBT(ItemStack stack, int slot, CompoundNBT nbt) {
		this.stack = stack;
		this.slot = slot;
		this.nbt = nbt;
	}
	
	@Override
	public MessageUpdateNBT fromBytes(PacketBuffer buf) {

		MessageUpdateNBT message = new MessageUpdateNBT();

		message.stack = buf.readItemStack();
		message.slot = buf.readInt();
		message.nbt = buf.readCompoundTag();

		return message;
	}

	@Override
	public void toBytes(MessageUpdateNBT message, PacketBuffer buf) {
		buf.writeItemStack(message.stack);
		buf.writeInt(slot);
		buf.writeCompoundTag(message.nbt);
	}

	@Override
	public void handleClientSide(MessageUpdateNBT message, PlayerEntity player) {
		ItemStack stack1 = player.inventory.getStackInSlot(message.slot);
		ItemStack stack2 = message.stack;
						
		stack1.setTag(stack2.getOrCreateTag());
		
		CompoundNBT dataTag = CapUtils.getCap(player.world, ItemDataProvider.ITEMDATA_CAP, null).getItemData();
		CompoundNBT itemTag = getItemTag(dataTag, stack2.getOrCreateTag().getString("UUID"));
		itemTag.merge(message.nbt);
		
		CapUtils.getCap(player.world, ItemDataProvider.ITEMDATA_CAP, null).setItemData(dataTag);
	}

	@Override
	public void handleServerSide(MessageUpdateNBT message, PlayerEntity player) {
		ItemStack stack1 = message.stack;
		//Main.LOGGER.info(message.nbt);
		ItemStack stack2 = player.inventory.getStackInSlot(message.slot);
						
		if (stack1.getItem() != stack2.getItem()) {
			stack2 = player.getHeldItemOffhand();
		}
		if (stack1.getItem() != stack2.getItem()) {
			Main.LOGGER.info("Items are not Equal");
			return;
		}
		
		String uuid = null;
		short status = -1; 
		
		for ( World world : ServerLifecycleHooks.getCurrentServer().getWorlds()) {
			IItemData itemData = CapUtils.getCap(world, ItemDataProvider.ITEMDATA_CAP, null);
			
			CompoundNBT baseTag = itemData.getItemData();
			CompoundNBT itemTag = null;
			if (uuid == null) {
				if (message.nbt.getString("UUID").isEmpty()) {
					if (!stack2.getOrCreateTag().getString("UUID").isEmpty()) {
						uuid = stack2.getOrCreateTag().getString("UUID");
						itemTag = getItemTag(baseTag, uuid);
						status = 0;
					}
					else {
						uuid = UUID.randomUUID().toString();
						CompoundNBT nbt = new CompoundNBT();
						nbt.putString("UUID", uuid);
						stack2.getOrCreateTag().merge(nbt);
						Main.LOGGER.info("Set UUID To: {}", uuid);
						itemTag = getItemTag(baseTag, uuid);
						itemTag.merge(message.nbt);
						itemTag.putString("UUID", uuid);
						status = 1;
						itemData.setItemData(baseTag);
					}
					NetworkHandler.sendToAll(new MessageUpdateNBT(stack2, message.slot, itemTag));
				}
				else {
					itemTag = getItemTag(baseTag, message.nbt.getString("UUID"));
					itemTag.merge(message.nbt);
					status = 2;
					itemData.setItemData(baseTag);
				}
			}
			else {
				if (status == 0) {
					itemTag = getItemTag(baseTag, uuid);
				}
				else if (status == 1) {
					itemTag = getItemTag(baseTag, uuid);
					itemTag.merge(message.nbt);
					itemData.setItemData(baseTag);
				}
				else if (status == 2) {
					itemTag = getItemTag(baseTag, uuid);
					itemTag.merge(message.nbt);
					itemData.setItemData(baseTag);
				}
			}
		}
	}
	
	private CompoundNBT getItemTag(CompoundNBT baseTag, String key) {
		CompoundNBT itemTag;
		if (baseTag.contains(key, 10)) {
			itemTag = baseTag.getCompound(key);
		}
		else {
			itemTag = new CompoundNBT();
			baseTag.put(key, itemTag);
		}
		return itemTag;
	}
}

package piman.recievermod.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import piman.recievermod.Main;

public class MessageAddToInventory extends MessageBase<MessageAddToInventory> {
	
	private ItemStack stack;
	private int amount;
	private int slot;

	public MessageAddToInventory() {}
	
	public MessageAddToInventory(ItemStack stack, int amount) {
		this(stack, amount, -1);
	}
	
	public MessageAddToInventory(Item item, int amount) {
		this(new ItemStack(item), amount);
	}

	public MessageAddToInventory(ItemStack stack, int amount, int slot) {
		this.stack = stack;
		this.amount = amount;
		this.slot = slot;
	}

	@Override
	public MessageAddToInventory fromBytes(PacketBuffer buf) {
		ItemStack stack = buf.readItemStack();
		int amount = buf.readInt();
		int slot = buf.readInt();

		return new MessageAddToInventory(stack, amount, slot);
	}

	@Override
	public void toBytes(MessageAddToInventory message, PacketBuffer buf) {
		buf.writeItemStack(message.stack);
		buf.writeInt(message.amount);
		buf.writeInt(message.slot);
	}

	@Override
	public void handleClientSide(MessageAddToInventory message, PlayerEntity player) {
		
	}

	@Override
	public void handleServerSide(MessageAddToInventory message, PlayerEntity player) {
		
		ItemStack stack1 = message.stack;
				
		if (message.slot != -1) {
			if (message.amount < 0) {
				ItemStack stack2 = player.inventory.getStackInSlot(message.slot);
				if (stack1.getItem() == stack2.getItem()) {
					stack2.grow(message.amount);
					return;
				}
				System.out.println("Unable to Remove Item From Requested Slot");
			}
			if (message.amount > 0) {
				if (player.inventory.getStackInSlot(message.slot).isEmpty()) {
					player.inventory.setInventorySlotContents(message.slot, stack1);
					return;
				}
				System.out.println("Unable to Add Item To Requested Slot");
			}
		}
		
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			
			ItemStack stack2 = player.inventory.getStackInSlot(i);
			
			if (ItemStack.areItemsEqual(stack1, stack2)) {
				
				if (ItemStack.areItemStackTagsEqual(stack1, stack2)) {
					
					stack2.grow(message.amount);
					
					if (stack2.getCount() > stack2.getMaxStackSize()) {
						
						ItemStack stack3 = stack2.split(stack2.getMaxStackSize());
						player.inventory.addItemStackToInventory(stack3);
						
					}
					return;
				}
			}
		}
		Main.LOGGER.info("Unable to Find Stack, slot: {}", message.slot);
		stack1.setCount(message.amount);
		if (!player.inventory.addItemStackToInventory(stack1)) {
			Main.LOGGER.info("dropping Item");
			player.dropItem(stack1, false, true);
		}
	}
}

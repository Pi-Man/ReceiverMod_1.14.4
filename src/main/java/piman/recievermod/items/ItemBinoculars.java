package piman.recievermod.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ItemBinoculars extends Item {

    public ItemBinoculars(Item.Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onFOVUpdate(FOVUpdateEvent event) {
        PlayerEntity player = event.getEntity();
        if (player.getActiveItemStack().getItem() instanceof ItemBinoculars) {
            event.setNewfov(event.getFov() / 10f);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 7200;
    }
}

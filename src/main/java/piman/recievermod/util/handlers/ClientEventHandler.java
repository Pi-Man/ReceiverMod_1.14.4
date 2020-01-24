package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.init.ModItems;
import piman.recievermod.items.guns.ItemGun;

@Mod.EventBusSubscriber
public class ClientEventHandler {

    private static boolean cancleBob;
    private static boolean bob;
    private static double mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;

    @SubscribeEvent
    public static void renderOverlayEvent(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (Minecraft.getInstance().player.getHeldItemMainhand().getItem() instanceof ItemGun) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void FOVEvent(FOVUpdateEvent event) {
        if (Minecraft.getInstance().player.getHeldItemMainhand().getItem() instanceof ItemGun) {
            ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
            ItemGun gun = (ItemGun) Minecraft.getInstance().player.getHeldItemMainhand().getItem();

            Minecraft.getInstance().world.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
                CompoundNBT nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
                if (nbt.getBoolean("ads")) {
                    event.setNewfov(event.getFov() * gun.getDefaultZoomFactor(stack));
                }
            });

        }
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            bob = Minecraft.getInstance().gameSettings.viewBobbing;
            if (cancleBob) {
                Minecraft.getInstance().gameSettings.viewBobbing = false;
            }
            mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;
            if (Minecraft.getInstance().player != null) {
                ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
                if (stack.getItem() instanceof ItemGun) {
                    ItemGun gun = (ItemGun)stack.getItem();
                    Minecraft.getInstance().world.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
                        CompoundNBT nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
                        if (nbt.getBoolean("ads")) {
                            float f = gun.getDefaultZoomFactor(stack);
                            Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity * f;
                        }
                    });
                }
            }
        }
        if (event.phase == TickEvent.Phase.END) {
            Minecraft.getInstance().gameSettings.viewBobbing = bob;
            Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity;
        }
    }

    @SubscribeEvent
    public static void ClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            cancleBob = false;
            mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;
            if (Minecraft.getInstance().player != null) {
                ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
                if (stack.getItem() instanceof ItemGun) {
                    ItemGun gun = (ItemGun)stack.getItem();
                    Minecraft.getInstance().world.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
                        CompoundNBT nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
                        if (nbt.getBoolean("ads")) {
                            float f = gun.getDefaultZoomFactor(stack);
                            Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity * f;
                        }
                    });
                }
            }
        }
        else if (event.phase == TickEvent.Phase.END) {
            Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity;
        }

    }

    public static void cancleBob() {
        cancleBob = true;
    }

}

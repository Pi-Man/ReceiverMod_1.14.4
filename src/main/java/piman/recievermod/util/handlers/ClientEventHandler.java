package piman.recievermod.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.items.guns.ItemGun;

@Mod.EventBusSubscriber
public class ClientEventHandler {

    private static boolean cancleBob;
    private static boolean prevCancleBob;
    private static boolean bob = Minecraft.getInstance().gameSettings.viewBobbing;

    private static double setMouseSensitivity = -1;
    private static boolean prevSetMouseSensitivity;
    private static double mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;

    public static void editMouseSensitivity() {
        if (Minecraft.getInstance().player != null) {
            ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
            if (stack.getItem() instanceof ItemGun) {
                ItemGun gun = (ItemGun)stack.getItem();
                Minecraft.getInstance().world.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
                    CompoundNBT nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
                    if (nbt.getBoolean("ads")) {
                        float f = gun.getDefaultZoomFactor(stack);
                        setMouseSensitivity = mouseSensitivity * f;
                    }
                });
            }

            if (setMouseSensitivity != -1) {
                Minecraft.getInstance().gameSettings.mouseSensitivity = setMouseSensitivity;
                setMouseSensitivity = -1;
                prevSetMouseSensitivity = true;
            }
            else if (prevSetMouseSensitivity) {
                Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity;
                prevSetMouseSensitivity = false;
            }
            else {
                mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;
            }

            if (cancleBob) {
                Minecraft.getInstance().gameSettings.viewBobbing = false;
                cancleBob = false;
                prevCancleBob = true;
            }
            else if (prevCancleBob) {
                Minecraft.getInstance().gameSettings.viewBobbing = bob;
                prevCancleBob = false;
            }
            else {
                bob = Minecraft.getInstance().gameSettings.viewBobbing;
            }
        }
    }

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
            //bob = Minecraft.getInstance().gameSettings.viewBobbing;
            if (cancleBob) {
                //Minecraft.getInstance().gameSettings.viewBobbing = false;
            }
        }
        if (event.phase == TickEvent.Phase.END) {
            //Minecraft.getInstance().gameSettings.viewBobbing = bob;
            //System.out.println("Minecraft.getInstance().gameSettings.mouseSensitivity = " + Minecraft.getInstance().gameSettings.mouseSensitivity);
            //Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity;
            //Minecraft.getInstance().enqueue(ClientEventHandler::editMouseSensitivity);
        }
    }

    @SubscribeEvent
    public static void ClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            //cancleBob = false;
            if (Minecraft.getInstance().player != null) {
                ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
                if (stack.getItem() instanceof ItemGun) {
                    ItemGun gun = (ItemGun)stack.getItem();
                    Minecraft.getInstance().world.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(itemData -> {
                        CompoundNBT nbt = itemData.getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
                        if (nbt.getBoolean("ads")) {
                            float f = gun.getDefaultZoomFactor(stack);
                            //Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity * f;
                        }
                        else {
                            //mouseSensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;
                        }
                    });
                }
            }
        }
        else if (event.phase == TickEvent.Phase.START) {
            //Minecraft.getInstance().gameSettings.mouseSensitivity = mouseSensitivity;
            Minecraft.getInstance().enqueue(ClientEventHandler::editMouseSensitivity);
        }

    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ItemGun) {
                event.getRenderer().getEntityModel().leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
                event.getRenderer().getEntityModel().rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
            }
        }
    }

    public static void cancleBob() {
        cancleBob = true;
    }

}

package piman.recievermod.items.animations;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.guns.ItemGun;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageEject;
import piman.recievermod.network.messages.MessagePlaySound;
import piman.recievermod.util.SoundsHandler;

import java.util.ArrayList;
import java.util.List;

public class AnimationControllerBolt implements IAnimationController {
    @Override
    public List<ItemPropertyWrapper> getProperties() {

        List<ItemPropertyWrapper> list = new ArrayList<>();

        list.add(IAnimationController.integerProperty("boltup", true));
        list.add(IAnimationController.integerProperty("boltback", true));

        return list;
    }

    @Override
    public void update(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected, CompoundNBT nbt, ItemGun gun) {

        boolean flag = player.getHeldItemMainhand().equals(stack);

        if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.SlideLock) && nbt.getInt("boltback") == 0) {
            if (nbt.getBoolean("up")) {
                NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_RIFLE_BOLTDOWN));
            }
            else {
                NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_RIFLE_BOLTUP));
            }
            nbt.putBoolean("up", !nbt.getBoolean("up"));
        }
        if (flag && KeyInputHandler.isKeyPressed(KeyInputHandler.KeyPresses.RemoveBullet) && nbt.getInt("boltup") == 2) {
            if (nbt.getBoolean("back")) {
                NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_RIFLE_BOLTFORWARD));
            }
            else {
                NetworkHandler.sendToServer(new MessagePlaySound(SoundsHandler.ITEM_RIFLE_BOLTBACK));
            }
            nbt.putBoolean("back", !nbt.getBoolean("back"));
        }

        if (nbt.getBoolean("up")) {
            if (nbt.getInt("boltup") < 2) {
                nbt.putInt("boltup", nbt.getInt("boltup") + 1);
            }
        }
        else {
            if (nbt.getInt("boltup") > 0) {
                nbt.putInt("boltup", nbt.getInt("boltup") - 1);
            }
        }

        if (nbt.getBoolean("back")) {
            if (nbt.getInt("boltback") < 2) {
                nbt.putInt("boltback", nbt.getInt("boltback") + 1);
            }
        }
        else {
            if (nbt.getInt("boltback") > 0) {
                nbt.putInt("boltback", nbt.getInt("boltback") - 1);
            }
        }

        if (nbt.getBoolean("up")) {
            nbt.putBoolean("hammer", true);
        }

        if (nbt.getInt("boltback") == 1) {
            if (nbt.getBoolean("back")) {
                if (!nbt.getString("BulletChambered").isEmpty()) {
                    NetworkHandler.sendToServer(new MessageEject(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("BulletChambered"))))));
                    nbt.putString("BulletChambered", "");
                }
            }
            else {
                if (nbt.getList("bullets", 8).size() > 0 && nbt.getString("BulletChambered").isEmpty()) {
                    nbt.putString("BulletChambered", nbt.getList("bullets", 8).getString(nbt.getList("bullets", 8).size() - 1));
                    nbt.getList("bullets", 8).remove(nbt.getList("bullets", 8).size() - 1);
                }
            }
        }

    }
}

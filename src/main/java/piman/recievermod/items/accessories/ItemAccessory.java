package piman.recievermod.items.accessories;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.IAnimationController;
import piman.recievermod.util.Reference;

import javax.annotation.Nullable;

public abstract class ItemAccessory extends Item {

    private AccessoryType type;

    public ItemAccessory(Item.Properties properties, AccessoryType type) {
        super(properties);
        this.addPropertyOverride(new ResourceLocation("model"), new IItemPropertyGetter() {
            @Override
            public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                return stack.getOrCreateTag().getBoolean("model") ? 1 : 0;
            }
        });
        this.type = type;
    }

    public AccessoryType getType() {
        return type;
    }

    public int getSlot() {
        return type.getSlot();
    }

    public enum AccessoryType {
        SCOPE(1);

        private int slot;

        AccessoryType(int slot) {
            this.slot = slot;
        }

        static public AccessoryType getTypeBySlot(int slot) {
            switch (slot) {
                case 1:
                    return SCOPE;
                default:
                    return null;
            }
        }

        public int getSlot() {
            return slot;
        }
    }
}

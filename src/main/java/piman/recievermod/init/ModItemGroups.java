package piman.recievermod.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup TOOLS = new ItemGroup("tools") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.BINOCULARS);
        }
    };

    public static final ItemGroup GUNS = new ItemGroup("guns") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COLT_1911);
        }
    };

}

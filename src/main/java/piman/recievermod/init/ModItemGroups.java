package piman.recievermod.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup TOOLS = new ItemGroup(-1, "tools") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.BINOCULARS);
        }
    };

}

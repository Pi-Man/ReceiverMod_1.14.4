package piman.recievermod.items.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BulletCrafterRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BulletCrafterRecipe> {

    @Nonnull
    @Override
    public BulletCrafterRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {

        String name = JSONUtils.getString(json, "name", "");
        int count = JSONUtils.getInt(json, "count", 1);
        int redstone = JSONUtils.getInt(json, "redstone", 1);
        int gunpoweder = JSONUtils.getInt(json, "gunpowder", 1);
        int iron = JSONUtils.getInt(json, "iron", 1);

        return new BulletCrafterRecipe(recipeId, Ingredient.fromStacks(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name + "casing")), count)),
                                                 Ingredient.fromStacks(new ItemStack(Items.REDSTONE, redstone)),
                                                 Ingredient.fromStacks(new ItemStack(Items.GUNPOWDER, gunpoweder)),
                                                 Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT, iron)),
                                                 new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)), count));
    }

    @Nullable
    @Override
    public BulletCrafterRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        return new BulletCrafterRecipe(recipeId, buffer);
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull BulletCrafterRecipe recipe) {
        recipe.toPacketBuffer(buffer);
    }

}

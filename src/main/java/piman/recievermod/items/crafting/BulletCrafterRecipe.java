package piman.recievermod.items.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import piman.recievermod.init.ModBlocks;
import piman.recievermod.init.ModRecipeSerializers;
import piman.recievermod.util.handlers.RegistryEventHandler;

import javax.annotation.Nonnull;

public class BulletCrafterRecipe implements IRecipe<IInventory> {

    private ResourceLocation location;

    private Ingredient casing;
    private Ingredient primer;
    private Ingredient propellant;
    private Ingredient bullet;

    private ItemStack result;

    public BulletCrafterRecipe(ResourceLocation location, PacketBuffer buffer) {

        this.location = location;

        this.casing = Ingredient.read(buffer);
        this.primer = Ingredient.read(buffer);
        this.propellant = Ingredient.read(buffer);
        this.bullet = Ingredient.read(buffer);

        this.result = buffer.readItemStack();

    }

    public BulletCrafterRecipe(ResourceLocation location, Ingredient casing, Ingredient primer, Ingredient propellant, Ingredient bullet, ItemStack result) {

        this.location = location;

        this.casing = casing;
        this.primer = primer;
        this.propellant = propellant;
        this.bullet = bullet;

        this.result = result;

    }

    public void toPacketBuffer(PacketBuffer buffer) {

        casing.write(buffer);
        primer.write(buffer);
        propellant.write(buffer);
        bullet.write(buffer);

        buffer.writeItemStack(result);

    }

    @Override
    public boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) {
        return matchesStack(casing, inv.getStackInSlot(0), worldIn) && matchesStack(primer, inv.getStackInSlot(1), worldIn) && matchesStack(propellant, inv.getStackInSlot(2), worldIn) && matchesStack(bullet, inv.getStackInSlot(3), worldIn);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.from(Ingredient.EMPTY, casing, primer, propellant, bullet);
    }

    public boolean matchesStack(Ingredient ingredient, ItemStack itemStack, World worldIn) {
        ItemStack[] itemStacks = ingredient.getMatchingStacks();

        for (ItemStack itemStack1 : itemStacks) {
            if (ItemStack.areItemsEqual(itemStack, itemStack1) && itemStack.getCount() >= itemStack1.getCount()) {
                return true;
            }
        }

        return false;
    }

    public ItemStack reduceStack(Ingredient ingredient, ItemStack itemStack, World worldIn) {
        ItemStack[] itemStacks = ingredient.getMatchingStacks();

        for (ItemStack itemStack1 : itemStacks) {
            if (ItemStack.areItemsEqual(itemStack, itemStack1) && itemStack.getCount() >= itemStack1.getCount()) {
                itemStack.shrink(itemStack1.getCount());
                break;
            }
        }

        return itemStack;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        return result.copy();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     *
     * @param width
     * @param height
     */
    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(ModBlocks.BULLET_CRAFTER);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return location;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BULLET_CRAFTER;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return RegistryEventHandler.BULLET_CRAFTER;
    }
}

package piman.recievermod.tileentity;

import java.util.List;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import piman.recievermod.block.BlockBulletCrafter;
import piman.recievermod.init.ModTileEntityTypes;
import piman.recievermod.inventory.container.ContainerBulletCrafter;
import piman.recievermod.items.bullets.ItemBulletMediumCasing;
import piman.recievermod.items.crafting.BulletCrafterRecipe;
import piman.recievermod.util.handlers.RegistryEventHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBulletCrafter extends TileEntity implements ITickableTileEntity, ISidedInventory, INamedContainerProvider {
	
	private static final int[] SLOTS_TOP = new int[] {0, 1, 2, 3};
    private static final int[] SLOTS_BOTTOM = new int[] {4};
    private static final int[] SLOTS_SIDES = new int[] {0, 1, 2, 3};
    /** The ItemStacks that hold the items currently being used in the crafter */
    private NonNullList<ItemStack> ItemStacks = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
    private int cookTime;
    private int totalCookTime;
    private String CustomName;

    private IIntArray intArray = new IIntArray() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return cookTime;
            }
            else if (index == 1) {
                return totalCookTime;
            }
            else return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                cookTime = value;
            }
            else if (index == 1) {
                totalCookTime = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public TileEntityBulletCrafter() {
        super(ModTileEntityTypes.BULLET_CRAFTER);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.ItemStacks.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.ItemStacks)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Nonnull
    public ItemStack getStackInSlot(int index)
    {
        return this.ItemStacks.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Nonnull
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.ItemStacks, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Nonnull
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.ItemStacks, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack itemstack = this.ItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.ItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag)
        {
            this.totalCookTime = this.getCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.CustomName : "container.bullet_crafter";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.CustomName != null && !this.CustomName.isEmpty();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
    	return new TranslationTextComponent(getName());
    }

    public void setCustomInventoryName(String p_145951_1_)
    {
        this.CustomName = p_145951_1_;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.ItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.ItemStacks);
        this.cookTime = compound.getInt("CookTime");
        this.totalCookTime = compound.getInt("CookTimeTotal");

        if (compound.contains("CustomName", 8))
        {
            this.CustomName = compound.getString("CustomName");
        }
    }

    @Nonnull
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putInt("CookTime", (short)this.cookTime);
        compound.putInt("CookTimeTotal", (short)this.totalCookTime);
        ItemStackHelper.saveAllItems(compound, this.ItemStacks);

        if (this.hasCustomName())
        {
            compound.putString("CustomName", this.CustomName);
        }

        return compound;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }
    
    /**
     * Crafter isCrafting
     */
    public boolean isCrafting()
    {
        return this.cookTime > 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    @Override
    public void tick()
    {
        boolean flag = this.isCrafting();
        boolean flag1 = false;

        if (!this.world.isRemote)
        {
            ItemStack itemstack = this.ItemStacks.get(1);

            if (this.isCrafting() || !itemstack.isEmpty() && !((ItemStack)this.ItemStacks.get(0)).isEmpty())
            {

                BulletCrafterRecipe recipe = this.world.getRecipeManager().getRecipe(RegistryEventHandler.BULLET_CRAFTER, this, world).orElse(null);

                if (!this.isCrafting() && this.canSmelt(recipe))
                {
                    flag1 = true;
                    this.totalCookTime = this.getCookTime(itemstack);
                    this.cookTime = 1;
                }

                if (this.isCrafting() && this.canSmelt(recipe))
                {
                    ++this.cookTime;

                    if (this.cookTime == this.totalCookTime)
                    {
                        this.cookTime = 0;
                        this.totalCookTime = this.getCookTime(this.ItemStacks.get(0));
                        this.smeltItem(recipe);
                        flag1 = true;
                    }
                }
                else
                {
                    this.cookTime = 0;
                }
            }
            else if (!this.isCrafting() && this.cookTime > 0)
            {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }

            if (flag != this.isCrafting())
            {
                flag1 = true;
                BlockBulletCrafter.setState(this.isCrafting(), this.world, this.pos);
            }
        }

        if (flag1)
        {
            this.markDirty();
        }
    }

//    @Override
//    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
//    	return oldState.getBlock() != newSate.getBlock();
//    }
    
    public int getCookTime(ItemStack stack)
    {
        return 20*32;
    }

    /**
     * Returns true if the  can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt(BulletCrafterRecipe recipe) {
        if (recipe == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = recipe.getRecipeOutput();

            if (itemstack.isEmpty())
            {
                return false;
            }
            else
            {
                ItemStack itemstack1 = this.ItemStacks.get(4);

                if (itemstack1.isEmpty())
                {
                    return true;
                }
                else if (!itemstack1.isItemEqual(itemstack))
                {
                    return false;
                }
                else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize())  // Forge fix: make  respect stack sizes in  recipes
                {
                    return true;
                }
                else
                {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make  respect stack sizes in  recipes
                }
            }
        }
    }

    /**
     * Turn one item from the  source stack into the appropriate smelted item in the  result stack
     */
    public void smeltItem(BulletCrafterRecipe recipe)
    {
        if (this.canSmelt(recipe))
        {
            List<ItemStack> inputs = this.ItemStacks.subList(0, 4);
            List<Ingredient> ingredients = recipe.getIngredients();
            ItemStack itemstack1 = recipe.getCraftingResult(this);
            ItemStack itemstack2 = this.ItemStacks.get(4);

            if (itemstack2.isEmpty())
            {
                this.ItemStacks.set(4, itemstack1.copy());
            }
            else if (itemstack2.getItem() == itemstack1.getItem())
            {
                itemstack2.grow(itemstack1.getCount());
            }

            for (int i = 0; i < inputs.size(); i++) {
            	ItemStack input = inputs.get(i);
            	recipe.reduceStack(ingredients.get(i), input, world);
            }
        }
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        }
        else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {    	
        if (index == 0) {
			return stack.getItem() instanceof ItemBulletMediumCasing;
        }
        else if (index == 1) {
        	return stack.getItem() == Items.REDSTONE;
        }
        else if (index == 2) {
        	return stack.getItem() == Items.GUNPOWDER;
        }
        else if (index == 3) {
        	return stack.getItem() == Items.IRON_INGOT;
        }
        else {
            return false;
        }
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side)
    {
        if (side == Direction.DOWN)
        {
            return SLOTS_BOTTOM;
        }
        else
        {
            return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
        }
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     */
    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, Direction direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     */
    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull Direction direction) {
        return direction == Direction.DOWN && index == 4;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new ContainerBulletCrafter(id, playerInventory, this, intArray);
    }

    @Override
    public void clear()
    {
        this.ItemStacks.clear();
    }

    LazyOptional<net.minecraftforge.items.IItemHandler> handlerTop = LazyOptional.of(() -> new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.Direction.UP));
    LazyOptional<net.minecraftforge.items.IItemHandler> handlerBottom = LazyOptional.of(() -> new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.Direction.DOWN));
    LazyOptional<net.minecraftforge.items.IItemHandler> handlerSide = LazyOptional.of(() -> new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.Direction.WEST));

    //@SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.DOWN) {
                return (LazyOptional<T>) handlerBottom;
            }
            else if (facing == Direction.UP) {
                return (LazyOptional<T>) handlerTop;
            }
            else {
                return (LazyOptional<T>) handlerSide;
            }
        }
        return super.getCapability(capability, facing);
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getTotalCookTime() {
        return totalCookTime;
    }

}

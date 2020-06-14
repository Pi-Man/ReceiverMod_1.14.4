package piman.recievermod.items.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import piman.recievermod.capabilities.itemdata.IItemData;
import piman.recievermod.capabilities.itemdata.ItemDataProvider;
import piman.recievermod.init.ModItemGroups;
import piman.recievermod.items.IItemInit;
import piman.recievermod.items.ItemPropertyWrapper;
import piman.recievermod.items.animations.IAnimationController;
import piman.recievermod.items.bullets.ItemBullet;
import piman.recievermod.items.mags.ItemMag;
import piman.recievermod.keybinding.KeyInputHandler;
import piman.recievermod.network.NetworkHandler;
import piman.recievermod.network.messages.MessageShoot;
import piman.recievermod.network.messages.MessageUpdateNBT;
import piman.recievermod.util.handlers.ClientEventHandler;

public abstract class ItemGun extends Item implements IItemInit {

    //private static final IItemPropertyGetter ADS_GETTER = new BooleanPropertyGetter("ADS");

    private static final IItemPropertyGetter BULLET_CHAMBERED_GETTER = new IItemPropertyGetter()
    {
        @Override
        @OnlyIn(Dist.CLIENT)
        public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
        {
            if (worldIn == null) {
                worldIn = Minecraft.getInstance().world;
            }

            LazyOptional<IItemData> lazyOptional = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP, null);

            if (worldIn == null || !lazyOptional.isPresent() || !stack.hasTag()) {
                return 0.0F;
            }
            CompoundNBT nbt = lazyOptional.orElse(null).getItemData().getCompound(stack.getOrCreateTag().getString("UUID"));
            float j = !nbt.getString("BulletChambered").isEmpty() ? 1.0F : 0.0F;
            return j;
        }
    };

    //private static final IItemPropertyGetter FIRED_GETTER = new BooleanPropertyGetter("fired");

    protected double spreadY;
    protected double spreadX;
    protected double drift;
    protected float accuracy;
    protected List<IAnimationController> animationControllers = new ArrayList<>();
    public Supplier<ItemBullet> ammo;
    public Supplier<ItemBullet> casing;
    public Supplier<ItemMag> mag;

    public ItemGun(Item.Properties properties) {
        super(properties.maxStackSize(1).group(ModItemGroups.GUNS));
        this.addPropertyOverride(new ResourceLocation("chambered"), BULLET_CHAMBERED_GETTER);
    }

    @Override
    public void Init() {
        List<ItemPropertyWrapper> itemProperties = new ArrayList<>();
        animationControllers.forEach(controller -> itemProperties.addAll(controller.getProperties()));
        itemProperties.forEach(property -> addPropertyOverride(property.getName(), property.getOverride()));
    }

    public boolean Shoot(CompoundNBT nbt, LivingEntity entityLiving, double damage, float entityAccuracy, float gunAccuracy, int bullets) {
        return Shoot(nbt, entityLiving, damage, entityAccuracy, gunAccuracy, bullets, 1200);
    }

    public boolean Shoot(CompoundNBT nbt, LivingEntity entityLiving, double damage, float entityAccuracy, float gunAccuracy, int bullets, int life) {

        if (entityLiving instanceof PlayerEntity) {

            PlayerEntity player = ((PlayerEntity)entityLiving);
            World world = player.world;
            boolean flag1 = player.isCreative();

            if (world.isRemote) {
                NetworkHandler.sendToServer(new MessageShoot(nbt, damage, entityAccuracy, gunAccuracy, life, bullets));
            }

            int i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(player.getHeldItemMainhand(), world, player, 20, (flag1 || !nbt.getString("BulletChambered").isEmpty()));

            if (i < 0) {
                return false;
            }

            if ((flag1 || !nbt.getString("BulletChambered").isEmpty())) {

                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("BulletChambered")));

                boolean flag = false;

                if (!(item instanceof ItemBullet) && player.isCreative())  {
                    item = this.ammo.get();
                    flag = true;
                }

                if (item instanceof ItemBullet) {
                    ItemBullet itemBullet = (ItemBullet) item;
                    if (!world.isRemote) {
                        itemBullet.fire(world, player, entityAccuracy, gunAccuracy, life);
                    }
                    if (flag) {
                        nbt.putString("BulletChambered", "");
                    }
                    else {
                        nbt.putString("BulletChambered", itemBullet.getCasing().getRegistryName().toString());
                    }
                }
//                else if (!world.isRemote) {
//
//                    EntityBullet bulletdummy = new EntityBullet(world, player);
//                    bulletdummy.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 23F, entityAccuracy);
//
//                    for (int j = 0; j < bullets; j++) {
//
//                        EntityBullet bullet = new EntityBullet(world, player, life);
//                        double x = bulletdummy.motionX;
//                        double y = bulletdummy.motionY;
//                        double z = bulletdummy.motionZ;
//                        bullet.shoot(x, y, z, MathHelper.sqrt(x * x + y * y + z * z), gunAccuracy);
//
//                        bullet.posY += 0.1D;
//                        bullet.setIsCritical(false);
//                        bullet.setDamage(damage);
//
//                        player.world.spawnEntity(bullet);
//
//                    }
//
//                    //System.out.println("Spawning Bullet");
//
//                    world.playSound(null, player.posX, player.posY, player.posZ, getShootSound(), SoundCategory.PLAYERS, 1, 1);
//
//                }

                player.rotationPitch += world.rand.nextGaussian() * this.spreadY - this.drift;
                player.rotationYaw += world.rand.nextGaussian() * this.spreadX;

                //nbt.setBoolean("BulletChambered", false);


                return true;
            }
        }
        return false;
    }

    public abstract SoundEvent getShootSound();

//    public float getZoomFactor(ItemStack stack) {
//        if (stack.getOrCreateTag().contains("Accessories", 10)) {
//            ItemAccessories item = (ItemAccessories) ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.getNBTTag(stack).getCompoundTag("Accessories").getString("1")));
//            if (item != null) {
//                return item.getZoomFactor();
//            }
//        }
//        return this.getDefaultZoomFactor(stack);
//    }

    public abstract float getDefaultZoomFactor(ItemStack stack);

    public float getAccuracy() {
        return accuracy;
    }

    public boolean hasAccessories(ItemStack stack) {
        return false;
    }

    public boolean hasAccessory(ItemStack stack, int type) {
        return stack.getOrCreateChildTag("Accessories").contains(Integer.valueOf(type).toString(), 8) && !stack.getOrCreateChildTag("Accessories").getString(Integer.valueOf(type).toString()).isEmpty();
    }

//    public List<ItemStack> getAccesories(ItemStack stack) {
//
//        if (stack.getOrCreateTag().contains("Accessories", 10)) {
//            CompoundNBT nbt = stack.getOrCreateChildTag("Accessories");
//
//            List<ItemStack> list = new ArrayList<ItemStack>();
//
//            for (int i = 0; i < 9; i++) {
//                ItemStack accessory = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString(Integer.toString(i)))));
//                if (!accessory.isEmpty()) {
//                    accessory.getOrCreateTag().putBoolean("model", true);
//                    accessory.getOrCreateTag().putString("UUID", stack.getOrCreateTag().getString("UUID"));
//                    accessory.getOrCreateTag().putIntArray("transform", this.getAccessoryTransformInts(((ItemAccessories)accessory.getItem()).getType()));
//                    list.add(accessory);
//                }
//            }
//
//            return list;
//        }
//        else {
//            return null;
//        }
//    }

    private int [] getAccessoryTransformInts(int type) {

        int [] ints = new int[16];

        Matrix4f m = this.getAccessoryTransform(type);

        for (int i = 0; i < 16; i++) {
            ints[i] = Float.floatToIntBits(m.getElement(i / 4, i % 4));
        }

        return ints;
    }

    public Matrix4f getAccessoryTransform(int type) {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return m;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) {

            if (entityIn instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entityIn;

                if (player.getHeldItemMainhand().equals(stack)) {
                    if (KeyInputHandler.isKeyDown(KeyInputHandler.KeyPresses.Shift)) {
                        ClientEventHandler.cancleBob();
                    }
                }

                worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP).ifPresent(cap -> {
                    CompoundNBT baseTag = cap.getItemData();

                    CompoundNBT nbt = baseTag.getCompound(stack.getOrCreateTag().getString("UUID"));

                    CompoundNBT oldnbt = nbt.copy();
                    oldnbt.remove("prev");
                    nbt.put("prev", oldnbt);

                    animationControllers.forEach(controller -> controller.update(stack, worldIn, player, itemSlot, isSelected, nbt, (ItemGun) stack.getItem()));

                    NetworkHandler.sendToServer(new MessageUpdateNBT(stack, itemSlot, nbt));
                });
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        boolean flag = false;

        if (oldStack.getItem() instanceof ItemGun && newStack.getItem() instanceof ItemGun) {
            flag = oldStack.getOrCreateTag().getString("UUID").equals(newStack.getOrCreateTag().getString("UUID"));
        }

        return slotChanged || !flag;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("ID: " + stack.getOrCreateTag().getString("UUID")));
    }

    public int findAmmo(PlayerEntity player) {
        for (int i = -1; i < player.inventory.getSizeInventory() - 1; ++i)
        {
            int k = i < 0 ? player.inventory.getSizeInventory() - 1 : i;

            ItemStack itemstack = player.inventory.getStackInSlot(k);

            if (this.isAmmo(itemstack))
            {
                return k;
            }
        }

        return -1;
    }

    public int findMag(PlayerEntity player) {
        for (int i = -1; i < player.inventory.getSizeInventory() - 1; ++i) {
            int k = i < 0 ? player.inventory.getSizeInventory() - 1 : i;

            ItemStack itemstack = player.inventory.getStackInSlot(k);

            if (this.isMag(itemstack)) {
                return k;
            }
        }

        return -1;
    }

    public int findFullestMag(PlayerEntity player, CompoundNBT baseTag) {
        TreeMap<Integer, Pair<ItemStack, Integer>> mags = new TreeMap<Integer, Pair<ItemStack, Integer>>();
        {
            ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.getSizeInventory() - 1);
            if (isMag(itemstack)) {
                return player.inventory.getSizeInventory() - 1;
            }
        }
        for (int i = 0; i < player.inventory.getSizeInventory() - 1; i++) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (isMag(itemstack)) {
                mags.put(baseTag.getCompound(itemstack.getOrCreateTag().getString("UUID")).getList("bullets", 8).size(), Pair.of(itemstack, i));
            }
        }
        if (!mags.isEmpty()) {
            return mags.lastEntry().getValue().getValue();
        }
        return -1;
    }

    public boolean isMag(ItemStack stack) {
        return stack.getItem() == mag.get();
    }

    private boolean isAmmo(ItemStack stack) {
        return stack.getItem() == ammo.get();
    }

//    protected static class BooleanPropertyGetter implements IItemPropertyGetter {
//
//        private final String name;
//
//        public BooleanPropertyGetter(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
//            if (worldIn == null) {
//                worldIn = Minecraft.getInstance().world;
//            }
//
//            if (worldIn == null || !worldIn.hasCapability(ItemDataProvider.ITEMDATA_CAP, null) || !stack.hasTagCompound()) {
//                return 0.0F;
//            }
//
//            NBTTagCompound nbt = worldIn.getCapability(ItemDataProvider.ITEMDATA_CAP, null).getItemData().getCompoundTag(stack.getTagCompound().getString("UUID"));
//            NBTTagCompound oldnbt = nbt.getCompoundTag("prev");
//
//            float pt = RenderPartialTickHandler.renderPartialTick;
//
//            float j = (oldnbt.getBoolean(name) ? 1.0F : 0.0F) * (1 - pt) + (nbt.getBoolean(name) ? 1.0F : 0.0F) * pt;
//
//            return j;
//        }
//
//    }
}

package piman.recievermod.init;


import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.items.IItemInit;
import piman.recievermod.items.ItemBinoculars;
import piman.recievermod.items.bullets.ItemBullet;
import piman.recievermod.items.bullets.ItemBulletMedium;
import piman.recievermod.items.bullets.ItemBulletMediumCasing;
import piman.recievermod.items.guns.*;
import piman.recievermod.items.mags.ItemMag;
import piman.recievermod.util.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ObjectHolder(Reference.MOD_ID)
public class ModItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static Map<Item, ResourceLocation> MODELS = new HashMap<>();

    public static final ItemGun COLT_1911 = null;
    public static final ItemMag COLT_MAG = null;
    public static final ItemGun GLOCK17 = null;
    public static final ItemMag GLOCK17_MAG = null;
    public static final ItemGun MODEL10 = null;
    public static final Item _GUN_44MAGNUM = null;
    public static final Item _GUN_BOLTRIFLE = null;
    public static final Item _GUN_REMINGTON870 = null;
    public static final ItemGun THOMPSON = null;
    public static final ItemMag THOMPSON_MAG = null;
    public static final Item _GUN_RPG7 = null;
    public static final Item RPG = null;
    public static final ItemGun M1_GARAND = null;
    public static final ItemMag M1_CLIP = null;
    public static final ItemGun LEE_ENFIELD = null;
    public static final ItemBullet BULLET45 = null;
    public static final ItemBullet BULLET45CASING = null;
    public static final ItemBullet BULLET9MM = null;
    public static final ItemBullet BULLET9MMCASING = null;
    public static final ItemBullet BULLET38SPECIAL = null;
    public static final ItemBullet BULLET38SPECIALCASING = null;
    public static final Item BULLET22 = null;
    public static final Item BULLET22CASING = null;
    public static final ItemBullet BULLET30_06 = null;
    public static final ItemBullet BULLET30_06CASING = null;
    public static final Item BULLETSHOTGUN = null;
    public static final Item BULLETSHOTGUNCASING = null;
    public static final Item LENS = null;
    public static final Item BINOCULARS = null;
    public static final Item FRAG_GRENADE = null;
    public static final Item CASSETTE = null;
    public static final Item SCOPE = null;

    public static Item[] getItemArray() {

        ITEMS.clear();

        ITEMS.add(new ItemColt(new Item.Properties()).setRegistryName(Reference.MOD_ID, "colt_1911"));
        putModel("_gun_colt");
        ITEMS.add(new ItemMag(new Item.Properties(), 7, () -> BULLET45).setRegistryName("colt_mag"));
        putModel("_mag_colt");

        ITEMS.add(new ItemGlock17(new Item.Properties()).setRegistryName(Reference.MOD_ID, "glock17"));
        putModel("_gun_glock");
        ITEMS.add(new ItemMag(new Item.Properties(), 17, () -> BULLET9MM).setRegistryName("glock17_mag"));
        putModel("_mag_glock");

        ITEMS.add(new ItemModel10(new Item.Properties()).setRegistryName("model10"));
        putModel("_gun_model_10");

        ITEMS.add(new ItemThompson(new Item.Properties()).setRegistryName(Reference.MOD_ID, "thompson"));
        putModel("_gun_thompson");
        ITEMS.add(new ItemMag(new Item.Properties(), 30, () -> ModItems.BULLET45).setRegistryName("thompson_mag"));

        ITEMS.add(new ItemGarand(new Item.Properties()).setRegistryName(Reference.MOD_ID, "m1_garand"));
        putModel("m1_garand_full.bbmodel");
        ITEMS.add(new ItemMag(new Item.Properties(), 8, () -> BULLET30_06).setRegistryName(Reference.MOD_ID, "m1_clip"));
        putModel("m1_garand_clip.bbmodel");

        ITEMS.add(new ItemLeeEnfield(new Item.Properties()).setRegistryName(Reference.MOD_ID, "lee_enfield"));
        putModel("lee_enfield.bbmodel");

        ITEMS.add(new ItemBulletMedium(new Item.Properties(), 0.45f, 20f, () -> ModItems.BULLET45CASING).setRegistryName("bullet45"));
        ITEMS.add(new ItemBulletMediumCasing(new Item.Properties()).setRegistryName(Reference.MOD_ID, "bullet45casing"));

        ITEMS.add(new ItemBulletMedium(new Item.Properties(), 0.3555f, 20f, () -> ModItems.BULLET9MMCASING).setRegistryName("bullet9mm"));
        putModel("bullet45");
        ITEMS.add(new ItemBulletMediumCasing(new Item.Properties()).setRegistryName(Reference.MOD_ID, "bullet9mmcasing"));
        putModel("bullet45casing");

        ITEMS.add(new ItemBulletMedium(new Item.Properties(), 0.38f, 20f, () -> ModItems.BULLET38SPECIALCASING).setRegistryName(Reference.MOD_ID, "bullet38special"));
        putModel("bullet45");
        ITEMS.add(new ItemBulletMediumCasing(new Item.Properties()).setRegistryName(Reference.MOD_ID, "bullet38specialcasing"));
        putModel("bullet45casing");

        ITEMS.add(new ItemBulletMedium(new Item.Properties(), 0.308f, 20f, () -> ModItems.BULLET30_06CASING).setRegistryName("bullet30_06"));
        putModel("bullet30-06.bbmodel");
        ITEMS.add(new ItemBulletMediumCasing(new Item.Properties()).setRegistryName(Reference.MOD_ID, "bullet30_06casing"));
        putModel("bullet30-06casing.bbmodel");

        ITEMS.add(new ItemBinoculars(new Item.Properties().maxStackSize(1).group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "binoculars"));

        ITEMS.add(new Item(new Item.Properties().group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "lens"));



        ITEMS.forEach(item -> {if (item instanceof IItemInit) ((IItemInit)item).Init();});

        return ITEMS.toArray(new Item[0]);

    }

    private static void putModel(String location) {
        MODELS.put(ITEMS.get(ITEMS.size() - 1), new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, location), "inventory"));
    }

}


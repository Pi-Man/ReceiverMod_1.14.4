package piman.recievermod.init;


import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.items.ItemBinoculars;
import piman.recievermod.items.bullets.ItemBulletMedium;
import piman.recievermod.items.bullets.ItemBulletMediumCasing;
import piman.recievermod.items.guns.ItemColt;
import piman.recievermod.items.mags.ItemMagColt;
import piman.recievermod.util.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ObjectHolder(Reference.MOD_ID)
public class ModItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static Map<Item, ResourceLocation> MODELS = new HashMap<>();

    public static final Item COLT_1911 = null;
    public static final Item COLT_MAG = null;
    public static final Item _GUN_GLOCK = null;
    public static final Item _CLIP_GLOCK = null;
    public static final Item _GUN_MODEL10 = null;
    public static final Item _GUN_44MAGNUM = null;
    public static final Item _GUN_BOLTRIFLE = null;
    public static final Item _GUN_REMINGTON870 = null;
    public static final Item _GUN_THOMPSON = null;
    public static final Item THOMPSON_CLIP = null;
    public static final Item _GUN_RPG7 = null;
    public static final Item RPG = null;
    public static final Item BULLET45 = null;
    public static final Item BULLET45CASING = null;
    public static final Item BULLET9MM = null;
    public static final Item BULLET9MMCASING = null;
    public static final Item BULLET38SPECIAL = null;
    public static final Item BULLET38SPECIALCASING = null;
    public static final Item BULLET22 = null;
    public static final Item BULLET22CASING = null;
    public static final Item BULLETSHOTGUN = null;
    public static final Item BULLETSHOTGUNCASING = null;
    public static final Item LENS = null;
    public static final Item BINOCULARS = null;
    public static final Item FRAG_GRENADE = null;
    public static final Item CASSETTE = null;
    public static final Item SCOPE = null;

    public static Item[] getItemArray() {

        ITEMS.clear();

        ITEMS.add(new ItemColt(new Item.Properties().group(ModItemGroups.GUNS)).setRegistryName(Reference.MOD_ID, "colt_1911"));
        putModel("_gun_colt");
        ITEMS.add(new ItemMagColt(new Item.Properties()).setRegistryName("colt_mag"));
        putModel("_mag_colt");
        ITEMS.add(new ItemBulletMedium(new Item.Properties(), 0.45f, 20f).setRegistryName("bullet45"));
        ITEMS.add(new ItemBulletMediumCasing((new Item.Properties())).setRegistryName(Reference.MOD_ID, "bullet45casing"));
        ITEMS.add(new ItemBinoculars(new Item.Properties().maxStackSize(1).group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "binoculars"));
        ITEMS.add(new Item(new Item.Properties().group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "lens"));

        return ITEMS.toArray(new Item[0]);

    }

    private static void putModel(String location) {
        MODELS.put(ITEMS.get(ITEMS.size() - 1), new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, location), "inventory"));
    }

}


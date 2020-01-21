package piman.recievermod.init;


import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.items.IItemInit;
import piman.recievermod.items.ItemBinoculars;
import piman.recievermod.items.guns.ItemColt;
import piman.recievermod.util.Reference;

import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Reference.MOD_ID)
public class ModItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static final Item _GUN_COLT = null;
    public static final Item _CLIP_COLT = null;
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

        ITEMS.add(new ItemColt(new Item.Properties().group(ModItemGroups.GUNS)).setRegistryName(Reference.MOD_ID, "_gun_colt"));
        ITEMS.add(new ItemBinoculars(new Item.Properties().maxStackSize(1).group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "binoculars"));
        ITEMS.add(new Item(new Item.Properties().group(ModItemGroups.TOOLS)).setRegistryName(Reference.MOD_ID, "lens"));

        return ITEMS.toArray(new Item[0]);

    }

}


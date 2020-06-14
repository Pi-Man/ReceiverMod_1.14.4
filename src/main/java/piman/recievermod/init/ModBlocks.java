package piman.recievermod.init;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.block.BlockBulletCrafter;
import piman.recievermod.util.Reference;

import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

    public static final Block BULLET_CRAFTER = null;

    public static Block[] getBlockArray() {
        List<Block> list = new ArrayList<>();

        list.add(new BlockBulletCrafter(Block.Properties.create(Material.IRON).hardnessAndResistance(1, 1).harvestLevel(1).harvestTool(ToolType.PICKAXE)).setRegistryName(Reference.MOD_ID, "bullet_crafter"));

        return list.toArray(new Block[0]);
    }

}

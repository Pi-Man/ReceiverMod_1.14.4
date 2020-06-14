package piman.recievermod.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;
import piman.recievermod.inventory.container.AmmoContainer;
import piman.recievermod.inventory.container.ContainerBulletCrafter;
import piman.recievermod.util.Reference;

@ObjectHolder(Reference.MOD_ID)
public class ModContainerTypes {

    public static final ContainerType<AmmoContainer> AMMO_CONTAINER = null;
    public static final ContainerType<ContainerBulletCrafter> BULLET_CRAFTER = null;

}

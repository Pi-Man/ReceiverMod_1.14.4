package piman.recievermod.util;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CapUtils {

    public static <T extends CapabilityProvider> boolean hasCap(T object, Capability capability, Direction direction) {
        return object.getCapability(capability, direction).isPresent();
    }

    public static <T extends CapabilityProvider, K> K getCap(T object, Capability<K> capability, Direction direction) {
        LazyOptional<K> lazyOptional = object.<K>getCapability(capability, direction);
        return lazyOptional.orElse(null);
    }

}

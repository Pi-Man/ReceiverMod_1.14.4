package piman.recievermod.client.renderer.model.animator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import piman.recievermod.util.clientUtils.TransformationBuilder;

import javax.vecmath.Vector3f;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public class Animator {

    private static final Gson SERIALIZER = new GsonBuilder().registerTypeAdapter(Animator.class, new Animator.Deserializer()).registerTypeAdapter(Predicate.class, new Predicate.Deserializer()).registerTypeAdapter(Transformation.class, new Transformation.Deserializer()).create();

    private final List<ResourceLocation> dependencies;

    private final Map<ResourceLocation, Map<ResourceLocation, Predicate>> predicates;

    public Animator() {
        this(new ArrayList<>(), new HashMap<>());
    }

    public Animator(List<ResourceLocation> dependencies, Map<ResourceLocation, Map<ResourceLocation, Predicate>> predicates) {
        this.dependencies = dependencies;
        this.predicates = predicates;
    }

    public static Animator deserialize(Reader reader) {
        return JSONUtils.fromJson(SERIALIZER, reader, Animator.class);
    }

    public Set<ResourceLocation> getDependencies() {
        return ImmutableSet.copyOf(dependencies);
    }

    public List<ResourceLocation> getDependenciesForMap() {
        return ImmutableList.copyOf(dependencies);
    }

    public Map<ResourceLocation, Map<ResourceLocation, Predicate>> getPredicates() {
        return ImmutableMap.copyOf(predicates);
    }

    public List<TRSRTransformation> getSubTransforms(ItemStack stack, World world, LivingEntity entity) {

        List<TRSRTransformation> list = new ArrayList<>();

        for (Map.Entry<ResourceLocation, Map<ResourceLocation, Predicate>> entry : predicates.entrySet()) {

            TransformationBuilder builder = new TransformationBuilder();
            for (Map.Entry<ResourceLocation, Predicate> entry2 : entry.getValue().entrySet()) {

                ResourceLocation predicateName = entry2.getKey();
                IItemPropertyGetter propertyGetter = stack.getItem().getPropertyGetter(predicateName);

                if (propertyGetter != null) {

                    float value = propertyGetter.call(stack, world, entity);
                    Predicate predicate = entry2.getValue();

                    for (Transformation transformation : predicate.getTransformations()) {

                        try {
                            float scaleFactor = transformation.getValue().eval(value);

                            Vector3f translation = transformation.getTranslation();
                            Vector3f scale = transformation.getScale();
                            Vector3f rotation = transformation.getRotation();
                            Vector3f center = transformation.getCenter();

                            if (translation != null) translation.scale(scaleFactor);
                            if (scale != null) scale.scale(scaleFactor);
                            if (rotation != null) rotation.scale(scaleFactor);
                            if (center != null) center.scale(scaleFactor);

                            builder.add(translation, scale, rotation, center, transformation.getOrder());
                        }
                        catch (IndexOutOfBoundsException ignored) {
                        }

                    }

                }

            }

            list.add(builder.build());

        }

        return list;

    }

    public static class Deserializer implements JsonDeserializer<Animator> {

        /**
         * Gson invokes this call-back method during deserialization when it encounters a field of the
         * specified type.
         * <p>In the implementation of this call-back method, you should consider invoking
         * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
         * for any non-trivial field of the returned object. However, you should never invoke it on the
         * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
         * call-back method again).
         *
         * @param json    The Json data being deserialized
         * @param typeOfT The type of the Object to deserialize to
         * @param context
         * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
         * @throws JsonParseException if json is not in the expected format of {@code typeofT}
         */
        @Override
        public Animator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            List<ResourceLocation> dependencies = new ArrayList<>();

            Map<ResourceLocation, Map<ResourceLocation, Predicate>> predicates = new HashMap<>();

            JsonArray submodels = json.getAsJsonObject().getAsJsonArray("submodels");

            for (JsonElement submodelElement : submodels) {

                JsonObject submodelObject = submodelElement.getAsJsonObject();

                ResourceLocation model = new ResourceLocation(submodelObject.get("model").getAsString());

                dependencies.add(model);

                JsonObject predicatesObject = submodelObject.getAsJsonObject("predicates");

                Map<ResourceLocation, Predicate> map = new HashMap<>();

                for (Map.Entry<String, JsonElement> entry : predicatesObject.entrySet()) {

                    ResourceLocation predicateName = new ResourceLocation(entry.getKey());

                    Predicate predicate = context.deserialize(entry.getValue(), Predicate.class);

                    map.put(predicateName, predicate);

                }

                predicates.put(model, map);

            }

            return new Animator(dependencies, predicates);
        }

    }

}

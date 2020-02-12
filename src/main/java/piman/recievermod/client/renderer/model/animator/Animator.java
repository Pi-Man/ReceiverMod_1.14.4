package piman.recievermod.client.renderer.model.animator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import javafx.util.Pair;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
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
    private final List<Pair<ResourceLocation, Map<ResourceLocation, Predicate>>> predicates;
    private final Map<ResourceLocation, Map<ItemCameraTransforms.TransformType, Predicate>> baseTransforms;

    public Animator() {
        this(new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    public Animator(List<ResourceLocation> dependencies, List<Pair<ResourceLocation, Map<ResourceLocation, Predicate>>> predicates, Map<ResourceLocation, Map<ItemCameraTransforms.TransformType, Predicate>> baseTransforms) {
        this.dependencies = dependencies;
        this.predicates = predicates;
        this.baseTransforms = baseTransforms;
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

    public List<TRSRTransformation> getSubTransforms(ItemStack stack, World world, LivingEntity entity) {

        List<TRSRTransformation> list = new ArrayList<>();

        for (Pair<ResourceLocation, Map<ResourceLocation, Predicate>> entry : predicates) {

            TransformationBuilder builder = new TransformationBuilder();
            for (Map.Entry<ResourceLocation, Predicate> entry2 : entry.getValue().entrySet()) {

                ResourceLocation predicateName = entry2.getKey();
                IItemPropertyGetter propertyGetter = stack.getItem().getPropertyGetter(predicateName);

                if (propertyGetter != null) {

                    float value = propertyGetter.call(stack, world, entity);
                    Predicate predicate = entry2.getValue();

                    for (Transformation transformation : predicate.getTransformations()) {

                       addTransformation(builder, transformation, value);

                    }

                }

            }

            list.add(builder.build());

        }

        return list;

    }

    public Map<ItemCameraTransforms.TransformType, TRSRTransformation> getBaseTransforms(ItemStack stack, World world, LivingEntity entity) {

        Map<ItemCameraTransforms.TransformType, TRSRTransformation> baseTransforms = new EnumMap<>(ItemCameraTransforms.TransformType.class);

        Map<ItemCameraTransforms.TransformType, TransformationBuilder> builders = new EnumMap<>(ItemCameraTransforms.TransformType.class);

        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            builders.put(type, new TransformationBuilder());
        }

        for (Map.Entry<ResourceLocation, Map<ItemCameraTransforms.TransformType, Predicate>> entry : this.baseTransforms.entrySet()) {

            for (Map.Entry<ItemCameraTransforms.TransformType, Predicate> entry1 : entry.getValue().entrySet()) {

                IItemPropertyGetter getter = stack.getItem().getPropertyGetter(entry.getKey());

                if (getter != null) {

                    float value = getter.call(stack, world, entity);

                    for (Transformation transformation : entry1.getValue().getTransformations()) {

                        addTransformation(builders.get(entry1.getKey()), transformation, value);

                    }

                }

            }

        }

        builders.forEach((type, builder) -> baseTransforms.put(type, builder.build()));

        return baseTransforms;

    }

    private void addTransformation(TransformationBuilder builder, Transformation transformation, float value) {
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

            builder.add(translation, rotation, center, scale, transformation.getOrder());
        }
        catch (IndexOutOfBoundsException ignored) {
        }
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

            List<Pair<ResourceLocation, Map<ResourceLocation, Predicate>>> predicates = new ArrayList<>();

            JsonArray submodels = json.getAsJsonObject().getAsJsonArray("submodels");


            if (submodels != null) for (JsonElement submodelElement : submodels) {

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

                predicates.add(new Pair<>(model, map));

            }

            JsonObject transformsObject = json.getAsJsonObject().getAsJsonObject("basetransformation");

            Map<ResourceLocation, Map<ItemCameraTransforms.TransformType, Predicate>> baseTransforms = new HashMap<>();

            if (transformsObject != null) {

                if (transformsObject.has("predicates")) {
                    transformsObject = transformsObject.getAsJsonObject("predicates");
                }

                for (Map.Entry<String, JsonElement> entry : transformsObject.entrySet()) {

                    ResourceLocation location = new ResourceLocation(entry.getKey());
                    JsonObject transformTypeObject = entry.getValue().getAsJsonObject();

                    Map<ItemCameraTransforms.TransformType, Predicate> transformationListMap = new EnumMap<ItemCameraTransforms.TransformType, Predicate>(ItemCameraTransforms.TransformType.class);

                    for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
                        if (transformTypeObject.has(type.name())) {
                            transformationListMap.put(type, context.deserialize(transformTypeObject.getAsJsonArray(type.name()), Predicate.class));
                        }
                    }

                    baseTransforms.put(location, transformationListMap);

                }

            }

            return new Animator(dependencies, predicates, baseTransforms);
        }

    }

}

package piman.recievermod.client.renderer.model.bbgunmodel;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class ModelBlock
{
    private static final Logger LOGGER = LogManager.getLogger();
    @VisibleForTesting
    static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new Deserializer()).registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer()).registerTypeAdapter(ElementFace.class, new ElementFace.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverrideDeserializer()).create();
    private final Map<UUID, ModelElement> elements;
    private final boolean gui3d;
    public final boolean ambientOcclusion;
    private final ItemCameraTransforms cameraTransforms;
    private final List<ItemOverride> overrides;
    public String name = "";
    @VisibleForTesting
    public final Map<String, String> textures;

    public static ModelBlock deserialize(Reader readerIn)
    {
        return (ModelBlock) JSONUtils.fromJson(SERIALIZER, readerIn, ModelBlock.class, false);
    }

    public static ModelBlock deserialize(String jsonString)
    {
        return deserialize(new StringReader(jsonString));
    }

    public ModelBlock(Map<UUID, ModelElement> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn, List<ItemOverride> overridesIn)
    {
        this.elements = elementsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.textures = texturesIn;
        this.cameraTransforms = cameraTransformsIn;
        this.overrides = overridesIn;
    }

    public Map<UUID, ModelElement> getElements()
    {
        return this.elements;
    }

    public boolean isAmbientOcclusion()
    {
        return this.ambientOcclusion;
    }

    public boolean isGui3d()
    {
        return this.gui3d;
    }

    public Collection<ResourceLocation> getOverrideLocations()
    {
        Set<ResourceLocation> set = Sets.<ResourceLocation>newHashSet();

        for (ItemOverride itemoverride : this.overrides)
        {
            set.add(itemoverride.getLocation());
        }

        return set;
    }

    public List<ItemOverride> getOverrides()
    {
        return this.overrides;
    }

//    public ItemOverrideList createOverrides()
//    {
//        return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(this.overrides);
//    }

    public boolean isTexturePresent(String textureName)
    {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }

    public String resolveTextureName(String textureName)
    {
        if (!this.startsWithHash(textureName))
        {
            textureName = '#' + textureName;
        }

        return this.resolveTextureName(textureName, new Bookkeep(this));
    }

    private String resolveTextureName(String textureName, Bookkeep p_178302_2_)
    {
        if (this.startsWithHash(textureName))
        {
            if (this == p_178302_2_.modelExt)
            {
                LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
                return "missingno";
            }
            else
            {
                String s = this.textures.get(textureName.substring(1));

                p_178302_2_.modelExt = this;

                if (s != null && this.startsWithHash(s))
                {
                    s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
                }

                return s != null && !this.startsWithHash(s) ? s : "missingno";
            }
        }
        else
        {
            return textureName;
        }
    }

    private boolean startsWithHash(String hash)
    {
        return hash.charAt(0) == '#';
    }

    public ItemCameraTransforms getAllTransforms()
    {
        ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
        ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
        ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
        ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
        return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
    }

    private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        return this.cameraTransforms.getTransform(type);
    }

    @OnlyIn(Dist.CLIENT)
    static final class Bookkeep
        {
            public final ModelBlock model;
            public ModelBlock modelExt;

            private Bookkeep(ModelBlock modelIn)
            {
                this.model = modelIn;
            }
        }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelBlock>
        {
            public ModelBlock deserialize(JsonElement baseElement, Type type, JsonDeserializationContext context) throws JsonParseException
            {
            	
            	JsonObject baseObject = (JsonObject) baseElement;
            	
            	this.checkVersion(baseElement);
            	
            	Map<UUID, ModelElement> elements = this.getElements(baseObject, context);
            	
            	Map<String, String> textures = this.getTextures(baseObject);
            	
                ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

                if (baseObject.has("display"))
                {
                    JsonObject jsonobject1 = JSONUtils.getJsonObject(baseObject, "display");
                    itemcameratransforms = (ItemCameraTransforms)context.deserialize(jsonobject1, ItemCameraTransforms.class);
                }
            	
            	boolean ambientOclusion = this.getAmbientOcclusionEnabled(baseObject);
            	
            	List<ItemOverride> overrides = this.getItemOverrides(context, baseObject);
            	
            	return new ModelBlock(elements, textures, ambientOclusion, true, itemcameratransforms, overrides);
            	
            }
            
            private void checkVersion(JsonElement element) {
            	
				JsonObject object = element.getAsJsonObject();
				
				JsonObject meta = object.getAsJsonObject("meta");
				
				if (!JSONUtils.getString(meta, "format_version", "").startsWith("3")) {
					throw new JsonParseException("Version must be at least 3.0");
				}
				
				if (!JSONUtils.getString(meta, "model_format", "").equals("free")) {
					throw new JsonParseException("Model must be free Model");
				}
				
				if (JSONUtils.getBoolean(meta, "box_uv", true)) {
					throw new JsonParseException("Box UV not supported");
				}
			}
            

            protected List<ItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object)
            {
                List<ItemOverride> list = Lists.<ItemOverride>newArrayList();

                if (object.has("overrides"))
                {
                    for (JsonElement jsonelement : JSONUtils.getJsonArray(object, "overrides"))
                    {
                        list.add((ItemOverride)deserializationContext.deserialize(jsonelement, ItemOverride.class));
                    }
                }

                return list;
            }

            private Map<String, String> getTextures(JsonObject object)
            {
                Map<String, String> map = Maps.<String, String>newHashMap();

                if (object.has("textures"))
                {
                    JsonArray texturesArray = object.getAsJsonArray("textures");

                    for (JsonElement textureElement : texturesArray)
                    {
                    	JsonObject textureObject = (JsonObject) textureElement;
                        map.put(textureObject.get("id").getAsString(), new ResourceLocation(textureObject.get("namespace").getAsString(), textureObject.get("folder").getAsString() + "/" + textureObject.get("name").getAsString().replace(".png", "")).toString());
                    }
                }

                return map;
            }

            protected boolean getAmbientOcclusionEnabled(JsonObject object)
            {
                return JSONUtils.getBoolean(object, "ambientocclusion", true);
            }
            
            private Map<UUID, ModelElement> getElements(JsonObject jsonObject, JsonDeserializationContext context) {
            	
            	JsonArray elementsArray = jsonObject.getAsJsonArray("elements");
            	
            	Map<UUID, ModelElement> map = new HashMap<>();
            	
            	for (JsonElement jsonElement : elementsArray) {
            		ModelElement modelElement = context.deserialize(jsonElement, ModelElement.class);
            		map.put(modelElement.uuid, modelElement);
            	}
            	
            	return map;
            }
        }

    @OnlyIn(Dist.CLIENT)
    public static class LoopException extends RuntimeException
        {
        }
    
    @OnlyIn(Dist.CLIENT)
    static class ItemOverrideDeserializer implements JsonDeserializer<ItemOverride>
        {
            public ItemOverride deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "model"));
                Map<ResourceLocation, Float> map = this.makeMapResourceValues(jsonobject);
                return new ItemOverride(resourcelocation, map);
            }

            protected Map<ResourceLocation, Float> makeMapResourceValues(JsonObject p_188025_1_)
            {
                Map<ResourceLocation, Float> map = Maps.<ResourceLocation, Float>newLinkedHashMap();
                JsonObject jsonobject = JSONUtils.getJsonObject(p_188025_1_, "predicate");

                for (Entry<String, JsonElement> entry : jsonobject.entrySet())
                {
                    map.put(new ResourceLocation(entry.getKey()), JSONUtils.getFloat(entry.getValue(), entry.getKey()));
                }

                return map;
            }
        }
}
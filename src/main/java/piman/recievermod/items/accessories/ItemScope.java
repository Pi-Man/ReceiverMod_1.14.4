package piman.recievermod.items.accessories;

public class ItemScope extends ItemAccessory {

    private float zoom;

    public ItemScope(Properties properties, float zoom) {
        super(properties, AccessoryType.SCOPE);
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }
}

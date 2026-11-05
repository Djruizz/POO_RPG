package rpg.model.enums;

/**
 * Clasificación de la categoría de un ítem dentro del inventario.
 */
public enum ItemType {
    WEAPON("Arma"),
    ARMOR("Armadura"),
    ACCESSORY("Accesorio"),
    CONSUMABLE("Consumible");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

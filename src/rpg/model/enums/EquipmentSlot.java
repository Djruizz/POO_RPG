package rpg.model.enums;

/**
 * Ranuras de equipamiento disponibles en el cuerpo del jugador.
 */
public enum EquipmentSlot {
    WEAPON("Arma"),
    HELMET("Yelmo"),
    ARMOR("Coraza"),
    ACCESSORY("Reliquia");

    private final String displayName;

    EquipmentSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

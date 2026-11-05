package rpg.model.item;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import rpg.model.enums.ItemType;
import rpg.model.enums.Rarity;

/**
 * Clase base abstracta de todos los ítems del juego.
 * Implementa Comparable para ordenamiento natural por Rareza descendente, tipo y nombre.
 */
public abstract class Item implements Comparable<Item>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Comparator<Item> NATURAL_ORDER = Comparator
            .comparingInt((Item i) -> i.getRarity().getTierWeight()).reversed()
            .thenComparing(Item::getItemType)
            .thenComparing(Item::getName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Item::getId);

    private final String id;
    private final String name;
    private final String description;
    private final Rarity rarity;
    private final ItemType itemType;
    private final int goldValue;

    protected Item(String id, String name, String description, Rarity rarity, ItemType itemType, int goldValue) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador del ítem no puede estar vacío.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del ítem no puede estar vacío.");
        }
        if (goldValue < 0) {
            throw new IllegalArgumentException("El valor en oro del ítem no puede ser negativo.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.rarity = Objects.requireNonNull(rarity, "La rareza del ítem es obligatoria.");
        this.itemType = Objects.requireNonNull(itemType, "El tipo de ítem es obligatorio.");
        this.goldValue = goldValue;
    }

    @Override
    public int compareTo(Item other) {
        Objects.requireNonNull(other, "No se puede comparar un Item contra null.");
        return NATURAL_ORDER.compare(this, other);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public abstract String getSummaryStats();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return rarity.format("[" + rarity.getDisplayName() + "] " + name) + " (" + getSummaryStats() + ")";
    }
}

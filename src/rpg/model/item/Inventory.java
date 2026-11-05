package rpg.model.item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import rpg.exception.InventoryFullException;
import rpg.exception.ItemNotFoundException;

/**
 * Contenedor encapsulado que gestiona la colección de ítems del jugador con restricción de capacidad.
 */
public final class Inventory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int maxCapacity;
    @SuppressWarnings("serial")
    private final List<Item> items;

    public Inventory(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("La capacidad máxima del inventario debe ser positiva.");
        }
        this.maxCapacity = maxCapacity;
        this.items = new ArrayList<>(maxCapacity);
    }

    public void addItem(Item item) throws InventoryFullException {
        Objects.requireNonNull(item, "No se puede añadir un ítem nulo al inventario.");
        if (isFull()) {
            throw new InventoryFullException(maxCapacity);
        }
        this.items.add(item);
    }

    public Item findById(String itemId) throws ItemNotFoundException {
        if (itemId == null || itemId.isBlank()) {
            throw new ItemNotFoundException(String.valueOf(itemId));
        }
        return items.stream()
                .filter(i -> i.getId().equalsIgnoreCase(itemId.trim()))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    public Item getByIndex(int oneBasedIndex) throws ItemNotFoundException {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= items.size()) {
            throw new ItemNotFoundException("Índice #" + oneBasedIndex);
        }
        return items.get(index);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public Item removeByIndex(int oneBasedIndex) throws ItemNotFoundException {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= items.size()) {
            throw new ItemNotFoundException("Índice #" + oneBasedIndex);
        }
        return items.remove(index);
    }

    /**
     * Ordena el inventario utilizando el orden natural definido por Item.compareTo.
     */
    public void sortNatural() {
        Collections.sort(this.items);
    }

    /**
     * Ordena el inventario utilizando un comparador alternativo.
     */
    public void sortWith(Comparator<Item> comparator) {
        this.items.sort(Objects.requireNonNull(comparator, "El comparador no puede ser nulo."));
    }

    public boolean isFull() {
        return items.size() >= maxCapacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}

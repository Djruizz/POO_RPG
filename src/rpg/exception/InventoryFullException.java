package rpg.exception;

import java.io.Serial;

/**
 * Se lanza al intentar añadir un ítem a un inventario que ha alcanzado su capacidad máxima.
 */
public class InventoryFullException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int maxCapacity;

    public InventoryFullException(int maxCapacity) {
        super("El inventario está lleno. Capacidad máxima: " + maxCapacity + " ranuras.");
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}

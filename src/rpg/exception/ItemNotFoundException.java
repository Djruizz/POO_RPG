package rpg.exception;

import java.io.Serial;

/**
 * Se lanza cuando no se encuentra un ítem especificado por ID o índice en un inventario o equipamiento.
 */
public class ItemNotFoundException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String itemIdentifier;

    public ItemNotFoundException(String itemIdentifier) {
        super("No se encontró ningún ítem con el identificador o índice: " + itemIdentifier);
        this.itemIdentifier = itemIdentifier;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }
}

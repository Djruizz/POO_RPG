package rpg.model.item;

import rpg.exception.GameException;
import rpg.model.entity.Player;

/**
 * Contrato para cualquier objeto del inventario que puede ser consumido o activado.
 */
public interface Usable {
    /**
     * Aplica el efecto del ítem sobre el jugador objetivo.
     * @param target Jugador que utiliza el ítem.
     * @return Mensaje descriptivo del efecto aplicado.
     * @throws GameException si las condiciones para usar el ítem no son válidas.
     */
    String use(Player target) throws GameException;

    /**
     * Indica si el ítem debe ser eliminado del inventario tras su uso.
     */
    boolean isConsumedOnUse();
}

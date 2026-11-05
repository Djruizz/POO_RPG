package rpg.persistence;

import java.util.List;
import rpg.exception.PersistenceException;
import rpg.model.dungeon.GameState;

/**
 * Contrato de repositorio de persistencia para guardar y recuperar partidas (Principio DIP).
 */
public interface GameRepository {
    void save(GameState state, String slotName) throws PersistenceException;
    GameState load(String slotName) throws PersistenceException;
    List<String> listSaveSlots();
}

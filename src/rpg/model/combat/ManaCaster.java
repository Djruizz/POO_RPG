package rpg.model.combat;

import rpg.exception.InsufficientManaException;

/**
 * Contrato segregado para combatientes que utilizan Maná para sus habilidades mágicas.
 */
public interface ManaCaster {
    void consumeMana(int amount) throws InsufficientManaException;
    int restoreMana(int amount);
    int getCurrentMana();
    int getMaxMana();
}

package rpg.model.combat;

import rpg.exception.InvalidActionException;

/**
 * Contrato segregado para combatientes que generan y gastan Furia en combate (ej. Guerrero).
 */
public interface FuryFighter {
    void consumeFury(int amount) throws InvalidActionException;
    void gainFury(int amount);
    int getCurrentFury();
    int getMaxFury();
}

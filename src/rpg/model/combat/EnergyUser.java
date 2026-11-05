package rpg.model.combat;

import rpg.exception.InvalidActionException;

/**
 * Contrato segregado para combatientes que utilizan Energía táctica (ej. Pícaro).
 */
public interface EnergyUser {
    void consumeEnergy(int amount) throws InvalidActionException;
    void regenerateEnergy(int amount);
    int getCurrentEnergy();
    int getMaxEnergy();
}

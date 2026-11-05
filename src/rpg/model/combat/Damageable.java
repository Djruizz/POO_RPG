package rpg.model.combat;

/**
 * Contrato fundamental para cualquier entidad susceptible a recibir daño o curación.
 */
public interface Damageable {
    /**
     * Aplica daño mitigado por defensas.
     * @param rawDamage Daño bruto antes de mitigación.
     * @return Daño real efectivo sufrido.
     */
    int takeDamage(int rawDamage);

    /**
     * Aplica curación sin sobrepasar la salud máxima.
     * @param amount Cantidad bruta de curación.
     * @return Salud efectiva recuperada.
     */
    int heal(int amount);

    /**
     * Determina si la entidad sigue con vida (currentHealth > 0).
     */
    boolean isAlive();

    /**
     * Salud actual de la entidad.
     */
    int getCurrentHealth();

    /**
     * Salud máxima permitida para la entidad.
     */
    int getMaxHealth();
}

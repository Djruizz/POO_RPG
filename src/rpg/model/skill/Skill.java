package rpg.model.skill;

import java.io.Serializable;
import rpg.exception.GameException;
import rpg.model.combat.Combatant;
import rpg.model.combat.SkillResult;

/**
 * Contrato polimórfico para cualquier acción o técnica ejecutable en combate.
 */
public interface Skill extends Serializable {
    /**
     * Nombre legible de la habilidad.
     */
    String getName();

    /**
     * Descripción detallada del efecto y escalado.
     */
    String getDescription();

    /**
     * Descripción del coste (ej. "25 Maná", "35 Furia", "Gratis").
     */
    String getCostDescription();

    /**
     * Indica si la habilidad se lanza sobre uno mismo (true) o sobre el oponente (false).
     */
    boolean isSelfTargeted();

    /**
     * Ejecuta la habilidad aplicando las fórmulas de daño, curación y efectos secundarios.
     * @param user Combatiente que ejecuta la habilidad.
     * @param target Combatiente receptor de la habilidad.
     * @return Registro inmutable con el resultado detallado de la acción.
     * @throws GameException si el usuario no cuenta con los recursos necesarios o el objetivo no es válido.
     */
    SkillResult execute(Combatant user, Combatant target) throws GameException;
}

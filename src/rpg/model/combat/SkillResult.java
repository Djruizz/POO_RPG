package rpg.model.combat;

import java.io.Serial;
import java.io.Serializable;
import rpg.model.enums.StatusEffectType;

/**
 * Registro inmutable que describe el desenlace de la ejecución de una habilidad.
 */
public record SkillResult(
        String actorName,
        String skillName,
        String targetName,
        int primaryValue,
        boolean selfHeal,
        boolean criticalHit,
        StatusEffectType appliedStatus,
        String detailMessage
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

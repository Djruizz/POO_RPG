package rpg.model.combat;

import rpg.model.enums.StatusEffectType;

/**
 * Objeto inmutable que describe el desenlace de la ejecución de una habilidad.
 */
public final class SkillResult {
    private final String actorName;
    private final String skillName;
    private final String targetName;
    private final int primaryValue;
    private final boolean selfHeal;
    private final boolean criticalHit;
    private final StatusEffectType appliedStatus;
    private final String detailMessage;

    public SkillResult(String actorName, String skillName, String targetName, int primaryValue,
                       boolean selfHeal, boolean criticalHit, StatusEffectType appliedStatus,
                       String detailMessage) {
        this.actorName = actorName;
        this.skillName = skillName;
        this.targetName = targetName;
        this.primaryValue = primaryValue;
        this.selfHeal = selfHeal;
        this.criticalHit = criticalHit;
        this.appliedStatus = appliedStatus;
        this.detailMessage = detailMessage;
    }

    public String getActorName() {
        return actorName;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getTargetName() {
        return targetName;
    }

    /** Daño infligido o, si {@link #isSelfHeal()} es verdadero, salud restaurada. */
    public int getPrimaryValue() {
        return primaryValue;
    }

    public boolean isSelfHeal() {
        return selfHeal;
    }

    public boolean isCriticalHit() {
        return criticalHit;
    }

    /** Estado aplicado al objetivo, o {@code null} si la habilidad no aplicó ninguno. */
    public StatusEffectType getAppliedStatus() {
        return appliedStatus;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}

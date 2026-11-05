package rpg.model.skill;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.combat.Combatant;
import rpg.model.combat.SkillResult;

/**
 * Ataque básico físico estándar. No consume recursos y genera impacto directo.
 */
public final class BasicAttack extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    public BasicAttack() {
        super("Ataque Básico", "Golpe físico directo equivalente al 100% del poder ofensivo.", false);
    }

    @Override
    public String getCostDescription() {
        return "Gratis";
    }

    @Override
    public SkillResult execute(Combatant user, Combatant target) {
        int rawDamage = user.getEffectiveAttack();
        boolean crit = ThreadLocalRandom.current().nextDouble() < user.getCriticalChance();
        if (crit) {
            rawDamage = (int) Math.round(rawDamage * user.getCriticalMultiplier());
        }
        int dealt = target.takeDamage(rawDamage);
        user.onAttackLanded(dealt);

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                dealt,
                false,
                crit,
                null,
                crit ? "¡Impacto crítico demoledor!" : "Un golpe certero."
        );
    }
}

package rpg.model.skill;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.exception.GameException;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.combat.ManaCaster;
import rpg.model.combat.SkillResult;
import rpg.model.enums.StatusEffectType;

/**
 * Hechizo elemental de fuego que inflige daño mágico potenciado y puede quemar al oponente.
 * Consume maná si el ejecutor implementa ManaCaster.
 */
public final class Fireball extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int manaCost;

    public Fireball(int manaCost) {
        super("Bola de Fuego", "Proyectil ígneo (145% ATQ) con 45% de probabilidad de quemar por 2 turnos.", false);
        this.manaCost = Math.max(0, manaCost);
    }

    @Override
    public String getCostDescription() {
        return manaCost > 0 ? manaCost + " Maná" : "Gratis";
    }

    @Override
    public SkillResult execute(Combatant user, Combatant target) throws GameException {
        if (user instanceof ManaCaster caster && manaCost > 0) {
            caster.consumeMana(manaCost);
        }

        int rawDamage = (int) Math.round(user.getEffectiveAttack() * 1.45);
        boolean crit = ThreadLocalRandom.current().nextDouble() < user.getCriticalChance();
        if (crit) {
            rawDamage = (int) Math.round(rawDamage * user.getCriticalMultiplier());
        }

        int dealt = target.takeDamage(rawDamage);
        user.onAttackLanded(dealt);

        StatusEffectType applied = null;
        String extraDetail = "";
        if (target.isAlive() && ThreadLocalRandom.current().nextDouble() < 0.45) {
            int burnTick = Math.max(3, user.getLevel() * 3 + 2);
            target.applyStatusEffect(new ActiveStatusEffect(StatusEffectType.BURN, 2, burnTick));
            applied = StatusEffectType.BURN;
            extraDetail = "¡Llamas abrasadoras envuelven al objetivo, quemándolo!";
        }

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                dealt,
                false,
                crit,
                applied,
                extraDetail.isEmpty() ? (crit ? "¡Explosión ígnea crítica!" : "Impacto de fuego abrasador.") : extraDetail
        );
    }
}

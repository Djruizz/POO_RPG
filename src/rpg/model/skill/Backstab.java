package rpg.model.skill;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.exception.GameException;
import rpg.model.combat.Combatant;
import rpg.model.combat.EnergyUser;
import rpg.model.combat.SkillResult;

/**
 * Ataque furtivo letal con alta probabilidad crítica.
 * Consume energía si el usuario implementa EnergyUser.
 */
public final class Backstab extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int energyCost;

    public Backstab(int energyCost) {
        super("Puñalada Trapera", "Ataque furtivo (180% ATQ) con +35% de probabilidad de golpe crítico.", false);
        this.energyCost = Math.max(0, energyCost);
    }

    @Override
    public String getCostDescription() {
        return energyCost > 0 ? energyCost + " Energía" : "Gratis";
    }

    @Override
    public SkillResult execute(Combatant user, Combatant target) throws GameException {
        if (user instanceof EnergyUser energyUser && energyCost > 0) {
            energyUser.consumeEnergy(energyCost);
        }

        int rawDamage = (int) Math.round(user.getEffectiveAttack() * 1.80);
        double boostedCritChance = Math.min(1.0, user.getCriticalChance() + 0.35);
        boolean crit = ThreadLocalRandom.current().nextDouble() < boostedCritChance;

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
                crit ? "¡Puñalada letal a un punto vital!" : "Golpe furtivo certero."
        );
    }
}

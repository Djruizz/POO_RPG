package rpg.model.skill;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.exception.GameException;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.combat.EnergyUser;
import rpg.model.combat.SkillResult;
import rpg.model.enums.StatusEffectType;

/**
 * Ataque con hoja impregnada en toxinas virulentas.
 * Inflige daño físico y puede infectar al oponente con Veneno por 3 turnos.
 */
public final class PoisonStrike extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int energyCost;

    public PoisonStrike(int energyCost) {
        super("Daga Tóxica", "Corte penetrante (115% ATQ) con 75% de probabilidad de envenenar por 3 turnos.", false);
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

        int rawDamage = (int) Math.round(user.getEffectiveAttack() * 1.15);
        boolean crit = ThreadLocalRandom.current().nextDouble() < user.getCriticalChance();
        if (crit) {
            rawDamage = (int) Math.round(rawDamage * user.getCriticalMultiplier());
        }

        int dealt = target.takeDamage(rawDamage);
        user.onAttackLanded(dealt);

        StatusEffectType applied = null;
        String extraDetail = "";
        if (target.isAlive() && ThreadLocalRandom.current().nextDouble() < 0.75) {
            int poisonTick = Math.max(3, user.getLevel() * 2 + 3);
            target.applyStatusEffect(new ActiveStatusEffect(StatusEffectType.POISON, 3, poisonTick));
            applied = StatusEffectType.POISON;
            extraDetail = "¡Toxinas virulentas entran en el torrente sanguíneo, envenenando a la víctima!";
        }

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                dealt,
                false,
                crit,
                applied,
                extraDetail.isEmpty() ? "Corte rápido con punta aceitosa." : extraDetail
        );
    }
}

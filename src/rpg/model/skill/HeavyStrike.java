package rpg.model.skill;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.exception.GameException;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.combat.FuryFighter;
import rpg.model.combat.SkillResult;
import rpg.model.enums.StatusEffectType;

/**
 * Golpe devastador con escalado del 170% del ataque y probabilidad de aturdimiento.
 * Consume furia si el ejecutor implementa FuryFighter.
 */
public final class HeavyStrike extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int furyCost;

    public HeavyStrike(int furyCost) {
        super("Golpe Brutal", "Impacto devastador (170% ATQ) con 25% de probabilidad de aturdir al objetivo.", false);
        this.furyCost = Math.max(0, furyCost);
    }

    @Override
    public String getCostDescription() {
        return furyCost > 0 ? furyCost + " Furia" : "Gratis";
    }

    @Override
    public SkillResult execute(Combatant user, Combatant target) throws GameException {
        if (user instanceof FuryFighter fighter && furyCost > 0) {
            fighter.consumeFury(furyCost);
        }

        int rawDamage = (int) Math.round(user.getEffectiveAttack() * 1.70);
        boolean crit = ThreadLocalRandom.current().nextDouble() < user.getCriticalChance();
        if (crit) {
            rawDamage = (int) Math.round(rawDamage * user.getCriticalMultiplier());
        }

        int dealt = target.takeDamage(rawDamage);
        user.onAttackLanded(dealt);

        StatusEffectType applied = null;
        String extraDetail = "";
        if (target.isAlive() && ThreadLocalRandom.current().nextDouble() < 0.25) {
            target.applyStatusEffect(new ActiveStatusEffect(StatusEffectType.STUN, 1, 0));
            applied = StatusEffectType.STUN;
            extraDetail = "¡El impacto deja aturdido al objetivo por 1 turno!";
        }

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                dealt,
                false,
                crit,
                applied,
                extraDetail.isEmpty() ? (crit ? "¡Golpe brutal crítico!" : "Golpe colosal.") : extraDetail
        );
    }
}

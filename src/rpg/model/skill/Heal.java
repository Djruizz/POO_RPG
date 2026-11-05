package rpg.model.skill;

import java.io.Serial;
import rpg.exception.GameException;
import rpg.exception.InvalidActionException;
import rpg.model.combat.Combatant;
import rpg.model.combat.ManaCaster;
import rpg.model.combat.SkillResult;

/**
 * Habilidad restauradora que recupera salud basada en el poder del combatiente.
 * Consume maná si el usuario implementa ManaCaster.
 */
public final class Heal extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int manaCost;

    public Heal(int manaCost) {
        super("Luz Restauradora", "Restaura salud equivalente al 135% del poder mágico.", true);
        this.manaCost = Math.max(0, manaCost);
    }

    @Override
    public String getCostDescription() {
        return manaCost > 0 ? manaCost + " Maná" : "Gratis";
    }

    @Override
    public SkillResult execute(Combatant user, Combatant target) throws GameException {
        if (target.getCurrentHealth() >= target.getMaxHealth()) {
            throw new InvalidActionException("La salud de " + target.getName() + " ya está al máximo.");
        }

        if (user instanceof ManaCaster caster && manaCost > 0) {
            caster.consumeMana(manaCost);
        }

        int healAmount = Math.max(15, (int) Math.round(user.getEffectiveAttack() * 1.35));
        int effectiveHeal = target.heal(healAmount);

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                effectiveHeal,
                true,
                false,
                null,
                "Una luz revitalizante restaura " + effectiveHeal + " puntos de salud."
        );
    }
}

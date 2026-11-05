package rpg.model.skill;

import java.io.Serial;
import rpg.exception.GameException;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.combat.FuryFighter;
import rpg.model.combat.SkillResult;
import rpg.model.enums.StatusEffectType;

/**
 * Habilidad defensiva que adopta una postura impenetrable confiriendo el estado Fortificación.
 * Incrementa sustancialmente la defensa física efectiva durante 2 rondas.
 */
public final class IronGuard extends AbstractSkill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int furyCost;

    public IronGuard(int furyCost) {
        super("Guardia de Hierro", "Postura inquebrantable que confiere FORTIFICACIÓN (+DEF) por 2 turnos.", true);
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

        int fortifyAmount = 8 + (user.getLevel() * 4);
        target.applyStatusEffect(new ActiveStatusEffect(StatusEffectType.FORTIFY, 2, fortifyAmount));

        return new SkillResult(
                user.getName(),
                getName(),
                target.getName(),
                fortifyAmount,
                true,
                false,
                StatusEffectType.FORTIFY,
                "¡Adopta una postura defensiva que incrementa la armadura en +" + fortifyAmount + " DEF por 2 turnos!"
        );
    }
}

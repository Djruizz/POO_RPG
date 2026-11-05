package rpg.model.entity;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.combat.Combatant;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.HeavyStrike;
import rpg.model.skill.Skill;

/**
 * No-muerto acorazado con alta defensa física y golpes contundentes.
 */
public final class Skeleton extends Enemy {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Skill regularAttack;
    private final Skill boneCrusher;

    public Skeleton(int floor) {
        super("Guardián Esquelético", 72, 16, 12, floor, 0.28, 55, 35, false);
        this.regularAttack = new BasicAttack();
        this.boneCrusher = new HeavyStrike(0);
        learnSkill(regularAttack);
        learnSkill(boneCrusher);
    }

    @Override
    public Skill chooseSkill(Combatant target) {
        // 45% de ejecutar golpe demoledor
        return ThreadLocalRandom.current().nextDouble() < 0.45 ? boneCrusher : regularAttack;
    }

    @Override
    public String getClassTitle() {
        return "No-Muerto Acorazado";
    }
}

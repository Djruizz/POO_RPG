package rpg.model.entity;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.combat.Combatant;
import rpg.model.skill.Backstab;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.Skill;

/**
 * Criatura hostil ágil y oportunista.
 * Aprovecha aperturas para infligir ataques sorpresa.
 */
public final class Goblin extends Enemy {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Skill quickAttack;
    private final Skill ambush;
    private final Skill poisonBlade;

    public Goblin(int floor) {
        super("Goblin Saqueador", 52, 15, 6, floor, 0.25, 40, 25, false);
        this.quickAttack = new BasicAttack();
        this.ambush = new Backstab(0);
        this.poisonBlade = new rpg.model.skill.PoisonStrike(0);
        learnSkill(quickAttack);
        learnSkill(ambush);
        learnSkill(poisonBlade);
    }

    @Override
    public double getCriticalChance() {
        return 0.18;
    }

    @Override
    public Skill chooseSkill(Combatant target) {
        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < 0.35) return poisonBlade;
        if (roll < 0.65) return ambush;
        return quickAttack;
    }

    @Override
    public String getClassTitle() {
        return "Goblin (Rápido)";
    }
}

package rpg.model.combat;

import java.util.List;
import java.util.Set;
import rpg.model.skill.Skill;

/**
 * Contrato que amplía Damageable para todos los participantes activos de un combate por turnos.
 */
public interface Combatant extends Damageable {
    String getName();
    int getLevel();
    int getEffectiveAttack();
    int getEffectiveDefense();
    double getCriticalChance();
    double getCriticalMultiplier();

    List<Skill> getSkills();
    Set<ActiveStatusEffect> getActiveEffects();

    void applyStatusEffect(ActiveStatusEffect effect);
    List<String> processTurnStartEffects();
    boolean isStunned();
    void onAttackLanded(int finalDamage);
}

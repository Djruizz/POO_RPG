package rpg.model.entity;

import java.io.Serial;
import java.util.List;
import rpg.exception.InsufficientManaException;
import rpg.model.combat.ManaCaster;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.Fireball;
import rpg.model.skill.Heal;

/**
 * Especialización de héroe: Mago Arcano.
 * Conjurador de devastadores hechizos ígneos y magia reconstituyente a base de Maná.
 */
public final class Mage extends Player implements ManaCaster {
    @Serial
    private static final long serialVersionUID = 1L;

    private int maxMana;
    private int mana;

    public Mage(String name) {
        super(name, 95, 22, 6, 12);
        this.maxMana = 80;
        this.mana = 80;
        learnSkill(new BasicAttack());
        learnSkill(new Fireball(25));
        learnSkill(new Heal(20));
    }

    @Override
    public void consumeMana(int amount) throws InsufficientManaException {
        if (this.mana < amount) {
            throw new InsufficientManaException(amount, this.mana);
        }
        this.mana -= amount;
    }

    @Override
    public int restoreMana(int amount) {
        int before = this.mana;
        this.mana = Math.min(this.maxMana, this.mana + Math.max(0, amount));
        return this.mana - before;
    }

    @Override
    protected void onTurnStartHook(List<String> turnLogs) {
        int recovered = restoreMana(7);
        if (recovered > 0) {
            turnLogs.add(getName() + " canaliza el flujo del éter y recupera +" + recovered + " de Maná.");
        }
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    protected void applyLevelUpBonuses() {
        int baseHpWithoutGear = getMaxHealth() - getEquippedHealthBonus();
        setMaxHealth(baseHpWithoutGear + 14);
        setAttackPower(getBaseAttackPower() + 6);
        setDefense(getBaseDefense() + 2);
        this.maxMana += 15;
        this.mana = this.maxMana;
    }

    @Override
    public String getClassTitle() {
        return "Mago Arcano";
    }

    @Override
    public String getResourceSummary() {
        return "Maná: " + mana + "/" + maxMana;
    }
}

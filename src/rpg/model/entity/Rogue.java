package rpg.model.entity;

import java.io.Serial;
import java.util.List;
import rpg.exception.InvalidActionException;
import rpg.model.combat.EnergyUser;
import rpg.model.skill.Backstab;
import rpg.model.skill.BasicAttack;

/**
 * Especialización de héroe: Pícaro Sombrío.
 * Experto en ataques furtivos, golpes críticos y gestión ágil de energía por turno.
 */
public final class Rogue extends Player implements EnergyUser {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_ENERGY = 100;
    private int energy;
    private double baseCritChance;

    public Rogue(String name) {
        super(name, 110, 20, 8, 14);
        this.energy = MAX_ENERGY;
        this.baseCritChance = 0.25;
        learnSkill(new BasicAttack());
        learnSkill(new Backstab(35));
        learnSkill(new rpg.model.skill.PoisonStrike(25));
    }

    @Override
    public double getCriticalChance() {
        return baseCritChance;
    }

    @Override
    public double getCriticalMultiplier() {
        return 1.85;
    }

    @Override
    public void consumeEnergy(int amount) throws InvalidActionException {
        if (energy < amount) {
            throw new InvalidActionException(String.format("Energía insuficiente (%d/%d requeridos). Espera un turno para recuperarte.", energy, amount));
        }
        this.energy -= amount;
    }

    @Override
    public void regenerateEnergy(int amount) {
        this.energy = Math.max(0, Math.min(MAX_ENERGY, this.energy + amount));
    }

    @Override
    protected void onTurnStartHook(List<String> turnLogs) {
        int before = this.energy;
        regenerateEnergy(15);
        int gained = this.energy - before;
        if (gained > 0) {
            turnLogs.add(getName() + " recobra el aliento y regenera +" + gained + " de Energía.");
        }
    }

    @Override
    public int getCurrentEnergy() {
        return energy;
    }

    @Override
    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    @Override
    protected void applyLevelUpBonuses() {
        int baseHpWithoutGear = getMaxHealth() - getEquippedHealthBonus();
        setMaxHealth(baseHpWithoutGear + 18);
        setAttackPower(getBaseAttackPower() + 5);
        setDefense(getBaseDefense() + 2);
        this.baseCritChance = Math.min(0.50, this.baseCritChance + 0.02);
    }

    @Override
    public String getClassTitle() {
        return "Pícaro Sombrío";
    }

    @Override
    public String getResourceSummary() {
        return "Energía: " + energy + "/" + MAX_ENERGY;
    }
}

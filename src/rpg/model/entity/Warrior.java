package rpg.model.entity;

import java.io.Serial;
import rpg.exception.InvalidActionException;
import rpg.model.combat.FuryFighter;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.HeavyStrike;

/**
 * Especialización de héroe: Guerrero acorazado.
 * Emplea Furia generada en la batalla para propinar golpes devastadores.
 */
public final class Warrior extends Player implements FuryFighter {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_FURY = 100;
    private int fury;

    public Warrior(String name) {
        super(name, 135, 18, 12, 12);
        this.fury = 20;
        learnSkill(new BasicAttack());
        learnSkill(new HeavyStrike(35));
        learnSkill(new rpg.model.skill.IronGuard(15));
    }

    @Override
    public void onAttackLanded(int finalDamage) {
        gainFury(15);
    }

    @Override
    protected void onDamageTakenHook(int actualDamage) {
        if (actualDamage > 0) {
            gainFury(10);
        }
    }

    @Override
    public void consumeFury(int amount) throws InvalidActionException {
        if (fury < amount) {
            throw new InvalidActionException(String.format("Furia insuficiente (%d/%d requeridos). ¡Golpea o recibe daño!", fury, amount));
        }
        this.fury -= amount;
    }

    @Override
    public void gainFury(int amount) {
        this.fury = Math.max(0, Math.min(MAX_FURY, this.fury + amount));
    }

    @Override
    public int getCurrentFury() {
        return fury;
    }

    @Override
    public int getMaxFury() {
        return MAX_FURY;
    }

    @Override
    protected void applyLevelUpBonuses() {
        int baseHpWithoutGear = getMaxHealth() - getEquippedHealthBonus();
        setMaxHealth(baseHpWithoutGear + 24);
        setAttackPower(getBaseAttackPower() + 4);
        setDefense(getBaseDefense() + 3);
    }

    @Override
    public String getClassTitle() {
        return "Guerrero";
    }

    @Override
    public String getResourceSummary() {
        return "Furia: " + fury + "/" + MAX_FURY;
    }
}

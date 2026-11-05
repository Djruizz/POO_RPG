package rpg.model.item;

import java.io.Serial;
import rpg.exception.GameException;
import rpg.exception.InvalidActionException;
import rpg.model.combat.ManaCaster;
import rpg.model.entity.Player;
import rpg.model.enums.ItemType;
import rpg.model.enums.Rarity;

/**
 * Ítem consumible que restaura salud, maná o purifica efectos alterados al ser usado.
 */
public final class ConsumableItem extends Item implements Usable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int healAmount;
    private final int manaAmount;
    private final boolean cleansesDebuffs;

    public ConsumableItem(String id, String name, String description, Rarity rarity, int goldValue,
                          int healAmount, int manaAmount, boolean cleansesDebuffs) {
        super(id, name, description, rarity, ItemType.CONSUMABLE, goldValue);
        this.healAmount = Math.max(0, healAmount);
        this.manaAmount = Math.max(0, manaAmount);
        this.cleansesDebuffs = cleansesDebuffs;
    }

    @Override
    public String use(Player target) throws GameException {
        boolean effectApplied = false;
        StringBuilder report = new StringBuilder("Usaste " + getName() + ": ");

        if (healAmount > 0) {
            if (target.getCurrentHealth() < target.getMaxHealth()) {
                int healed = target.heal(healAmount);
                report.append("recuperas +").append(healed).append(" PV. ");
                effectApplied = true;
            }
        }

        if (manaAmount > 0) {
            if (target instanceof ManaCaster caster) {
                if (caster.getCurrentMana() < caster.getMaxMana()) {
                    int restored = caster.restoreMana(manaAmount);
                    report.append("recuperas +").append(restored).append(" PM. ");
                    effectApplied = true;
                }
            }
        }

        if (cleansesDebuffs) {
            boolean hadDebuffs = target.getActiveEffects().stream().anyMatch(e -> e.getType().isNegative());
            if (hadDebuffs) {
                target.clearNegativeEffects();
                report.append("¡Purificas todos los estados negativos! ");
                effectApplied = true;
            }
        }

        if (!effectApplied) {
            throw new InvalidActionException("No puedes usar " + getName() + ": tu salud/recursos ya están al máximo y no sufres estados negativos.");
        }

        return report.toString().trim();
    }

    @Override
    public boolean isConsumedOnUse() {
        return true;
    }

    @Override
    public String getSummaryStats() {
        StringBuilder sb = new StringBuilder();
        if (healAmount > 0) sb.append("+").append(healAmount).append(" PV ");
        if (manaAmount > 0) sb.append("+").append(manaAmount).append(" PM ");
        if (cleansesDebuffs) sb.append("Purificación ");
        return sb.toString().trim();
    }

    public int getHealAmount() {
        return healAmount;
    }

    public int getManaAmount() {
        return manaAmount;
    }

    public boolean isCleansesDebuffs() {
        return cleansesDebuffs;
    }
}

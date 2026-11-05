package rpg.model.item;

import java.io.Serial;
import java.util.Objects;
import rpg.model.enums.EquipmentSlot;
import rpg.model.enums.ItemType;
import rpg.model.enums.Rarity;

/**
 * Ítem equipable en una ranura corporal que confiere bonificadores pasivos.
 */
public final class EquipmentItem extends Item implements Equippable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final EquipmentSlot slot;
    private final int attackBonus;
    private final int defenseBonus;
    private final int maxHealthBonus;

    public EquipmentItem(String id, String name, String description, Rarity rarity, ItemType itemType,
                         int goldValue, EquipmentSlot slot, int attackBonus, int defenseBonus, int maxHealthBonus) {
        super(id, name, description, rarity, itemType, goldValue);
        this.slot = Objects.requireNonNull(slot, "La ranura de equipamiento no puede ser nula.");
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.maxHealthBonus = maxHealthBonus;
    }

    @Override
    public EquipmentSlot getSlot() {
        return slot;
    }

    @Override
    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public int getDefenseBonus() {
        return defenseBonus;
    }

    @Override
    public int getMaxHealthBonus() {
        return maxHealthBonus;
    }

    @Override
    public String getSummaryStats() {
        StringBuilder sb = new StringBuilder();
        sb.append(slot.getDisplayName()).append(" | ");
        if (attackBonus != 0) sb.append(attackBonus > 0 ? "+" : "").append(attackBonus).append(" ATQ ");
        if (defenseBonus != 0) sb.append(defenseBonus > 0 ? "+" : "").append(defenseBonus).append(" DEF ");
        if (maxHealthBonus != 0) sb.append(maxHealthBonus > 0 ? "+" : "").append(maxHealthBonus).append(" PV ");
        return sb.toString().trim();
    }
}

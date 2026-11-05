package rpg.model.item;

import rpg.model.enums.EquipmentSlot;

/**
 * Contrato para cualquier ítem que puede ser equipado en una ranura corporal,
 * confiriendo bonificadores pasivos a los atributos.
 */
public interface Equippable {
    EquipmentSlot getSlot();
    int getAttackBonus();
    int getDefenseBonus();
    int getMaxHealthBonus();
}

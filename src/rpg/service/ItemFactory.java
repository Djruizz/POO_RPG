package rpg.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.enums.EquipmentSlot;
import rpg.model.enums.ItemType;
import rpg.model.enums.Rarity;
import rpg.model.item.ConsumableItem;
import rpg.model.item.EquipmentItem;
import rpg.model.item.Item;

/**
 * Fábrica de ítems balanceada por piso y rareza.
 */
public final class ItemFactory {

    public Item createHealthPotion(int floor) {
        int heal = 40 + (floor * 15);
        return new ConsumableItem(
                "pot_hp_" + UUID.randomUUID().toString().substring(0, 5),
                "Poción de Vida (" + heal + " PV)",
                "Brebaje revitalizante que restaura salud al instante.",
                floor >= 2 ? Rarity.UNCOMMON : Rarity.COMMON,
                15 * floor,
                heal,
                0,
                false
        );
    }

    public Item createManaPotion(int floor) {
        int mana = 35 + (floor * 12);
        return new ConsumableItem(
                "pot_mp_" + UUID.randomUUID().toString().substring(0, 5),
                "Poción de Maná (" + mana + " PM)",
                "Esencia arcana líquida que reconstituye reservas mágicas.",
                floor >= 2 ? Rarity.UNCOMMON : Rarity.COMMON,
                15 * floor,
                0,
                mana,
                false
        );
    }

    public Item createElixir(int floor) {
        int val = 50 + (floor * 20);
        return new ConsumableItem(
                "elix_" + UUID.randomUUID().toString().substring(0, 5),
                "Elixir de Purificación",
                "Restaura salud, maná y purifica cualquier veneno o quemadura.",
                Rarity.RARE,
                40 * floor,
                val,
                val,
                true
        );
    }

    public Item createWeapon(int floor, boolean bossDrop) {
        Rarity rarity = pickRarity(floor, bossDrop);
        int multiplier = rarity.getTierWeight();
        int attackBonus = (4 * floor) + (multiplier * 3);
        String name = switch (rarity) {
            case COMMON -> "Espada de Acero Forjado";
            case UNCOMMON -> "Hoja de Plata Rúnica";
            case RARE -> "Filo del Verdugo Sombrío";
            case EPIC -> "Espadón del Dragón Abisal";
            case LEGENDARY -> "Segadora de Almas de la Eternidad";
        };

        return new EquipmentItem(
                "wpn_" + UUID.randomUUID().toString().substring(0, 5),
                name,
                "Arma balanceada imbuida con poder ofensivo creciente.",
                rarity,
                ItemType.WEAPON,
                30 * multiplier * floor,
                EquipmentSlot.WEAPON,
                attackBonus,
                0,
                0
        );
    }

    public Item createArmor(int floor, boolean bossDrop) {
        Rarity rarity = pickRarity(floor, bossDrop);
        int multiplier = rarity.getTierWeight();
        int defBonus = (3 * floor) + (multiplier * 2);
        int hpBonus = (10 * floor) + (multiplier * 8);
        String name = switch (rarity) {
            case COMMON -> "Coraza de Malla Pesada";
            case UNCOMMON -> "Armadura de Acero Templado";
            case RARE -> "Cota de Escamas de Basilisco";
            case EPIC -> "Placas del Titán Olvidado";
            case LEGENDARY -> "Armadura Imperecedera del Campeón";
        };

        return new EquipmentItem(
                "arm_" + UUID.randomUUID().toString().substring(0, 5),
                name,
                "Robusta protección que incrementa la defensa física y salud máxima.",
                rarity,
                ItemType.ARMOR,
                25 * multiplier * floor,
                EquipmentSlot.ARMOR,
                0,
                defBonus,
                hpBonus
        );
    }

    public Item createHelmet(int floor, boolean bossDrop) {
        Rarity rarity = pickRarity(floor, bossDrop);
        int multiplier = rarity.getTierWeight();
        int defBonus = (2 * floor) + multiplier;
        int hpBonus = (8 * floor) + (multiplier * 5);
        String name = switch (rarity) {
            case COMMON -> "Yelmo de Cuero Endurecido";
            case UNCOMMON -> "Casco de Acero Reforzado";
            case RARE -> "Celada de Malla Dracónica";
            case EPIC -> "Corona del Señor de la Guerra";
            case LEGENDARY -> "Diadema Celestial de la Luz";
        };

        return new EquipmentItem(
                "hlm_" + UUID.randomUUID().toString().substring(0, 5),
                name,
                "Protección para la cabeza que refuerza la vida y resistencia física.",
                rarity,
                ItemType.ARMOR,
                22 * multiplier * floor,
                EquipmentSlot.HELMET,
                0,
                defBonus,
                hpBonus
        );
    }

    public Item createAccessory(int floor, boolean bossDrop) {
        Rarity rarity = pickRarity(floor, bossDrop);
        int multiplier = rarity.getTierWeight();
        int atk = 2 * multiplier;
        int def = 2 * multiplier;
        int hp = 15 * multiplier;
        return new EquipmentItem(
                "acc_" + UUID.randomUUID().toString().substring(0, 5),
                "Amuleto del Inframundo",
                "Reliquia ancestral que refuerza todos los atributos vitales.",
                rarity,
                ItemType.ACCESSORY,
                35 * multiplier * floor,
                EquipmentSlot.ACCESSORY,
                atk,
                def,
                hp
        );
    }

    public Item rollLootDrop(int floor, boolean bossDrop) {
        double roll = ThreadLocalRandom.current().nextDouble();
        if (bossDrop) {
            if (roll < 0.30) return createWeapon(floor, true);
            if (roll < 0.55) return createArmor(floor, true);
            if (roll < 0.80) return createHelmet(floor, true);
            return createAccessory(floor, true);
        }
        if (roll < 0.25) return createHealthPotion(floor);
        if (roll < 0.45) return createManaPotion(floor);
        if (roll < 0.58) return createElixir(floor);
        if (roll < 0.72) return createWeapon(floor, false);
        if (roll < 0.82) return createArmor(floor, false);
        if (roll < 0.92) return createHelmet(floor, false);
        return createAccessory(floor, false);
    }

    private Rarity pickRarity(int floor, boolean bossDrop) {
        if (bossDrop) {
            return floor >= 3 ? Rarity.LEGENDARY : (floor == 2 ? Rarity.EPIC : Rarity.RARE);
        }
        double roll = ThreadLocalRandom.current().nextDouble();
        if (floor == 1) {
            return roll < 0.75 ? Rarity.COMMON : Rarity.UNCOMMON;
        } else if (floor == 2) {
            if (roll < 0.40) return Rarity.COMMON;
            if (roll < 0.80) return Rarity.UNCOMMON;
            return Rarity.RARE;
        } else {
            if (roll < 0.30) return Rarity.UNCOMMON;
            if (roll < 0.70) return Rarity.RARE;
            return Rarity.EPIC;
        }
    }
}

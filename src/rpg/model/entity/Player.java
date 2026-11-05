package rpg.model.entity;

import java.io.Serial;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import rpg.exception.GameException;
import rpg.exception.InvalidActionException;
import rpg.exception.InventoryFullException;
import rpg.exception.ItemNotFoundException;
import rpg.model.enums.EquipmentSlot;
import rpg.model.item.Equippable;
import rpg.model.item.Inventory;
import rpg.model.item.Item;
import rpg.model.item.Usable;

/**
 * Subclase abstracta que define las características del héroe controlado por el usuario:
 * progresión de nivel y experiencia, monedero de oro, inventario y gestión de equipamiento por ranura.
 */
public abstract class Player extends Character {
    @Serial
    private static final long serialVersionUID = 1L;

    private int experience;
    private int experienceToNextLevel;
    private int gold;
    private final Inventory inventory;
    @SuppressWarnings("serial")
    private final Map<EquipmentSlot, Equippable> equippedItems;

    protected Player(String name, int maxHealth, int attackPower, int defense, int inventoryCapacity) {
        super(name, maxHealth, attackPower, defense, 1);
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.gold = 30;
        this.inventory = new Inventory(inventoryCapacity);
        this.equippedItems = new EnumMap<>(EquipmentSlot.class);
    }

    @Override
    public int getMaxHealth() {
        int bonus = equippedItems == null ? 0 : equippedItems.values().stream()
                .mapToInt(Equippable::getMaxHealthBonus)
                .sum();
        return super.getMaxHealth() + bonus;
    }

    @Override
    public int getEffectiveAttack() {
        int bonus = equippedItems == null ? 0 : equippedItems.values().stream()
                .mapToInt(Equippable::getAttackBonus)
                .sum();
        return super.getEffectiveAttack() + bonus;
    }

    @Override
    public int getEffectiveDefense() {
        int bonus = equippedItems == null ? 0 : equippedItems.values().stream()
                .mapToInt(Equippable::getDefenseBonus)
                .sum();
        return super.getEffectiveDefense() + bonus;
    }

    protected int getEquippedHealthBonus() {
        return equippedItems == null ? 0 : equippedItems.values().stream()
                .mapToInt(Equippable::getMaxHealthBonus)
                .sum();
    }

    public Equippable equipItem(String itemId) throws ItemNotFoundException, InvalidActionException {
        Item found = inventory.findById(itemId);
        if (!(found instanceof Equippable equippable)) {
            throw new InvalidActionException("El ítem '" + found.getName() + "' no se puede equipar.");
        }

        inventory.removeItem(found);
        Equippable previous = equippedItems.put(equippable.getSlot(), equippable);
        if (previous instanceof Item previousItem) {
            try {
                inventory.addItem(previousItem);
            } catch (InventoryFullException e) {
                // Revertir para mantener invariantes
                equippedItems.put(equippable.getSlot(), previous);
                try {
                    inventory.addItem(found);
                } catch (InventoryFullException ignored) {}
                throw new InvalidActionException("Inventario lleno: no se pudo guardar el equipo anterior.");
            }
        }
        setCurrentHealth(getCurrentHealth());
        return previous;
    }

    public Equippable unequipItem(EquipmentSlot slot) throws InvalidActionException, InventoryFullException {
        if (!equippedItems.containsKey(slot)) {
            throw new InvalidActionException("No tienes ningún equipo colocado en la ranura de " + slot.getDisplayName() + ".");
        }
        if (inventory.isFull()) {
            throw new InventoryFullException(inventory.getMaxCapacity());
        }
        Equippable unequipped = equippedItems.remove(slot);
        if (unequipped instanceof Item item) {
            inventory.addItem(item);
        }
        setCurrentHealth(getCurrentHealth());
        return unequipped;
    }

    public String useInventoryItem(String itemId) throws GameException {
        Item found = inventory.findById(itemId);
        if (!(found instanceof Usable usable)) {
            throw new InvalidActionException("El ítem '" + found.getName() + "' no es un objeto consumible.");
        }
        String effectReport = usable.use(this);
        if (usable.isConsumedOnUse()) {
            inventory.removeItem(found);
        }
        return effectReport;
    }

    public int gainExperience(int expAmount) {
        if (expAmount < 0) {
            throw new IllegalArgumentException("La experiencia no puede ser negativa.");
        }
        this.experience += expAmount;
        int levelsGained = 0;
        while (this.experience >= this.experienceToNextLevel) {
            this.experience -= this.experienceToNextLevel;
            setLevel(getLevel() + 1);
            this.experienceToNextLevel = (int) Math.round(this.experienceToNextLevel * 1.35);
            applyLevelUpBonuses();
            setCurrentHealth(getMaxHealth());
            levelsGained++;
        }
        return levelsGained;
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("El oro a añadir no puede ser negativo.");
        }
        this.gold += amount;
    }

    public void spendGold(int amount) throws InvalidActionException {
        if (amount < 0) {
            throw new IllegalArgumentException("La cantidad de oro no puede ser negativa.");
        }
        if (this.gold < amount) {
            throw new InvalidActionException("Oro insuficiente. Tienes " + gold + " pero requieres " + amount + " oro.");
        }
        this.gold -= amount;
    }

    protected abstract void applyLevelUpBonuses();

    public Inventory getInventory() {
        return inventory;
    }

    public Map<EquipmentSlot, Equippable> getEquippedItems() {
        return Collections.unmodifiableMap(equippedItems);
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    public int getGold() {
        return gold;
    }
}

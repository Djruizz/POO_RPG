package rpg.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.enums.StatusEffectType;
import rpg.model.skill.Skill;

/**
 * Clase abstracta raíz que modela los atributos comunes y el comportamiento vital
 * de todos los personajes y criaturas del juego.
 */
public abstract class Character implements Combatant, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private int maxHealth;
    private int currentHealth;
    private int attackPower;
    private int defense;
    private int level;
    @SuppressWarnings("serial")
    private final Set<ActiveStatusEffect> activeEffects;
    @SuppressWarnings("serial")
    private final List<Skill> skills;

    protected Character(String name, int maxHealth, int attackPower, int defense, int level) {
        setName(name);
        setLevel(level);
        setMaxHealth(maxHealth);
        this.currentHealth = this.maxHealth;
        setAttackPower(attackPower);
        setDefense(defense);
        this.activeEffects = new LinkedHashSet<>();
        this.skills = new ArrayList<>();
    }

    @Override
    public int takeDamage(int rawDamage) {
        if (rawDamage < 0) {
            throw new IllegalArgumentException("El daño bruto no puede ser negativo.");
        }
        int effectiveDef = getEffectiveDefense();
        int mitigated = Math.max(1, rawDamage - (effectiveDef / 2));
        int previousHealth = this.currentHealth;
        setCurrentHealth(this.currentHealth - mitigated);
        int actualLost = previousHealth - this.currentHealth;
        onDamageTakenHook(actualLost);
        return actualLost;
    }

    public int takeTrueDamage(int directDamage) {
        if (directDamage < 0) {
            throw new IllegalArgumentException("El daño directo no puede ser negativo.");
        }
        int previousHealth = this.currentHealth;
        setCurrentHealth(this.currentHealth - directDamage);
        int actualLost = previousHealth - this.currentHealth;
        onDamageTakenHook(actualLost);
        return actualLost;
    }

    @Override
    public int heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("La cantidad de curación no puede ser negativa.");
        }
        int previousHealth = this.currentHealth;
        setCurrentHealth(this.currentHealth + amount);
        return this.currentHealth - previousHealth;
    }

    @Override
    public boolean isAlive() {
        return this.currentHealth > 0;
    }

    @Override
    public void applyStatusEffect(ActiveStatusEffect effect) {
        Objects.requireNonNull(effect, "El efecto de estado no puede ser nulo.");
        this.activeEffects.remove(effect); // Refresca si ya existía el mismo tipo
        this.activeEffects.add(effect);
    }

    @Override
    public List<String> processTurnStartEffects() {
        List<String> logs = new ArrayList<>();
        onTurnStartHook(logs);

        Iterator<ActiveStatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            ActiveStatusEffect effect = iterator.next();
            if (effect.getType() == StatusEffectType.POISON || effect.getType() == StatusEffectType.BURN) {
                int lost = takeTrueDamage(effect.getPotency());
                logs.add(String.format("%s sufre %d de daño por %s.",
                        name, lost, effect.getType().getDisplayName()));
            }
            effect.decrementTurn();
            if (effect.isExpired()) {
                iterator.remove();
                logs.add(String.format("El efecto %s en %s se ha disipado.",
                        effect.getType().getDisplayName(), name));
            }
        }
        return logs;
    }

    @Override
    public boolean isStunned() {
        return activeEffects.stream().anyMatch(e -> e.getType() == StatusEffectType.STUN);
    }

    public void clearNegativeEffects() {
        activeEffects.removeIf(e -> e.getType().isNegative());
    }

    @Override
    public void onAttackLanded(int finalDamage) {
        // Hook opcional para subclases (ej. Guerrero gana Furia al asestar daño)
    }

    protected void onDamageTakenHook(int actualDamage) {
        // Hook opcional para subclases (ej. Guerrero gana Furia al recibir daño)
    }

    protected void onTurnStartHook(List<String> turnLogs) {
        // Hook opcional para subclases (ej. Mago o Pícaro regeneran recursos)
    }

    protected final void learnSkill(Skill skill) {
        Objects.requireNonNull(skill, "La habilidad no puede ser nula.");
        this.skills.add(skill);
    }

    @Override
    public String getName() {
        return name;
    }

    protected final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del personaje no puede estar vacío.");
        }
        this.name = name.trim();
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    protected final void setMaxHealth(int maxHealth) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("La salud máxima debe ser mayor a cero.");
        }
        this.maxHealth = maxHealth;
        if (this.currentHealth > this.maxHealth) {
            this.currentHealth = this.maxHealth;
        }
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    protected final void setCurrentHealth(int value) {
        this.currentHealth = Math.max(0, Math.min(value, getMaxHealth()));
    }

    public int getBaseAttackPower() {
        return attackPower;
    }

    protected final void setAttackPower(int attackPower) {
        if (attackPower < 0) {
            throw new IllegalArgumentException("El poder de ataque no puede ser negativo.");
        }
        this.attackPower = attackPower;
    }

    public int getBaseDefense() {
        return defense;
    }

    protected final void setDefense(int defense) {
        if (defense < 0) {
            throw new IllegalArgumentException("La defensa no puede ser negativa.");
        }
        this.defense = defense;
    }

    @Override
    public int getEffectiveAttack() {
        return attackPower;
    }

    @Override
    public int getEffectiveDefense() {
        int fortifyBonus = activeEffects.stream()
                .filter(e -> e.getType() == StatusEffectType.FORTIFY)
                .mapToInt(ActiveStatusEffect::getPotency)
                .sum();
        return defense + fortifyBonus;
    }

    @Override
    public double getCriticalChance() {
        return 0.05;
    }

    @Override
    public double getCriticalMultiplier() {
        return 1.50;
    }

    @Override
    public int getLevel() {
        return level;
    }

    protected final void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("El nivel mínimo es 1.");
        }
        this.level = level;
    }

    @Override
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    @Override
    public Set<ActiveStatusEffect> getActiveEffects() {
        return Collections.unmodifiableSet(activeEffects);
    }

    public abstract String getClassTitle();
    public abstract String getResourceSummary();
}

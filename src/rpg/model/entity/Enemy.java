package rpg.model.entity;

import java.io.Serial;
import rpg.model.combat.Combatant;
import rpg.model.skill.Skill;

/**
 * Subclase abstracta de criaturas hostiles de la mazmorra.
 * Incluye escalado paramétrico de estadísticas según el piso actual y contrato de IA para toma de decisiones.
 */
public abstract class Enemy extends Character {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int experienceReward;
    private final int goldReward;
    private final boolean boss;

    protected Enemy(String name, int baseHp, int baseAtk, int baseDef,
                    int floor, double growthRate, int baseExp, int baseGold, boolean boss) {
        super(
                name,
                scaleStat(baseHp, floor, growthRate),
                scaleStat(baseAtk, floor, growthRate),
                scaleStat(baseDef, floor, growthRate),
                floor
        );
        this.experienceReward = scaleStat(baseExp, floor, 0.35);
        this.goldReward = scaleStat(baseGold, floor, 0.30);
        this.boss = boss;
    }

    /**
     * Algoritmo de escalado matemático de estadísticas por piso.
     */
    protected static int scaleStat(int base, int floor, double growthRate) {
        if (floor < 1) {
            throw new IllegalArgumentException("El número de piso debe ser al menos 1.");
        }
        return (int) Math.round(base * (1.0 + (floor - 1) * growthRate));
    }

    /**
     * Algoritmo de Inteligencia Artificial que selecciona la habilidad a ejecutar según el estado del combate.
     * @param target Oponente (generalmente el jugador).
     * @return Habilidad seleccionada polimórficamente.
     */
    public abstract Skill chooseSkill(Combatant target);

    public int getExperienceReward() {
        return experienceReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public boolean isBoss() {
        return boss;
    }

    @Override
    public String getResourceSummary() {
        return "Nivel de Amenaza: Piso " + getLevel();
    }
}

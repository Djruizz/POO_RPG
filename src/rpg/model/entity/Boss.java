package rpg.model.entity;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.combat.Combatant;
import rpg.model.lore.FloorTheme;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.Fireball;
import rpg.model.skill.Heal;
import rpg.model.skill.HeavyStrike;
import rpg.model.skill.Skill;

/**
 * Jefe de piso de alta dificultad con mecánicas de fases y estado Enfurecido.
 * Su identidad (nombre y diálogos) proviene del {@link FloorTheme} del piso que custodia.
 */
public final class Boss extends Enemy {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Skill basicSlash;
    private final Skill crushingBlow;
    private final Skill infernoBurst;
    private final Skill darkRecovery;
    private boolean usedSelfHeal;
    private boolean enraged;
    private final FloorTheme theme;

    public Boss(int floor) {
        super(FloorTheme.forFloor(floor).getBossName(), 175, 23, 14, floor, 0.35, 160, 110, true);
        this.basicSlash = new BasicAttack();
        this.crushingBlow = new HeavyStrike(0);
        this.infernoBurst = new Fireball(0);
        this.darkRecovery = new Heal(0);
        this.usedSelfHeal = false;
        this.enraged = false;
        this.theme = FloorTheme.forFloor(floor);

        learnSkill(basicSlash);
        learnSkill(crushingBlow);
        learnSkill(infernoBurst);
        learnSkill(darkRecovery);
    }

    @Override
    public int getEffectiveAttack() {
        int base = super.getEffectiveAttack();
        return enraged ? (int) Math.round(base * 1.30) : base;
    }

    @Override
    public Skill chooseSkill(Combatant target) {
        double healthPercentage = (double) getCurrentHealth() / getMaxHealth();

        // Si la vida cae bajo el 45%, entra en Furia
        if (healthPercentage < 0.45 && !enraged) {
            this.enraged = true;
        }

        // Táctica de autocuración cuando la salud cae por debajo del 30% (máximo una vez)
        if (healthPercentage < 0.30 && !usedSelfHeal) {
            this.usedSelfHeal = true;
            return darkRecovery;
        }

        // Si está enfurecido, usa sus habilidades de alto impacto
        if (enraged) {
            double roll = ThreadLocalRandom.current().nextDouble();
            if (roll < 0.50) return infernoBurst;
            if (roll < 0.85) return crushingBlow;
            return basicSlash;
        }

        // Rotación normal
        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < 0.40) return basicSlash;
        if (roll < 0.70) return crushingBlow;
        return infernoBurst;
    }

    public boolean isEnraged() {
        return enraged;
    }

    /** Frase al iniciar el encuentro; {@code null} en partidas guardadas antes de existir los temas. */
    public String getIntroLine() {
        return theme == null ? null : theme.getBossIntroLine();
    }

    /** Frase al entrar en furia; {@code null} si el jefe no tiene tema asociado. */
    public String getEnrageLine() {
        return theme == null ? null : theme.getBossEnrageLine();
    }

    /** Últimas palabras al caer; {@code null} si el jefe no tiene tema asociado. */
    public String getDeathLine() {
        return theme == null ? null : theme.getBossDeathLine();
    }

    @Override
    public String getClassTitle() {
        return enraged ? "¡JEFE ENFURECIDO!" : "Jefe de Piso";
    }

    @Override
    public String getResourceSummary() {
        return enraged ? "Fase: ¡Furia Abisal (+30% ATQ)!" : "Fase: Normal";
    }
}

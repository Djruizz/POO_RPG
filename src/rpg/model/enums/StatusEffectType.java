package rpg.model.enums;

import rpg.ui.AnsiColor;

/**
 * Tipos de efectos de estado activos que pueden afectar a un combatiente durante las rondas de combate.
 */
public enum StatusEffectType {
    POISON("Veneno", true, AnsiColor.GREEN),
    BURN("Quemadura", true, AnsiColor.RED),
    STUN("Aturdimiento", true, AnsiColor.YELLOW),
    FORTIFY("Fortificación", false, AnsiColor.CYAN);

    private final String displayName;
    private final boolean negative;
    private final AnsiColor color;

    StatusEffectType(String displayName, boolean negative, AnsiColor color) {
        this.displayName = displayName;
        this.negative = negative;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNegative() {
        return negative;
    }

    public AnsiColor getColor() {
        return color;
    }
}

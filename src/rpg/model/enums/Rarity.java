package rpg.model.enums;

import rpg.ui.AnsiColor;

/**
 * Representa la rareza de un ítem, determinando su ponderación en ordenamientos, color y calidad.
 */
public enum Rarity {
    COMMON(1, "Común", AnsiColor.WHITE),
    UNCOMMON(2, "Poco Común", AnsiColor.BRIGHT_GREEN),
    RARE(3, "Raro", AnsiColor.BRIGHT_BLUE),
    EPIC(4, "Épico", AnsiColor.BRIGHT_PURPLE),
    LEGENDARY(5, "Legendario", AnsiColor.BRIGHT_YELLOW);

    private final int tierWeight;
    private final String displayName;
    private final AnsiColor color;

    Rarity(int tierWeight, String displayName, AnsiColor color) {
        this.tierWeight = tierWeight;
        this.displayName = displayName;
        this.color = color;
    }

    public int getTierWeight() {
        return tierWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AnsiColor getColor() {
        return color;
    }

    public String format(String text) {
        return color.boldWrap(text);
    }
}

package rpg.model.enums;

import rpg.ui.AnsiColor;

/**
 * Tipos de salas que componen cada piso de la mazmorra.
 */
public enum RoomType {
    ENTRANCE("Entrada", "[E]", AnsiColor.BRIGHT_CYAN),
    EMPTY("Pasillo Despejado", "[·]", AnsiColor.BRIGHT_BLACK),
    COMBAT("Encuentro Hostil", "[!]", AnsiColor.BRIGHT_RED),
    TREASURE("Cámara del Tesoro", "[$]", AnsiColor.BRIGHT_YELLOW),
    SHRINE("Santuario de Reposo", "[+]", AnsiColor.BRIGHT_GREEN),
    SHOP("Puesto del Mercader", "[M]", AnsiColor.BRIGHT_BLUE),
    BOSS("Cámara del Jefe", "[B]", AnsiColor.BRIGHT_PURPLE);

    private final String description;
    private final String symbol;
    private final AnsiColor color;

    RoomType(String description, String symbol, AnsiColor color) {
        this.description = description;
        this.symbol = symbol;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public AnsiColor getColor() {
        return color;
    }

    public String getFormattedSymbol() {
        return color.boldWrap(symbol);
    }
}

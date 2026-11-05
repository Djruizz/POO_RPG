package rpg.model.enums;

/**
 * Puntos cardinales de desplazamiento en la mazmorra.
 */
public enum Direction {
    NORTH(-1, 0, "Norte"),
    SOUTH(1, 0, "Sur"),
    EAST(0, 1, "Este"),
    WEST(0, -1, "Oeste");

    private final int deltaRow;
    private final int deltaCol;
    private final String displayName;

    Direction(int deltaRow, int deltaCol, String displayName) {
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
        this.displayName = displayName;
    }

    public int getDeltaRow() {
        return deltaRow;
    }

    public int getDeltaCol() {
        return deltaCol;
    }

    public String getDisplayName() {
        return displayName;
    }
}

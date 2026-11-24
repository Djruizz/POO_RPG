package rpg.model.dungeon;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import rpg.exception.InvalidActionException;
import rpg.model.enums.Direction;

/**
 * Representa un piso completo de la mazmorra estructurado en una cuadrícula bidimensional.
 */
public final class DungeonFloor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_SIZE = 5;

    private final int floorNumber;
    private final int rows;
    private final int cols;
    private final Room[][] grid;
    private int playerRow;
    private int playerCol;

    public DungeonFloor(int floorNumber, Room[][] grid) {
        if (floorNumber < 1) {
            throw new IllegalArgumentException("El número de piso debe ser >= 1.");
        }
        this.floorNumber = floorNumber;
        this.grid = Objects.requireNonNull(grid, "La cuadrícula no puede ser nula.");
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.playerRow = 0;
        this.playerCol = 0;
        this.grid[0][0].markVisited();
    }

    public boolean canMove(Direction direction) {
        if (direction == null) return false;
        int nextR = playerRow + direction.getDeltaRow();
        int nextC = playerCol + direction.getDeltaCol();
        return nextR >= 0 && nextR < rows && nextC >= 0 && nextC < cols;
    }

    public Room move(Direction direction) throws InvalidActionException {
        if (!canMove(direction)) {
            throw new InvalidActionException("Muros impenetrables bloquean el paso hacia el " + direction.getDisplayName() + ".");
        }
        this.playerRow += direction.getDeltaRow();
        this.playerCol += direction.getDeltaCol();
        Room current = getCurrentRoom();
        current.markVisited();
        return current;
    }

    public Room getCurrentRoom() {
        return grid[playerRow][playerCol];
    }

    public Room getRoom(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IndexOutOfBoundsException("Coordenadas fuera de la mazmorra: (" + r + "," + c + ")");
        }
        return grid[r][c];
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }
}

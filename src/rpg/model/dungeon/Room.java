package rpg.model.dungeon;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import rpg.model.entity.Enemy;
import rpg.model.enums.RoomType;
import rpg.model.item.Item;

/**
 * Celda individual de la mazmorra con coordenadas, tipo de evento, enemigo o botín asociado y estado de niebla.
 */
public final class Room implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;
    private final RoomType type;
    private Enemy enemy;
    private Item loot;
    private boolean visited;
    private boolean cleared;

    public Room(int row, int col, RoomType type) {
        this.row = row;
        this.col = col;
        this.type = Objects.requireNonNull(type, "El tipo de sala no puede ser nulo.");
        this.visited = false;
        this.cleared = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public RoomType getType() {
        return type;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public Item getLoot() {
        return loot;
    }

    public void setLoot(Item loot) {
        this.loot = loot;
    }

    public boolean isVisited() {
        return visited;
    }

    public void markVisited() {
        this.visited = true;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void markCleared() {
        this.cleared = true;
    }
}

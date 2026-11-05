package rpg.model.dungeon;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import rpg.model.entity.Player;

/**
 * Instantánea serializable completa que encapsula todo el estado activo de la partida.
 */
public final class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_FLOORS = 3;

    private final Player player;
    private DungeonFloor currentFloor;
    private final int maxFloors;
    private boolean victory;

    public GameState(Player player, DungeonFloor currentFloor) {
        this(player, currentFloor, DEFAULT_MAX_FLOORS);
    }

    public GameState(Player player, DungeonFloor currentFloor, int maxFloors) {
        this.player = Objects.requireNonNull(player, "El jugador no puede ser nulo.");
        this.currentFloor = Objects.requireNonNull(currentFloor, "El piso inicial no puede ser nulo.");
        this.maxFloors = Math.max(1, maxFloors);
        this.victory = false;
    }

    public void advanceFloor(DungeonFloor nextFloor) {
        this.currentFloor = Objects.requireNonNull(nextFloor, "El siguiente piso no puede ser nulo.");
    }

    public Player getPlayer() {
        return player;
    }

    public DungeonFloor getCurrentFloor() {
        return currentFloor;
    }

    public int getMaxFloors() {
        return maxFloors;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }
}

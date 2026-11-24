package rpg.model.dungeon;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import rpg.model.entity.Player;
import rpg.model.lore.LoreFragment;

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
    /** Códice del héroe. Puede ser {@code null} en partidas guardadas antes de existir el códice. */
    private EnumSet<LoreFragment> discoveredLore;

    public GameState(Player player, DungeonFloor currentFloor) {
        this(player, currentFloor, DEFAULT_MAX_FLOORS);
    }

    public GameState(Player player, DungeonFloor currentFloor, int maxFloors) {
        this.player = Objects.requireNonNull(player, "El jugador no puede ser nulo.");
        this.currentFloor = Objects.requireNonNull(currentFloor, "El piso inicial no puede ser nulo.");
        this.maxFloors = Math.max(1, maxFloors);
        this.victory = false;
        this.discoveredLore = EnumSet.noneOf(LoreFragment.class);
    }

    /**
     * Registra un fragmento en el códice.
     * @return {true} si el fragmento era nuevo, {false} si ya estaba registrado.
     */
    public boolean discoverLore(LoreFragment fragment) {
        Objects.requireNonNull(fragment, "El fragmento no puede ser nulo.");
        return codex().add(fragment);
    }

    /** Vista de solo lectura del códice, en orden de lectura. */
    public Set<LoreFragment> getDiscoveredLore() {
        return Collections.unmodifiableSet(codex());
    }

    private Set<LoreFragment> codex() {
        if (discoveredLore == null) {
            discoveredLore = EnumSet.noneOf(LoreFragment.class);
        }
        return discoveredLore;
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

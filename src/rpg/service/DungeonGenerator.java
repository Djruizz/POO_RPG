package rpg.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.dungeon.DungeonFloor;
import rpg.model.dungeon.Room;
import rpg.model.entity.Boss;
import rpg.model.entity.Goblin;
import rpg.model.entity.Skeleton;
import rpg.model.enums.RoomType;
import rpg.model.lore.FloorTheme;
import rpg.model.lore.LoreFragment;

/**
 * Generador procedural balanceado de pisos de la mazmorra con eventos, enemigos, tesoros
 * e inscripciones de historia distribuidos.
 */
public final class DungeonGenerator {
    private final ItemFactory itemFactory;

    public DungeonGenerator(ItemFactory itemFactory) {
        this.itemFactory = Objects.requireNonNull(itemFactory, "La fábrica de ítems no puede ser nula.");
    }

    public DungeonFloor generateFloor(int floorNumber) {
        final int size = DungeonFloor.DEFAULT_SIZE;
        Room[][] grid = new Room[size][size];
        boolean[][] reserved = new boolean[size][size];

        // Entrada en (0, 0) y Jefe en (4, 4)
        grid[0][0] = new Room(0, 0, RoomType.ENTRANCE);
        grid[0][0].markCleared();
        reserved[0][0] = true;

        Room bossRoom = new Room(size - 1, size - 1, RoomType.BOSS);
        bossRoom.setEnemy(new Boss(floorNumber));
        grid[size - 1][size - 1] = bossRoom;
        reserved[size - 1][size - 1] = true;

        // Santuario garantizado en (2, 2) y Mercader Ambulante en (1, 3)
        grid[2][2] = new Room(2, 2, RoomType.SHRINE);
        reserved[2][2] = true;
        grid[1][3] = new Room(1, 3, RoomType.SHOP);
        reserved[1][3] = true;

        placeInscriptions(floorNumber, grid, reserved);

        // Distribuir eventos en las celdas restantes
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (reserved[r][c]) {
                    continue;
                }
                double roll = ThreadLocalRandom.current().nextDouble();
                if (roll < 0.45) {
                    // Sala de Combate
                    Room combatRoom = new Room(r, c, RoomType.COMBAT);
                    boolean isSkeleton = ThreadLocalRandom.current().nextBoolean();
                    combatRoom.setEnemy(isSkeleton ? new Skeleton(floorNumber) : new Goblin(floorNumber));
                    grid[r][c] = combatRoom;
                } else if (roll < 0.65) {
                    // Sala del Tesoro
                    Room treasureRoom = new Room(r, c, RoomType.TREASURE);
                    treasureRoom.setLoot(itemFactory.rollLootDrop(floorNumber, false));
                    grid[r][c] = treasureRoom;
                } else if (roll < 0.72) {
                    // Santuario adicional
                    grid[r][c] = new Room(r, c, RoomType.SHRINE);
                } else {
                    // Pasillo despejado
                    grid[r][c] = new Room(r, c, RoomType.EMPTY);
                }
            }
        }

        return new DungeonFloor(floorNumber, grid);
    }

    /**
     * Coloca en celdas libres al azar una sala de inscripción por cada fragmento de historia
     * del tema del piso. Los pisos más profundos que el último tema no reciben inscripciones,
     * para no repetir fragmentos.
     */
    private void placeInscriptions(int floorNumber, Room[][] grid, boolean[][] reserved) {
        FloorTheme theme = FloorTheme.forFloor(floorNumber);
        if (theme.getFloorNumber() != floorNumber) {
            return;
        }

        List<int[]> freeCells = new ArrayList<>();
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (!reserved[r][c]) {
                    freeCells.add(new int[] {r, c});
                }
            }
        }
        Collections.shuffle(freeCells, ThreadLocalRandom.current());

        List<LoreFragment> fragments = LoreFragment.forTheme(theme);
        for (int i = 0; i < fragments.size() && i < freeCells.size(); i++) {
            int r = freeCells.get(i)[0];
            int c = freeCells.get(i)[1];
            Room inscriptionRoom = new Room(r, c, RoomType.INSCRIPTION);
            inscriptionRoom.setLoreFragment(fragments.get(i));
            grid[r][c] = inscriptionRoom;
            reserved[r][c] = true;
        }
    }
}

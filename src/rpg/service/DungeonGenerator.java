package rpg.service;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import rpg.model.dungeon.DungeonFloor;
import rpg.model.dungeon.Room;
import rpg.model.entity.Boss;
import rpg.model.entity.Goblin;
import rpg.model.entity.Skeleton;
import rpg.model.enums.RoomType;

/**
 * Generador procedural balanceado de pisos de la mazmorra con eventos, enemigos y tesoros distribuidos.
 */
public final class DungeonGenerator {
    private final ItemFactory itemFactory;

    public DungeonGenerator(ItemFactory itemFactory) {
        this.itemFactory = Objects.requireNonNull(itemFactory, "La fábrica de ítems no puede ser nula.");
    }

    public DungeonFloor generateFloor(int floorNumber) {
        final int size = DungeonFloor.DEFAULT_SIZE;
        Room[][] grid = new Room[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = new Room(r, c, RoomType.EMPTY);
            }
        }

        // Entrada en (0, 0) y Jefe en (4, 4)
        grid[0][0] = new Room(0, 0, RoomType.ENTRANCE);
        grid[0][0].markCleared();

        Room bossRoom = new Room(size - 1, size - 1, RoomType.BOSS);
        bossRoom.setEnemy(new Boss(floorNumber));
        grid[size - 1][size - 1] = bossRoom;

        // Santuario garantizado en (2, 2) y Mercader Ambulante en (1, 3)
        grid[2][2] = new Room(2, 2, RoomType.SHRINE);
        grid[1][3] = new Room(1, 3, RoomType.SHOP);

        // Distribuir eventos en las celdas restantes
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if ((r == 0 && c == 0) || (r == size - 1 && c == size - 1)
                        || (r == 2 && c == 2) || (r == 1 && c == 3)) {
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
}

package rpg.engine;

import java.util.List;
import java.util.Objects;
import rpg.exception.GameException;
import rpg.exception.InvalidActionException;
import rpg.exception.InventoryFullException;
import rpg.exception.ItemNotFoundException;
import rpg.exception.PersistenceException;
import rpg.model.combat.Combatant;
import rpg.model.combat.SkillResult;
import rpg.model.dungeon.DungeonFloor;
import rpg.model.dungeon.GameState;
import rpg.model.dungeon.Room;
import rpg.model.entity.Enemy;
import rpg.model.entity.Mage;
import rpg.model.entity.Player;
import rpg.model.entity.Rogue;
import rpg.model.entity.Warrior;
import rpg.model.enums.Direction;
import rpg.model.enums.EquipmentSlot;
import rpg.model.enums.ItemType;
import rpg.model.enums.RoomType;
import rpg.model.item.Equippable;
import rpg.model.item.Item;
import rpg.model.skill.Skill;
import rpg.persistence.GameRepository;
import rpg.service.CombatService;
import rpg.service.DungeonGenerator;
import rpg.service.ItemFactory;
import rpg.ui.ConsoleReader;
import rpg.ui.ConsoleView;

/**
 * Controlador central del flujo de ejecución del juego: orquesta el ciclo de vida de la aplicación,
 * navegación por la mazmorra, combate por turnos, administración de equipamiento e I/O.
 */
public final class GameEngine {
    private final ConsoleView view;
    private final ConsoleReader reader;
    private final CombatService combatService;
    private final DungeonGenerator dungeonGenerator;
    private final ItemFactory itemFactory;
    private final GameRepository repository;

    public GameEngine(ConsoleView view, ConsoleReader reader, CombatService combatService,
                      DungeonGenerator dungeonGenerator, ItemFactory itemFactory, GameRepository repository) {
        this.view = Objects.requireNonNull(view, "ConsoleView no puede ser nulo.");
        this.reader = Objects.requireNonNull(reader, "ConsoleReader no puede ser nulo.");
        this.combatService = Objects.requireNonNull(combatService, "CombatService no puede ser nulo.");
        this.dungeonGenerator = Objects.requireNonNull(dungeonGenerator, "DungeonGenerator no puede ser nulo.");
        this.itemFactory = Objects.requireNonNull(itemFactory, "ItemFactory no puede ser nulo.");
        this.repository = Objects.requireNonNull(repository, "GameRepository no puede ser nulo.");
    }

    public void run() {
        view.printTitleBanner();
        boolean running = true;

        while (running) {
            view.printMainMenu();
            int option = reader.readIntInRange("Selecciona una opción", 1, 3);
            switch (option) {
                case 1 -> startNewGame();
                case 2 -> loadSavedGame();
                case 3 -> {
                    if (reader.readYesNo("¿Estás seguro de que deseas salir del juego?")) {
                        running = false;
                        view.printSuccess("¡Gracias por jugar! Que la luz guíe tu camino.");
                    }
                }
            }
        }
    }

    private void startNewGame() {
        String heroName = reader.readNonBlankString("Introduce el nombre de tu héroe", 20);
        view.printHeroCreationClasses();
        int classChoice = reader.readIntInRange("Elige tu vocación", 1, 3);

        Player player = switch (classChoice) {
            case 1 -> new Warrior(heroName);
            case 2 -> new Mage(heroName);
            case 3 -> new Rogue(heroName);
            default -> throw new IllegalStateException("Opción inesperada: " + classChoice);
        };

        // Equipamiento y suministros iniciales
        try {
            player.getInventory().addItem(itemFactory.createHealthPotion(1));
            player.getInventory().addItem(itemFactory.createHealthPotion(1));
            if (player instanceof Mage) {
                player.getInventory().addItem(itemFactory.createManaPotion(1));
                player.getInventory().addItem(itemFactory.createManaPotion(1));
            }
        } catch (InventoryFullException ignored) {}

        DungeonFloor firstFloor = dungeonGenerator.generateFloor(1);
        GameState gameState = new GameState(player, firstFloor);

        view.printSuccess("¡La expedición de " + player.getName() + " comienza en las catacumbas!");
        reader.waitForEnter();
        runExplorationLoop(gameState);
    }

    private void loadSavedGame() {
        List<String> saveSlots = repository.listSaveSlots();
        if (saveSlots.isEmpty()) {
            view.printWarning("No se encontraron archivos de guardado en el directorio 'saves/'.");
            reader.waitForEnter();
            return;
        }

        System.out.println("\nPartidas disponibles para cargar:");
        for (int i = 0; i < saveSlots.size(); i++) {
            System.out.printf(" %d. %s%n", (i + 1), saveSlots.get(i));
        }

        int choice = reader.readIntInRange("Elige la partida a cargar (0 para cancelar)", 0, saveSlots.size());
        if (choice == 0) return;

        String selectedSlot = saveSlots.get(choice - 1);
        try {
            GameState loadedState = repository.load(selectedSlot);
            view.printSuccess("Partida cargada exitosamente: " + selectedSlot);
            reader.waitForEnter();
            runExplorationLoop(loadedState);
        } catch (PersistenceException e) {
            view.printError("Fallo al cargar la partida: " + e.getMessage());
            reader.waitForEnter();
        }
    }

    private void runExplorationLoop(GameState gameState) {
        boolean inGame = true;

        while (inGame && gameState.getPlayer().isAlive() && !gameState.isVictory()) {
            view.printExplorationScreen(gameState);
            int action = reader.readIntInRange("Acción", 1, 6);

            try {
                switch (action) {
                    case 1 -> handleMovement(gameState);
                    case 2 -> handleInventoryAndEquipment(gameState.getPlayer());
                    case 3 -> {
                        view.printCharacterSheet(gameState.getPlayer());
                        reader.waitForEnter();
                    }
                    case 4 -> handleSortInventory(gameState.getPlayer());
                    case 5 -> handleSaveGame(gameState);
                    case 6 -> {
                        if (reader.readYesNo("¿Deseas volver al menú principal? (Asegúrate de haber guardado)")) {
                            inGame = false;
                        }
                    }
                }
            } catch (GameException e) {
                view.printError(e.getMessage());
                reader.waitForEnter();
            }
        }

        if (!gameState.getPlayer().isAlive()) {
            view.printDefeatScreen(gameState);
            reader.waitForEnter();
        } else if (gameState.isVictory()) {
            view.printVictoryScreen(gameState);
            reader.waitForEnter();
        }
    }

    private void handleMovement(GameState gameState) throws GameException {
        System.out.println("\nElige dirección cardinal:");
        System.out.println(" 1. Norte (▲)");
        System.out.println(" 2. Sur   (▼)");
        System.out.println(" 3. Este  (►)");
        System.out.println(" 4. Oeste (◄)");

        int dirChoice = reader.readIntInRange("Dirección", 1, 4);
        Direction dir = switch (dirChoice) {
            case 1 -> Direction.NORTH;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.EAST;
            case 4 -> Direction.WEST;
            default -> throw new IllegalStateException();
        };

        Room room = gameState.getCurrentFloor().move(dir);
        resolveRoomEvent(gameState, room);
    }

    private void resolveRoomEvent(GameState gameState, Room room) {
        if (room.isCleared()) {
            view.printWarning("Esta sala ya ha sido explorada y pacificada.");
            reader.waitForEnter();
            return;
        }

        switch (room.getType()) {
            case COMBAT, BOSS -> {
                Enemy enemy = room.getEnemy();
                if (enemy != null && enemy.isAlive()) {
                    if (room.getType() == RoomType.BOSS) {
                        view.printWarning("¡ADVERTENCIA! Has entrado en los aposentos del " + enemy.getName() + ".");
                    } else {
                        view.printWarning("¡Una sombra se abalanza desde la oscuridad! Encuentro hostil: " + enemy.getName());
                    }
                    reader.waitForEnter();

                    runCombatLoop(gameState.getPlayer(), enemy, gameState.getCurrentFloor().getFloorNumber());

                    if (gameState.getPlayer().isAlive() && !enemy.isAlive()) {
                        room.markCleared();
                        if (enemy.isBoss()) {
                            int currentFloorNum = gameState.getCurrentFloor().getFloorNumber();
                            if (currentFloorNum >= gameState.getMaxFloors()) {
                                gameState.setVictory(true);
                            } else {
                                view.printSuccess("¡El guardián del piso ha caído! Se abre el portal hacia el Piso " + (currentFloorNum + 1) + ".");
                                reader.waitForEnter();
                                DungeonFloor nextFloor = dungeonGenerator.generateFloor(currentFloorNum + 1);
                                gameState.advanceFloor(nextFloor);
                            }
                        }
                    }
                }
            }
            case TREASURE -> {
                Item loot = room.getLoot();
                if (loot != null) {
                    System.out.println("\n¡Has descubierto un cofre antiguo adornado con runas!");
                    try {
                        gameState.getPlayer().getInventory().addItem(loot);
                        view.printSuccess("¡Obtienes: " + loot.getName() + " (" + loot.getSummaryStats() + ")!");
                        room.markCleared();
                    } catch (InventoryFullException e) {
                        view.printError("¡Tu mochila está llena! No puedes recoger " + loot.getName() + ". Haz espacio y regresa.");
                    }
                } else {
                    room.markCleared();
                }
                reader.waitForEnter();
            }
            case SHRINE -> {
                System.out.println("\nEncuentras un santuario sagrado consagrado a los antiguos dioses.");
                int heal = gameState.getPlayer().heal((int) (gameState.getPlayer().getMaxHealth() * 0.40));
                gameState.getPlayer().clearNegativeEffects();
                view.printSuccess("La luz divina restaura +" + heal + " PV y purifica todos los estados negativos.");
                room.markCleared();
                reader.waitForEnter();
            }
            case SHOP -> {
                handleMerchantShop(gameState.getPlayer(), gameState.getCurrentFloor().getFloorNumber());
            }
            case EMPTY, ENTRANCE -> {
                System.out.println("\nUn pasillo silencioso y frío. No hay peligros inmediatos.");
                room.markCleared();
                reader.waitForEnter();
            }
        }
    }

    private void runCombatLoop(Player player, Enemy enemy, int floorNumber) {
        int round = 1;

        while (player.isAlive() && enemy.isAlive()) {
            view.printCombatHeader(round, player, enemy);

            // Turno del Jugador: efectos activos
            List<String> playerStartLogs = player.processTurnStartEffects();
            for (String log : playerStartLogs) {
                System.out.println(log);
            }
            if (!player.isAlive()) break;

            if (player.isStunned()) {
                view.printWarning("¡Estás aturdido! No puedes moverte este turno.");
            } else {
                boolean turnActionFinished = false;
                while (!turnActionFinished && player.isAlive() && enemy.isAlive()) {
                    System.out.println("\nAcciones de Combate:");
                    System.out.println(" 1. Ejecutar Habilidad");
                    System.out.println(" 2. Usar Objeto Consumible");
                    System.out.println(" 3. Defenderse (Postura de Guardia)");
                    System.out.println(" 4. Intentar Huir");

                    int combatChoice = reader.readIntInRange("Acción de turno", 1, 4);
                    try {
                        if (combatChoice == 1) {
                            List<Skill> skills = player.getSkills();
                            view.printSkillsMenu(skills);
                            int skillIdx = reader.readIntInRange("Elige habilidad (0 para volver)", 0, skills.size());
                            if (skillIdx == 0) continue;

                            Skill chosenSkill = skills.get(skillIdx - 1);
                            Combatant target = chosenSkill.isSelfTargeted() ? player : enemy;

                            SkillResult result = combatService.executeSkillAction(player, chosenSkill, target);
                            view.printSkillResult(result);
                            turnActionFinished = true;
                        } else if (combatChoice == 2) {
                            List<Item> items = player.getInventory().getItems();
                            List<Item> consumables = items.stream()
                                    .filter(i -> i.getItemType() == ItemType.CONSUMABLE)
                                    .toList();

                            if (consumables.isEmpty()) {
                                throw new InvalidActionException("No tienes ningún objeto consumible en la mochila.");
                            }

                            System.out.println("\nConsumibles disponibles:");
                            for (int i = 0; i < consumables.size(); i++) {
                                System.out.printf(" %d. %s%n", (i + 1), consumables.get(i));
                            }

                            int itemChoice = reader.readIntInRange("Selecciona consumible (0 para volver)", 0, consumables.size());
                            if (itemChoice == 0) continue;

                            Item chosenItem = consumables.get(itemChoice - 1);
                            String useMsg = player.useInventoryItem(chosenItem.getId());
                            view.printSuccess(useMsg);
                            turnActionFinished = true;
                        } else if (combatChoice == 3) {
                            int guardDef = 8 + (player.getLevel() * 3);
                            player.applyStatusEffect(new rpg.model.combat.ActiveStatusEffect(
                                    rpg.model.enums.StatusEffectType.FORTIFY, 1, guardDef));
                            view.printSuccess("¡Adoptas una guardia defensiva férrea! (+ " + guardDef + " DEF durante esta ronda).");
                            turnActionFinished = true;
                        } else if (combatChoice == 4) {
                            if (enemy.isBoss()) {
                                throw new InvalidActionException("¡Barreras místicas sellan las salidas! No puedes huir de un Jefe.");
                            }
                            double fleeRoll = java.util.concurrent.ThreadLocalRandom.current().nextDouble();
                            if (fleeRoll < 0.65) {
                                view.printSuccess("¡Aprovechas una distracción del rival y logras escapar del combate!");
                                return; // Sale del combate sin matar al enemigo
                            } else {
                                view.printWarning("¡El enemigo se interpuso en tu retirada y bloqueó tu escape! Pierdes el turno.");
                                turnActionFinished = true;
                            }
                        }
                    } catch (GameException ex) {
                        view.printError(ex.getMessage());
                        // ¡El turno no se pierde! El jugador puede rectificar su selección
                    }
                }
            }

            if (!enemy.isAlive()) break;

            // Turno del Enemigo
            System.out.println();
            List<String> enemyStartLogs = enemy.processTurnStartEffects();
            for (String log : enemyStartLogs) {
                System.out.println(log);
            }
            if (!enemy.isAlive()) break;

            if (enemy.isStunned()) {
                view.printWarning(enemy.getName() + " está aturdido y pierde su acción.");
            } else {
                Skill enemySkill = enemy.chooseSkill(player);
                Combatant enemyTarget = enemySkill.isSelfTargeted() ? enemy : player;
                try {
                    SkillResult enemyResult = combatService.executeSkillAction(enemy, enemySkill, enemyTarget);
                    view.printSkillResult(enemyResult);
                } catch (GameException e) {
                    view.printError("Fallo en acción enemiga: " + e.getMessage());
                }
            }

            round++;
            reader.waitForEnter();
        }

        if (player.isAlive() && !enemy.isAlive()) {
            Item lootDrop = itemFactory.rollLootDrop(floorNumber, enemy.isBoss());
            String victoryReport = combatService.grantVictoryRewards(player, enemy, lootDrop);
            view.printSuccess(victoryReport);
            reader.waitForEnter();
        }
    }

    private void handleInventoryAndEquipment(Player player) {
        boolean managing = true;
        while (managing) {
            view.printEquipment(player.getEquippedItems());
            view.printInventory(player.getInventory());

            System.out.println(rpg.ui.AnsiColor.CYAN.boldWrap("\nOpciones de Mochila y Equipo:"));
            System.out.println(" 1. Usar Objeto Consumible");
            System.out.println(" 2. Equipar Objeto");
            System.out.println(" 3. Desequipar Ranura");
            System.out.println(" 4. Descartar / Tirar Ítem de la Mochila");
            System.out.println(" 5. Volver a Exploración");

            int choice = reader.readIntInRange("Opción", 1, 5);
            try {
                switch (choice) {
                    case 1 -> {
                        if (player.getInventory().isEmpty()) {
                            view.printWarning("Tu mochila está vacía.");
                            continue;
                        }
                        int idx = reader.readIntInRange("Introduce el número de ítem a usar", 1, player.getInventory().size());
                        Item item = player.getInventory().getByIndex(idx);
                        String msg = player.useInventoryItem(item.getId());
                        view.printSuccess(msg);
                    }
                    case 2 -> {
                        if (player.getInventory().isEmpty()) {
                            view.printWarning("Tu mochila está vacía.");
                            continue;
                        }
                        int idx = reader.readIntInRange("Introduce el número de ítem a equipar", 1, player.getInventory().size());
                        Item item = player.getInventory().getByIndex(idx);
                        Equippable previous = player.equipItem(item.getId());
                        view.printSuccess("¡Equipaste " + item.getName() + "!");
                        if (previous instanceof Item prevItem) {
                            System.out.println("  (El equipo previo '" + prevItem.getName() + "' fue devuelto a la mochila)");
                        }
                    }
                    case 3 -> {
                        System.out.println("\nSelecciona ranura a desequipar:");
                        EquipmentSlot[] slots = EquipmentSlot.values();
                        for (int i = 0; i < slots.length; i++) {
                            System.out.printf(" %d. %s%n", (i + 1), slots[i].getDisplayName());
                        }
                        int slotIdx = reader.readIntInRange("Ranura (0 para cancelar)", 0, slots.length);
                        if (slotIdx == 0) continue;
                        EquipmentSlot chosenSlot = slots[slotIdx - 1];
                        Equippable unequipped = player.unequipItem(chosenSlot);
                        if (unequipped instanceof Item eqItem) {
                            view.printSuccess("Desequipaste " + eqItem.getName() + " y volvió a tu mochila.");
                        }
                    }
                    case 4 -> {
                        if (player.getInventory().isEmpty()) {
                            view.printWarning("Tu mochila está vacía.");
                            continue;
                        }
                        int idx = reader.readIntInRange("Introduce el número de ítem a descartar (0 para cancelar)", 0, player.getInventory().size());
                        if (idx == 0) continue;
                        Item itemToDiscard = player.getInventory().getByIndex(idx);
                        if (reader.readYesNo("¿Confirmas tirar " + itemToDiscard.getName() + " a la basura?")) {
                            player.getInventory().removeByIndex(idx);
                            view.printSuccess("Desechaste " + itemToDiscard.getName() + " de tu mochila.");
                        }
                    }
                    case 5 -> managing = false;
                }
            } catch (GameException e) {
                view.printError(e.getMessage());
            }
        }
    }

    private void handleSortInventory(Player player) {
        System.out.println("\nSelecciona el criterio para ordenar tu mochila:");
        System.out.println(" 1. Orden Natural (Rareza > Tipo > Nombre)");
        System.out.println(" 2. Por Valor en Oro (Más costosos primero)");
        System.out.println(" 3. Alfabético por Nombre (A-Z)");

        int sortChoice = reader.readIntInRange("Criterio", 1, 3);
        switch (sortChoice) {
            case 1 -> {
                player.getInventory().sortNatural();
                view.printSuccess("Mochila ordenada según orden natural (Comparable).");
            }
            case 2 -> {
                player.getInventory().sortWith(java.util.Comparator.comparingInt(Item::getGoldValue).reversed());
                view.printSuccess("Mochila ordenada por valor en oro.");
            }
            case 3 -> {
                player.getInventory().sortWith(java.util.Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
                view.printSuccess("Mochila ordenada alfabéticamente por nombre.");
            }
        }
        reader.waitForEnter();
    }

    private void handleMerchantShop(Player player, int floor) {
        System.out.println(rpg.ui.AnsiColor.BRIGHT_BLUE.boldWrap(
                "\n==================== [ PUESTO DEL MERCADER AMBULANTE ] ===================="));
        System.out.println(" \"¡Saludos valiente explorador! Si tienes el oro suficiente, tengo equipo y elixires selectos...\"");

        boolean shopping = true;
        while (shopping) {
            System.out.printf("%n Tu bolsa de monedas: %s💰 | Capacidad mochila: %d/%d ranuras%n",
                    rpg.ui.AnsiColor.BRIGHT_YELLOW.boldWrap(String.valueOf(player.getGold())),
                    player.getInventory().size(),
                    player.getInventory().getMaxCapacity());

            int potHpCost = 25 * floor;
            int potMpCost = 25 * floor;
            int elixirCost = 60 * floor;
            int mysteryGearCost = 80 * floor;

            System.out.println(" 1. Comprar Poción de Salud (" + potHpCost + "💰)");
            System.out.println(" 2. Comprar Poción de Maná (" + potMpCost + "💰)");
            System.out.println(" 3. Comprar Elixir de Purificación (" + elixirCost + "💰)");
            System.out.println(" 4. Comprar Equipo Forjado Misterioso (" + mysteryGearCost + "💰)");
            System.out.println(" 5. Vender un Ítem de tu Mochila");
            System.out.println(" 6. Salir de la Tienda");

            int shopChoice = reader.readIntInRange("Acción", 1, 6);
            try {
                switch (shopChoice) {
                    case 1 -> buyItemFromShop(player, itemFactory.createHealthPotion(floor), potHpCost);
                    case 2 -> buyItemFromShop(player, itemFactory.createManaPotion(floor), potMpCost);
                    case 3 -> buyItemFromShop(player, itemFactory.createElixir(floor), elixirCost);
                    case 4 -> buyItemFromShop(player, itemFactory.rollLootDrop(floor, false), mysteryGearCost);
                    case 5 -> sellItemToShop(player);
                    case 6 -> {
                        shopping = false;
                        System.out.println(" \"¡Vuelve cuando reúnas más botín y oro, aventurero!\"");
                    }
                }
            } catch (GameException e) {
                view.printError(e.getMessage());
            }
        }
        reader.waitForEnter();
    }

    private void buyItemFromShop(Player player, Item item, int cost) throws GameException {
        if (player.getInventory().isFull()) {
            throw new InventoryFullException(player.getInventory().getMaxCapacity());
        }
        player.spendGold(cost);
        player.getInventory().addItem(item);
        view.printSuccess("¡Compraste " + item.getName() + " por " + cost + "💰!");
    }

    private void sellItemToShop(Player player) throws GameException {
        if (player.getInventory().isEmpty()) {
            view.printWarning("Tu mochila está vacía, no tienes nada para vender.");
            return;
        }
        view.printInventory(player.getInventory());
        int idx = reader.readIntInRange("Selecciona el número de ítem a vender (0 para cancelar)", 0, player.getInventory().size());
        if (idx == 0) return;

        Item item = player.getInventory().getByIndex(idx);
        int sellPrice = Math.max(5, item.getGoldValue());
        if (reader.readYesNo("¿Vender " + item.getName() + " por " + sellPrice + "💰 de oro?")) {
            player.getInventory().removeItem(item);
            player.addGold(sellPrice);
            view.printSuccess("¡Vendiste " + item.getName() + " y recibiste +" + sellPrice + "💰 de oro!");
        }
    }

    private void handleSaveGame(GameState gameState) {
        String slotName = reader.readNonBlankString("Introduce el nombre para el archivo de guardado (ej. heroe_partida)", 25);
        try {
            repository.save(gameState, slotName);
            view.printSuccess("Partida guardada exitosamente en saves/" + slotName + ".dat");
        } catch (PersistenceException e) {
            view.printError("No se pudo guardar la partida: " + e.getMessage());
        }
        reader.waitForEnter();
    }
}

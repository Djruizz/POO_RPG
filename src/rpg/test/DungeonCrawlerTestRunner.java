package rpg.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import rpg.exception.GameException;
import rpg.exception.InsufficientManaException;
import rpg.exception.InvalidActionException;
import rpg.exception.InventoryFullException;
import rpg.exception.ItemNotFoundException;
import rpg.model.combat.SkillResult;
import rpg.model.dungeon.DungeonFloor;
import rpg.model.dungeon.GameState;
import rpg.model.dungeon.Room;
import rpg.model.entity.Boss;
import rpg.model.entity.Goblin;
import rpg.model.entity.Mage;
import rpg.model.entity.Rogue;
import rpg.model.entity.Skeleton;
import rpg.model.entity.Warrior;
import rpg.model.enums.Direction;
import rpg.model.enums.EquipmentSlot;
import rpg.model.enums.ItemType;
import rpg.model.enums.Rarity;
import rpg.model.enums.RoomType;
import rpg.model.item.ConsumableItem;
import rpg.model.item.EquipmentItem;
import rpg.model.item.Inventory;
import rpg.model.item.Item;
import rpg.model.lore.FloorTheme;
import rpg.model.lore.LoreFragment;
import rpg.model.skill.BasicAttack;
import rpg.model.skill.Fireball;
import rpg.model.skill.Heal;
import rpg.model.skill.HeavyStrike;
import rpg.persistence.SerializableGameRepository;
import rpg.service.CombatService;
import rpg.service.DungeonGenerator;
import rpg.service.ItemFactory;

/**
 * Suite de verificación automatizada integral para validar principios de POO,
 * invariantes de encapsulamiento, polimorfismo, excepciones y persistencia.
 */
public final class DungeonCrawlerTestRunner {

    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SUITE DE VERIFICACIÓN AUTOMATIZADA (POO / SOLID)...");

        testEncapsulationAndHealthInvariants();
        testUnmodifiableCollections();
        testInventoryAndComparableOrder();
        testCustomExceptions();
        testPolymorphicCharactersAndSkills();
        testDungeonGenerationAndNavigation();
        testPersistenceSerialization();
        testLoreThemesAndCodex();

        System.out.println(">>> ¡TODAS LAS 8 PRUEBAS DE ARQUITECTURA POO PASARON EXITOSAMENTE (100%)!");
    }

    private static void testEncapsulationAndHealthInvariants() {
        System.out.print(" [1/8] Probando encapsulamiento e invariantes de salud... ");
        Warrior warrior = new Warrior("Conan");
        int max = warrior.getMaxHealth();

        // No puede superar el máximo de salud
        warrior.heal(100);
        assert warrior.getCurrentHealth() == max : "La salud superó la salud máxima.";

        // Daño que supera la salud deja la salud en 0 (no números negativos)
        warrior.takeDamage(1000);
        assert warrior.getCurrentHealth() == 0 : "La salud cayó por debajo de 0.";
        assert !warrior.isAlive() : "El personaje muerto aún figura vivo.";

        System.out.println("OK");
    }

    private static void testUnmodifiableCollections() {
        System.out.print(" [2/8] Probando inmutabilidad de colecciones expuestas... ");
        Mage mage = new Mage("Gandalf");

        // Intento de mutar lista de habilidades debe lanzar UnsupportedOperationException
        try {
            mage.getSkills().add(new BasicAttack());
            throw new AssertionError("Fallo: getSkills() permitió mutación externa.");
        } catch (UnsupportedOperationException expected) {
            // Correcto
        }

        // Intento de mutar equipamiento directamente debe lanzar UnsupportedOperationException
        try {
            mage.getEquippedItems().clear();
            throw new AssertionError("Fallo: getEquippedItems() permitió mutación externa.");
        } catch (UnsupportedOperationException expected) {
            // Correcto
        }

        // Intento de mutar inventario expuesto debe lanzar UnsupportedOperationException
        try {
            mage.getInventory().getItems().clear();
            throw new AssertionError("Fallo: inventory.getItems() permitió mutación externa.");
        } catch (UnsupportedOperationException expected) {
            // Correcto
        }

        System.out.println("OK");
    }

    private static void testInventoryAndComparableOrder() {
        System.out.print(" [3/8] Probando ordenamiento natural Comparable<Item>... ");
        Inventory inv = new Inventory(5);

        Item commonItem = new ConsumableItem("c1", "Poción Simple", "Desc", Rarity.COMMON, 10, 20, 0, false);
        Item rareItem = new ConsumableItem("r1", "Elixir Raro", "Desc", Rarity.RARE, 50, 50, 0, false);
        Item legendaryItem = new EquipmentItem("l1", "Espada Legendaria", "Desc", Rarity.LEGENDARY, ItemType.WEAPON, 500, EquipmentSlot.WEAPON, 50, 0, 0);

        try {
            inv.addItem(commonItem);
            inv.addItem(legendaryItem);
            inv.addItem(rareItem);
        } catch (InventoryFullException e) {
            throw new AssertionError(e);
        }

        inv.sortNatural();
        List<Item> sorted = inv.getItems();

        // El primer ítem debe ser LEGENDARY, el segundo RARE, el tercero COMMON
        assert sorted.get(0).getRarity() == Rarity.LEGENDARY : "El ítem legendario no está en primer lugar.";
        assert sorted.get(1).getRarity() == Rarity.RARE : "El ítem raro no está en segundo lugar.";
        assert sorted.get(2).getRarity() == Rarity.COMMON : "El ítem común no está en tercer lugar.";

        // Prueba de ordenamiento por valor en oro con sortWith
        inv.sortWith(java.util.Comparator.comparingInt(Item::getGoldValue).reversed());
        assert inv.getItems().get(0).getGoldValue() >= inv.getItems().get(1).getGoldValue();

        // Prueba de remoción por índice 1-based (removeByIndex)
        try {
            Item removed = inv.removeByIndex(1);
            assert removed != null;
            assert inv.size() == 2;
        } catch (ItemNotFoundException e) {
            throw new AssertionError(e);
        }

        System.out.println("OK");
    }

    private static void testCustomExceptions() {
        System.out.print(" [4/8] Probando jerarquía de excepciones personalizadas de negocio... ");

        // 1. InventoryFullException
        Inventory smallInv = new Inventory(1);
        try {
            smallInv.addItem(new ConsumableItem("i1", "Poción", "Desc", Rarity.COMMON, 5, 10, 0, false));
            smallInv.addItem(new ConsumableItem("i2", "Otra Poción", "Desc", Rarity.COMMON, 5, 10, 0, false));
            throw new AssertionError("Fallo: Se debió lanzar InventoryFullException.");
        } catch (InventoryFullException expected) {
            assert expected.getMaxCapacity() == 1;
        }

        // 2. ItemNotFoundException
        try {
            smallInv.findById("no_existe");
            throw new AssertionError("Fallo: Se debió lanzar ItemNotFoundException.");
        } catch (ItemNotFoundException expected) {
            // Correcto
        }

        // 3. InsufficientManaException
        Mage mage = new Mage("Merlín");
        try {
            // Mago inicia con 80 mana, intentamos consumir 100
            mage.consumeMana(100);
            throw new AssertionError("Fallo: Se debió lanzar InsufficientManaException.");
        } catch (InsufficientManaException expected) {
            assert expected.getRequiredMana() == 100;
            assert expected.getCurrentMana() == 80;
        }

        // 4. InvalidActionException
        Warrior warrior = new Warrior("Ares");
        try {
            // Guerrero inicia con 20 furia, HeavyStrike consume 35
            warrior.consumeFury(35);
            throw new AssertionError("Fallo: Se debió lanzar InvalidActionException.");
        } catch (InvalidActionException expected) {
            // Correcto
        }

        System.out.println("OK");
    }

    private static void testPolymorphicCharactersAndSkills() {
        System.out.print(" [5/8] Probando combate polimórfico, subclases y estados... ");
        CombatService combatService = new CombatService();

        Warrior warrior = new Warrior("Thor");
        Goblin goblin = new Goblin(1);
        Skeleton skeleton = new Skeleton(1);
        Boss boss = new Boss(1);

        try {
            // Guerrero ejecuta ataque básico sobre goblin
            int initialGoblinHp = goblin.getCurrentHealth();
            SkillResult r1 = combatService.executeSkillAction(warrior, new BasicAttack(), goblin);
            assert goblin.getCurrentHealth() < initialGoblinHp : "El goblin no recibió daño.";
            assert warrior.getCurrentFury() > 20 : "El guerrero no generó furia al golpear.";

            // Mago ejecuta bola de fuego sobre skeleton
            Mage mage = new Mage("Voldemort");
            int initialSkeletonHp = skeleton.getCurrentHealth();
            combatService.executeSkillAction(mage, new Fireball(25), skeleton);
            assert skeleton.getCurrentHealth() < initialSkeletonHp : "El esqueleto no recibió daño mágico.";
            assert mage.getCurrentMana() == 55 : "El maná del mago no se redujo correctamente.";

            // Pícaro ejecuta backstab sobre boss
            Rogue rogue = new Rogue("Altair");
            int initialBossHp = boss.getCurrentHealth();
            combatService.executeSkillAction(rogue, rogue.getSkills().get(1), boss); // Backstab
            assert boss.getCurrentHealth() < initialBossHp : "El boss no recibió daño.";
            assert rogue.getCurrentEnergy() == 65 : "La energía del pícaro no se redujo correctamente.";

            // Curación polimórfica Heal
            mage.takeDamage(30);
            int damagedHp = mage.getCurrentHealth();
            combatService.executeSkillAction(mage, new Heal(20), mage);
            assert mage.getCurrentHealth() > damagedHp : "El hechizo Heal no restauró salud.";

            // Guardia de Hierro (FORTIFY)
            int baseDef = warrior.getBaseDefense();
            combatService.executeSkillAction(warrior, new rpg.model.skill.IronGuard(0), warrior);
            assert warrior.getEffectiveDefense() > baseDef : "Fortify no incrementó la defensa efectiva.";

            // Daga Tóxica (POISON)
            combatService.executeSkillAction(rogue, new rpg.model.skill.PoisonStrike(0), skeleton);
            boolean poisoned = skeleton.getActiveEffects().stream().anyMatch(e -> e.getType() == rpg.model.enums.StatusEffectType.POISON);
            if (poisoned) {
                int hpBeforePoison = skeleton.getCurrentHealth();
                skeleton.processTurnStartEffects();
                assert skeleton.getCurrentHealth() < hpBeforePoison : "El veneno no infligió daño al inicio de turno.";
            }

            // Yelmo y ranura HELMET
            Item helmet = new ItemFactory().createHelmet(1, false);
            warrior.getInventory().addItem(helmet);
            warrior.equipItem(helmet.getId());
            assert warrior.getEquippedItems().containsKey(EquipmentSlot.HELMET) : "El yelmo no quedó equipado en su ranura.";

            // Economía y spendGold
            int prevGold = warrior.getGold();
            warrior.spendGold(15);
            assert warrior.getGold() == prevGold - 15 : "spendGold no decrementó el saldo correctamente.";

        } catch (GameException e) {
            throw new AssertionError("Error inesperado en combate polimórfico", e);
        }

        System.out.println("OK");
    }

    private static void testDungeonGenerationAndNavigation() {
        System.out.print(" [6/8] Probando generación procedural de mazmorra y navegación... ");
        ItemFactory itemFactory = new ItemFactory();
        DungeonGenerator generator = new DungeonGenerator(itemFactory);
        DungeonFloor floor = generator.generateFloor(1);

        assert floor.getPlayerRow() == 0 && floor.getPlayerCol() == 0 : "El jugador no inició en (0,0).";
        assert floor.getCurrentRoom().isVisited() : "La sala inicial no está marcada como visitada.";

        // No puede moverse al Norte o al Oeste desde (0,0)
        assert !floor.canMove(Direction.NORTH) : "Permitió moverse al norte fuera del límite.";
        assert !floor.canMove(Direction.WEST) : "Permitió moverse al oeste fuera del límite.";
        assert floor.canMove(Direction.SOUTH) : "No permitió moverse al sur dentro del límite.";
        assert floor.canMove(Direction.EAST) : "No permitió moverse al este dentro del límite.";

        try {
            floor.move(Direction.SOUTH);
            assert floor.getPlayerRow() == 1 && floor.getPlayerCol() == 0;
            assert floor.getCurrentRoom().isVisited();
        } catch (InvalidActionException e) {
            throw new AssertionError("Fallo en movimiento válido", e);
        }

        System.out.println("OK");
    }

    private static void testPersistenceSerialization() {
        System.out.print(" [7/8] Probando persistencia y serialización completa de GameState... ");
        ItemFactory itemFactory = new ItemFactory();
        DungeonGenerator generator = new DungeonGenerator(itemFactory);
        Warrior warrior = new Warrior("Aquiles");
        warrior.addGold(150);

        // Añadir equipo e ítems
        Item sword = itemFactory.createWeapon(2, false);
        try {
            warrior.getInventory().addItem(sword);
            warrior.equipItem(sword.getId());
            warrior.getInventory().addItem(itemFactory.createHealthPotion(1));
        } catch (GameException e) {
            throw new AssertionError(e);
        }

        DungeonFloor floor = generator.generateFloor(2);
        GameState originalState = new GameState(warrior, floor);

        Path testSaveDir = Paths.get("saves_test");
        SerializableGameRepository repo = new SerializableGameRepository(testSaveDir);

        try {
            repo.save(originalState, "test_hero");
            GameState loadedState = repo.load("test_hero");

            assert loadedState.getPlayer().getName().equals("Aquiles") : "Nombre no coincide.";
            assert loadedState.getPlayer() instanceof Warrior : "La clase no preservó el tipo concreto Warrior.";
            assert loadedState.getPlayer().getGold() == 180 : "El oro no coincide (30 inicial + 150).";
            assert loadedState.getCurrentFloor().getFloorNumber() == 2 : "El piso no coincide.";
            assert !loadedState.getPlayer().getInventory().isEmpty() : "El inventario cargado está vacío.";
            assert loadedState.getPlayer().getEquippedItems().containsKey(EquipmentSlot.WEAPON) : "El arma no quedó equipada.";

            // Limpieza
            Files.deleteIfExists(testSaveDir.resolve("test_hero.dat"));
            Files.deleteIfExists(testSaveDir);
        } catch (Exception e) {
            throw new AssertionError("Fallo al probar la serialización", e);
        }

        System.out.println("OK");
    }

    private static void testLoreThemesAndCodex() {
        System.out.print(" [8/8] Probando temas de piso, jefes únicos y códice de historia... ");
        DungeonGenerator generator = new DungeonGenerator(new ItemFactory());

        // Cada piso tiene su propio tema y su propio jefe con diálogos
        for (FloorTheme theme : FloorTheme.values()) {
            int floorNumber = theme.getFloorNumber();
            assert FloorTheme.forFloor(floorNumber) == theme : "forFloor no devolvió el tema del piso.";
            Boss boss = new Boss(floorNumber);
            assert boss.getName().equals(theme.getBossName()) : "El jefe no tomó el nombre de su tema.";
            assert boss.getIntroLine() != null && boss.getDeathLine() != null : "El jefe no tiene diálogos.";
        }
        assert !new Boss(1).getName().equals(new Boss(2).getName()) : "Los jefes de pisos distintos se llaman igual.";
        assert FloorTheme.forFloor(99) == FloorTheme.ABYSS_HEART : "Los pisos profundos no reutilizan el último tema.";

        // Cada piso genera exactamente una sala de inscripción por fragmento de su tema, sin repetir
        java.util.Set<LoreFragment> allPlaced = java.util.EnumSet.noneOf(LoreFragment.class);
        for (FloorTheme theme : FloorTheme.values()) {
            DungeonFloor floor = generator.generateFloor(theme.getFloorNumber());
            java.util.Set<LoreFragment> placed = java.util.EnumSet.noneOf(LoreFragment.class);
            for (int r = 0; r < floor.getRows(); r++) {
                for (int c = 0; c < floor.getCols(); c++) {
                    Room room = floor.getRoom(r, c);
                    if (room.getType() == RoomType.INSCRIPTION) {
                        assert room.getLoreFragment() != null : "Sala de inscripción sin fragmento.";
                        assert placed.add(room.getLoreFragment()) : "Fragmento repetido en el mismo piso.";
                    } else {
                        assert room.getLoreFragment() == null : "Sala normal con fragmento asignado.";
                    }
                }
            }
            assert placed.equals(java.util.EnumSet.copyOf(LoreFragment.forTheme(theme)))
                    : "El piso no contiene los fragmentos de su tema.";
            assert floor.getRoom(0, 0).getType() == RoomType.ENTRANCE : "Una inscripción pisó la entrada.";
            assert floor.getRoom(4, 4).getType() == RoomType.BOSS : "Una inscripción pisó la sala del jefe.";
            assert floor.getRoom(2, 2).getType() == RoomType.SHRINE : "Una inscripción pisó el santuario.";
            assert floor.getRoom(1, 3).getType() == RoomType.SHOP : "Una inscripción pisó al mercader.";
            allPlaced.addAll(placed);
        }
        assert allPlaced.size() == LoreFragment.totalCount() : "No todos los fragmentos son alcanzables.";

        // El códice no admite duplicados, es de solo lectura y sobrevive al guardado
        GameState state = new GameState(new Rogue("Ezio"), generator.generateFloor(1));
        assert state.discoverLore(LoreFragment.VIGIL_OATH) : "El primer hallazgo no se registró.";
        assert !state.discoverLore(LoreFragment.VIGIL_OATH) : "El códice aceptó un fragmento duplicado.";
        state.discoverLore(LoreFragment.FORGE_LOG);
        try {
            state.getDiscoveredLore().add(LoreFragment.ANVIL_MARK);
            throw new AssertionError("Fallo: getDiscoveredLore() permitió mutación externa.");
        } catch (UnsupportedOperationException expected) {
            // Correcto
        }

        Path testSaveDir = Paths.get("saves_test");
        SerializableGameRepository repo = new SerializableGameRepository(testSaveDir);
        try {
            repo.save(state, "test_codex");
            GameState loaded = repo.load("test_codex");
            assert loaded.getDiscoveredLore().equals(state.getDiscoveredLore()) : "El códice no se preservó al guardar.";
            assert loaded.getCurrentFloor().getRoom(4, 4).getEnemy().getName()
                    .equals(FloorTheme.CATACOMBS.getBossName()) : "El jefe no se preservó al guardar.";
            Files.deleteIfExists(testSaveDir.resolve("test_codex.dat"));
            Files.deleteIfExists(testSaveDir);
        } catch (Exception e) {
            throw new AssertionError("Fallo al probar la persistencia del códice", e);
        }

        System.out.println("OK");
    }
}

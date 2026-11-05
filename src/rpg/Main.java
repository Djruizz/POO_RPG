package rpg;

import rpg.engine.GameEngine;
import rpg.persistence.GameRepository;
import rpg.persistence.SerializableGameRepository;
import rpg.service.CombatService;
import rpg.service.DungeonGenerator;
import rpg.service.ItemFactory;
import rpg.ui.ConsoleReader;
import rpg.ui.ConsoleView;

/**
 * Punto de entrada principal (Bootstrap y Composition Root) de la aplicación.
 * Realiza la inyección de dependencias manual y arranca el GameEngine.
 */
public final class Main {

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        ConsoleReader reader = new ConsoleReader(System.in);
        ItemFactory itemFactory = new ItemFactory();
        CombatService combatService = new CombatService();
        DungeonGenerator dungeonGenerator = new DungeonGenerator(itemFactory);
        GameRepository repository = new SerializableGameRepository();

        GameEngine engine = new GameEngine(
                view,
                reader,
                combatService,
                dungeonGenerator,
                itemFactory,
                repository
        );

        try {
            engine.run();
        } catch (Exception ex) {
            System.err.println("Error crítico inesperado en el ciclo del juego: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

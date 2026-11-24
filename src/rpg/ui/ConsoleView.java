package rpg.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rpg.model.combat.ActiveStatusEffect;
import rpg.model.combat.Combatant;
import rpg.model.combat.SkillResult;
import rpg.model.dungeon.DungeonFloor;
import rpg.model.dungeon.GameState;
import rpg.model.dungeon.Room;
import rpg.model.entity.Player;
import rpg.model.enums.EquipmentSlot;
import rpg.model.item.Equippable;
import rpg.model.item.Inventory;
import rpg.model.item.Item;
import rpg.model.lore.FloorTheme;
import rpg.model.lore.LoreFragment;
import rpg.model.skill.Skill;

/**
 * Capa de presentación responsable de renderizar tableros, interfaces de combate,
 * inventarios, barras de estado y mapas en la consola con códigos ANSI.
 */
public final class ConsoleView {
    private static final int NARRATION_WIDTH = 76;

    public void printTitleBanner() {
        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap("""
                ================================================================================
                    DUNGEON CRAWLER POO: LAS PROFUNDIDADES DEL ABISMO
                ================================================================================
                """));
    }

    public void printMainMenu() {
        System.out.println(AnsiColor.CYAN.boldWrap("\n--- MENÚ PRINCIPAL ---"));
        System.out.println(" 1. Iniciar Nueva Expedición");
        System.out.println(" 2. Cargar Partida Guardada");
        System.out.println(" 3. Salir al Escritorio");
    }

    public void printHeroCreationClasses() {
        System.out.println(AnsiColor.CYAN.boldWrap("\n--- SELECCIONA LA CLASE DE TU HÉROE ---"));
        System.out.println(" 1. " + AnsiColor.BRIGHT_RED.boldWrap("Guerrero") + "     | PV: 135 | ATQ: 18 | DEF: 12 | Mecánica: Furia al golpear/recibir daño");
        System.out.println(" 2. " + AnsiColor.BRIGHT_BLUE.boldWrap("Mago Arcano") + "  | PV: 95  | ATQ: 22 | DEF: 6  | Mecánica: Maná regenerativo, Hechizos y Cura");
        System.out.println(" 3. " + AnsiColor.BRIGHT_GREEN.boldWrap("Pícaro") + "       | PV: 110 | ATQ: 20 | DEF: 8  | Mecánica: Alta tasa crítica y Energía ágil");
    }

    public void printExplorationScreen(GameState gameState) {
        Player player = gameState.getPlayer();
        DungeonFloor floor = gameState.getCurrentFloor();

        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap(
                "\n==================== [ PISO " + floor.getFloorNumber() + " / " + gameState.getMaxFloors() + " ] ===================="));
        System.out.println(AnsiColor.PURPLE.wrap(" " + FloorTheme.forFloor(floor.getFloorNumber()).getDisplayName()));
        printPlayerHud(player);
        System.out.println();
        printMinimap(floor);
        System.out.println();
        Room current = floor.getCurrentRoom();
        System.out.println(AnsiColor.WHITE.boldWrap("Ubicación actual: ") + current.getType().getDescription() +
                (current.isCleared() ? AnsiColor.BRIGHT_BLACK.wrap(" (Despejada)") : ""));
        System.out.println(AnsiColor.CYAN.boldWrap("\nAcciones de Exploración:"));
        System.out.println(" 1. Desplazarse (Norte, Sur, Este, Oeste)");
        System.out.println(" 2. Gestionar Mochila y Equipamiento");
        System.out.println(" 3. Ver Ficha Completa del Personaje");
        System.out.println(" 4. Consultar Códice (" + gameState.getDiscoveredLore().size() + "/" + LoreFragment.totalCount() + " fragmentos)");
        System.out.println(" 5. Ordenar Inventario (Comparable)");
        System.out.println(" 6. Guardar Partida en Disco");
        System.out.println(" 7. Regresar al Menú Principal");
    }

    public void printPlayerHud(Player player) {
        String hpBar = renderBar(player.getCurrentHealth(), player.getMaxHealth(), 18, AnsiColor.GREEN, AnsiColor.RED);
        System.out.printf(" %s [%s] Nivel %d | Oro: %d💰 | EXP: %d/%d%n",
                AnsiColor.BRIGHT_YELLOW.boldWrap(player.getName()),
                player.getClassTitle(),
                player.getLevel(),
                player.getGold(),
                player.getExperience(),
                player.getExperienceToNextLevel());
        System.out.printf("  Salud: [%s] %d/%d PV | %s | ATQ: %d | DEF: %d%n",
                hpBar,
                player.getCurrentHealth(),
                player.getMaxHealth(),
                AnsiColor.BRIGHT_CYAN.wrap(player.getResourceSummary()),
                player.getEffectiveAttack(),
                player.getEffectiveDefense());

        if (!player.getActiveEffects().isEmpty()) {
            System.out.print("  Estados Activos: ");
            for (ActiveStatusEffect effect : player.getActiveEffects()) {
                System.out.print(effect.getType().getColor().wrap("[" + effect + "]") + " ");
            }
            System.out.println();
        }
    }

    public void printMinimap(DungeonFloor floor) {
        System.out.println(AnsiColor.BRIGHT_BLACK.wrap("  --- Minimapa de la Mazmorra ---"));
        for (int r = 0; r < floor.getRows(); r++) {
            System.out.print("   ");
            for (int c = 0; c < floor.getCols(); c++) {
                if (r == floor.getPlayerRow() && c == floor.getPlayerCol()) {
                    System.out.print(AnsiColor.BRIGHT_YELLOW.boldWrap("[P]"));
                } else {
                    Room room = floor.getRoom(r, c);
                    if (!room.isVisited()) {
                        System.out.print(AnsiColor.BRIGHT_BLACK.wrap("[?]"));
                    } else if (room.isCleared()) {
                        System.out.print(AnsiColor.BRIGHT_BLACK.wrap("[·]"));
                    } else {
                        System.out.print(room.getType().getFormattedSymbol());
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println(AnsiColor.BRIGHT_BLACK.wrap("  Leyenda: [P] Jugador | [E] Entrada | [!] Combate | [$] Tesoro | [+] Santuario | [M] Mercader | [~] Inscripción | [B] Jefe | [?] Oculto"));
    }

    public void printInventory(Inventory inventory) {
        System.out.println(AnsiColor.CYAN.boldWrap("\n--- MOCHILA (" + inventory.size() + "/" + inventory.getMaxCapacity() + " ranuras) ---"));
        if (inventory.isEmpty()) {
            System.out.println(AnsiColor.BRIGHT_BLACK.wrap("  (La mochila está completamente vacía)"));
            return;
        }
        List<Item> items = inventory.getItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.printf(" %2d. %s [%s] (%s) Valor: %d💰 - %s%n",
                    (i + 1),
                    item.getRarity().format(item.getName()),
                    item.getItemType().getDisplayName(),
                    item.getSummaryStats(),
                    item.getGoldValue(),
                    AnsiColor.BRIGHT_BLACK.wrap(item.getDescription()));
        }
    }

    public void printEquipment(Map<EquipmentSlot, Equippable> equipment) {
        System.out.println(AnsiColor.CYAN.boldWrap("\n--- EQUIPAMIENTO ACTUAL ---"));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Equippable eq = equipment.get(slot);
            if (eq instanceof Item item) {
                System.out.printf("  %-12s: %s (%s)%n",
                        slot.getDisplayName(),
                        item.getRarity().format(item.getName()),
                        item.getSummaryStats());
            } else {
                System.out.printf("  %-12s: %s%n", slot.getDisplayName(), AnsiColor.BRIGHT_BLACK.wrap("[Vacío]"));
            }
        }
    }

    public void printCharacterSheet(Player player) {
        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap("\n=================== FICHA DETALLADA DEL HÉROE ==================="));
        System.out.println(" Nombre:       " + player.getName());
        System.out.println(" Clase:        " + player.getClassTitle());
        System.out.println(" Nivel:        " + player.getLevel());
        System.out.println(" Experiencia:  " + player.getExperience() + " / " + player.getExperienceToNextLevel());
        System.out.println(" Salud:        " + player.getCurrentHealth() + " / " + player.getMaxHealth() + " PV");
        System.out.println(" Recurso:      " + player.getResourceSummary());
        System.out.println(" Poder Ataque: " + player.getEffectiveAttack() + " (Base: " + player.getBaseAttackPower() + ")");
        System.out.println(" Defensa:      " + player.getEffectiveDefense() + " (Base: " + player.getBaseDefense() + ")");
        System.out.println(" Prob. Crítica:" + String.format(" %.1f%% (x%.2f)", player.getCriticalChance() * 100, player.getCriticalMultiplier()));
        System.out.println(" Oro:          " + player.getGold() + " monedas");
        printEquipment(player.getEquippedItems());
        printInventory(player.getInventory());
        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap("=================================================================="));
    }

    public void printCombatHeader(int round, Combatant player, Combatant enemy) {
        System.out.println(AnsiColor.BRIGHT_RED.boldWrap(
                "\n------------------------- [ RONDA " + round + " DE COMBATE ] -------------------------"));
        String playerHp = renderBar(player.getCurrentHealth(), player.getMaxHealth(), 15, AnsiColor.GREEN, AnsiColor.RED);
        String enemyHp = renderBar(enemy.getCurrentHealth(), enemy.getMaxHealth(), 15, AnsiColor.RED, AnsiColor.BRIGHT_BLACK);

        System.out.printf(" %-22s [%s] %d/%d PV%n",
                AnsiColor.BRIGHT_CYAN.boldWrap(player.getName()), playerHp, player.getCurrentHealth(), player.getMaxHealth());
        System.out.printf(" %-22s [%s] %d/%d PV%n",
                AnsiColor.BRIGHT_RED.boldWrap(enemy.getName()), enemyHp, enemy.getCurrentHealth(), enemy.getMaxHealth());
        System.out.println("---------------------------------------------------------------------");
    }

    public void printSkillResult(SkillResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ⚔ ").append(AnsiColor.BOLD.wrap(result.getActorName()))
          .append(" ejecuta ").append(AnsiColor.CYAN.boldWrap(result.getSkillName())).append(": ");

        if (result.isSelfHeal()) {
            sb.append(AnsiColor.GREEN.boldWrap("+" + result.getPrimaryValue() + " PV restaurados."));
        } else {
            sb.append(AnsiColor.RED.boldWrap(result.getPrimaryValue() + " de daño"))
              .append(" sobre ").append(result.getTargetName()).append(".");
        }

        if (result.isCriticalHit()) {
            sb.append(" ").append(AnsiColor.BRIGHT_YELLOW.boldWrap("[¡CRÍTICO!]"));
        }
        if (result.getAppliedStatus() != null) {
            sb.append(" ").append(result.getAppliedStatus().getColor().boldWrap("[+" + result.getAppliedStatus().getDisplayName() + "]"));
        }
        if (result.getDetailMessage() != null && !result.getDetailMessage().isBlank()) {
            sb.append(" ").append(AnsiColor.BRIGHT_BLACK.wrap(result.getDetailMessage()));
        }
        System.out.println(sb);
    }

    public void printSkillsMenu(List<Skill> skills) {
        System.out.println(AnsiColor.CYAN.boldWrap("\nSelecciona Habilidad para ejecutar:"));
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            System.out.printf(" %d. %-18s [%s] - %s%n",
                    (i + 1),
                    AnsiColor.BOLD.wrap(s.getName()),
                    AnsiColor.YELLOW.wrap(s.getCostDescription()),
                    s.getDescription());
        }
    }

    /** Texto narrativo ajustado al ancho de consola, en cursiva. */
    public void printNarration(String text) {
        for (String line : wrapText(text, NARRATION_WIDTH)) {
            System.out.println("  " + AnsiColor.ITALIC.wrap(line));
        }
    }

    /** Diálogo de un personaje: nombre resaltado y su frase debajo. */
    public void printDialogue(String speaker, String line) {
        System.out.println("\n  " + AnsiColor.BRIGHT_PURPLE.boldWrap(speaker + ":"));
        for (String wrapped : wrapText(line, NARRATION_WIDTH - 2)) {
            System.out.println("    " + AnsiColor.BRIGHT_WHITE.wrap(wrapped));
        }
    }

    public void printFloorArrival(int floorNumber) {
        FloorTheme theme = FloorTheme.forFloor(floorNumber);
        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap(
                "\n~~~~~~~~~~ PISO " + floorNumber + ": " + theme.getDisplayName().toUpperCase() + " ~~~~~~~~~~"));
        printNarration(theme.getArrivalText());
    }

    public void printLoreFragment(LoreFragment fragment) {
        System.out.println("\n  " + AnsiColor.BRIGHT_WHITE.boldWrap(
                toRoman(fragment.getEntryNumber()) + ". " + fragment.getTitle()));
        printNarration(fragment.getText());
    }

    /** Códice completo agrupado por piso; los fragmentos sin descubrir aparecen ocultos. */
    public void printCodex(Set<LoreFragment> discovered) {
        System.out.println(AnsiColor.BRIGHT_PURPLE.boldWrap(
                "\n==================== CÓDICE DE LA VIGILIA (" + discovered.size() + "/"
                        + LoreFragment.totalCount() + ") ===================="));
        for (FloorTheme theme : FloorTheme.values()) {
            System.out.println(AnsiColor.CYAN.boldWrap("\n--- " + theme.getDisplayName() + " ---"));
            for (LoreFragment fragment : LoreFragment.forTheme(theme)) {
                if (discovered.contains(fragment)) {
                    printLoreFragment(fragment);
                } else {
                    System.out.println("\n  " + AnsiColor.BRIGHT_BLACK.wrap(
                            toRoman(fragment.getEntryNumber()) + ". ??? (fragmento sin descubrir)"));
                }
            }
        }
        if (discovered.size() == LoreFragment.totalCount()) {
            System.out.println(AnsiColor.BRIGHT_YELLOW.boldWrap(
                    "\n  Has reunido la historia completa de Vael Aruun."));
        }
    }

    public void printSuccess(String message) {
        System.out.println(AnsiColor.BRIGHT_GREEN.boldWrap("  [✓] " + message));
    }

    public void printWarning(String message) {
        System.out.println(AnsiColor.BRIGHT_YELLOW.boldWrap("  [!] " + message));
    }

    public void printError(String message) {
        System.out.println(AnsiColor.BRIGHT_RED.boldWrap("  [✗] " + message));
    }

    public void printDefeatScreen(GameState state) {
        System.out.println(AnsiColor.BRIGHT_RED.boldWrap("""
                \n================================================================================
                                          ¡HAS SIDO DERROTADO!
                    Tu héroe ha sucumbido a los horrores que habitan la mazmorra.
                ================================================================================
                """));
        printNarration(state.getPlayer().getName() + " cae en "
                + FloorTheme.forFloor(state.getCurrentFloor().getFloorNumber()).getDisplayName()
                + ". La Grieta sigue abierta, y la Vigilia suma otro nombre a sus muros.");
    }

    public void printVictoryScreen(GameState state) {
        System.out.println(AnsiColor.BRIGHT_YELLOW.boldWrap("""
                \n================================================================================
                                       ¡VICTORIA TRIUNFAL LEGENDARIA!
                    Has derrotado al Señor del Abismo y purificado las profundidades.
                    ¡Obtienes el título de Campeón Supremo de la Mazmorra!
                ================================================================================
                """));
        printNarration("La séptima cadena se tensa por primera vez en siglos y la Grieta enmudece. "
                + "Arriba, en las ruinas de Vael Aruun, alguien tendrá que volver a montar guardia. "
                + state.getPlayer().getName() + " ya sabe quién.");
        System.out.println(AnsiColor.BRIGHT_BLACK.wrap("\n  Fragmentos del códice reunidos: "
                + state.getDiscoveredLore().size() + "/" + LoreFragment.totalCount()));
    }

    private static List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            if (current.length() > 0 && current.length() + 1 + word.length() > width) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if (current.length() > 0) {
                current.append(' ');
            }
            current.append(word);
        }
        if (current.length() > 0) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static String toRoman(int number) {
        String[] numerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return number >= 1 && number <= numerals.length ? numerals[number - 1] : String.valueOf(number);
    }

    private String renderBar(int current, int max, int totalBlocks, AnsiColor fill, AnsiColor empty) {
        if (max <= 0) return "";
        double fraction = Math.max(0.0, Math.min(1.0, (double) current / max));
        int filled = (int) Math.round(fraction * totalBlocks);
        int unfilled = totalBlocks - filled;
        return fill.wrap("█".repeat(filled)) + empty.wrap("░".repeat(unfilled));
    }
}

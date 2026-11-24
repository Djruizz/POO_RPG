package rpg.model.lore;

import java.util.Arrays;
import java.util.List;

/**
 * Fragmentos de historia que el héroe encuentra en las salas de inscripción y reúne en su códice.
 * El orden de declaración es el orden de lectura.
 */
public enum LoreFragment {
    VIGIL_OATH(FloorTheme.CATACOMBS, "El juramento de la Vigilia",
            "Grabado sobre un dintel: «Vael Aruun no fue construido para adorar, sino para tapar. "
                    + "Bajo esta piedra se abre la Grieta. Quien vista nuestro hábito jura tres cosas: "
                    + "que nadie baje, que nada suba, y que la Vigilia no termine con su vida.»"),
    ALDRIC_TOMBSTONE(FloorTheme.CATACOMBS, "Lápida de Sir Aldric",
            "«Aquí vela Sir Aldric de Thornwald, Custodio de la Primera Puerta. Cuando las criaturas "
                    + "subieron por primera vez, sostuvo el umbral tres noches. Lo enterramos de pie y con "
                    + "la espada desenvainada, como pidió. Que nadie le diga que puede descansar.»"),
    NOVICE_LETTER(FloorTheme.CATACOMBS, "Carta de un novicio",
            "«Hermana: el Prior Morvane dice que vigilar no basta, que una herida no se cura mirándola. "
                    + "Ha ordenado fundir las campanas. Ya no llamamos a oración; ahora solo se oye martillar "
                    + "abajo, en la forja. Los goblins de las galerías han empezado a imitar el ritmo.»"),

    FORGE_LOG(FloorTheme.SUNKEN_FORGE, "Bitácora de la forja",
            "«Día 212. Las Siete Cadenas piden más metal del que hay en el valle. La Maestra Ignara ha "
                    + "echado al crisol los cálices, los relicarios y las espadas de los muertos. Dice que "
                    + "el hierro que ha conocido un juramento aguanta mejor.»"),
    ANVIL_MARK(FloorTheme.SUNKEN_FORGE, "Marca en un yunque",
            "«Seis cadenas templadas en agua bendita. Para la séptima no quedaba agua. El Prior ofreció "
                    + "otra cosa para templarla. No pregunté qué. —I.»"),
    PRIOR_LAST_ORDERS(FloorTheme.SUNKEN_FORGE, "Últimas órdenes del Prior",
            "«A toda la Orden: las cadenas deben anclarse desde dentro. Bajaré yo. Si la séptima cede, "
                    + "sellad la forja conmigo abajo y no abráis a ninguna voz, aunque sea la mía. Sobre todo "
                    + "si es la mía. —Hadrien Morvane, último Prior de Vael Aruun.»"),

    REVERSED_PRAYER(FloorTheme.ABYSS_HEART, "Plegaria invertida",
            "Arañadas en la roca, las palabras del juramento de la Vigilia escritas de derecha a "
                    + "izquierda, una y otra vez, hasta que la letra deja de parecer humana: "
                    + "«adiv us noc enimret on ailigiV al euq»."),
    WHAT_THE_RIFT_SAYS(FloorTheme.ABYSS_HEART, "Lo que dice la Grieta",
            "«No hay monstruo en el fondo. La Grieta es hambre con forma de agujero, y el hambre necesita "
                    + "una voluntad que la contenga. Las cadenas no atan al Abismo: atan a su guardián. "
                    + "Mientras alguien permanezca aquí, lo de abajo no sube.»"),
    MORVANE_LAST_PAGE(FloorTheme.ABYSS_HEART, "Diario de Morvane, última página",
            "«Ya no recuerdo el rostro de Ignara ni el nombre de mi orden. Recuerdo que debía cerrar algo. "
                    + "Las criaturas me llaman Señor. Si alguien lee esto: no soy el enemigo que viniste a "
                    + "matar, soy la puerta. Y una puerta rota hay que cambiarla.»");

    private final FloorTheme theme;
    private final String title;
    private final String text;

    LoreFragment(FloorTheme theme, String title, String text) {
        this.theme = theme;
        this.title = title;
        this.text = text;
    }

    /** Fragmentos que pueden encontrarse en un tema de piso, en orden de lectura. */
    public static List<LoreFragment> forTheme(FloorTheme theme) {
        return Arrays.stream(values()).filter(f -> f.theme == theme).toList();
    }

    public static int totalCount() {
        return values().length;
    }

    /** Número de entrada dentro del códice (1-based). */
    public int getEntryNumber() {
        return ordinal() + 1;
    }

    public FloorTheme getTheme() {
        return theme;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}

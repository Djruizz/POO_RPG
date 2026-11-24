package rpg.model.lore;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Identidad narrativa de cada piso de la mazmorra: nombre del lugar, texto de llegada,
 * descripciones de ambiente y el guardián que lo custodia junto con sus diálogos.
 */
public enum FloorTheme {
    CATACOMBS(
            "Las Catacumbas de la Vigilia",
            "Bajo las ruinas del monasterio de Vael Aruun, los nichos de la Orden de la Vigilia "
                    + "se alinean hasta donde alcanza tu antorcha. Los monjes-guerreros fueron enterrados "
                    + "de pie, mirando hacia abajo: ni muertos dejaron de vigilar la Grieta.",
            List.of(
                    "Nichos vacíos a ambos lados. Alguien, o algo, sacó los cuerpos desde dentro.",
                    "Un pasillo frío. La cera de mil velas apagadas cubre el suelo como escarcha.",
                    "Silencio. Una hilera de espadas oxidadas en el muro marca los votos de quienes ya no pueden cumplirlos.",
                    "El aire huele a tierra húmeda e incienso rancio. Nada se mueve... por ahora."
            ),
            "Sir Aldric, el Custodio Sin Reposo",
            "«Nadie baja. Nadie sube. Ese fue mi juramento... y la muerte no me relevó de él.»",
            "«¡La Vigilia no se rinde! ¡NO SE RINDE!»",
            "«Entonces... ¿ha llegado el relevo? Baja, pues. Y perdona al Prior, si aún queda algo que perdonar.»"
    ),
    SUNKEN_FORGE(
            "La Forja Hundida",
            "El calor te golpea antes que la luz. Aquí la Orden fundió sus campanas, sus espadas y "
                    + "hasta sus reliquias para forjar las Siete Cadenas que debían cerrar la Grieta. "
                    + "Los hornos nunca se apagaron, aunque ya no queman carbón.",
            List.of(
                    "Yunques partidos y moldes de eslabones del tamaño de un hombre. El metal aún está tibio.",
                    "Un canal de escoria reseca cruza la sala. Algo dejó huellas en ella cuando todavía estaba al rojo.",
                    "El pasillo vibra con un martilleo lejano y paciente. Nadie debería estar trabajando aquí abajo.",
                    "Ceniza hasta los tobillos. Bajo ella, el brillo apagado de herramientas que nadie recogió."
            ),
            "Maestra Ignara, la Forjadora de Cadenas",
            "«Seis cadenas aguantaron. La séptima se rompió, y yo sigo aquí, forjándola de nuevo. "
                    + "¿Traes metal... o eres el metal?»",
            "«¡Al horno! ¡Todo sirve para la séptima cadena!»",
            "«El fuego... por fin se apaga. Dile a Morvane que su cadena nunca estuvo floja. El eslabón débil era él.»"
    ),
    ABYSS_HEART(
            "El Corazón del Abismo",
            "Ya no hay muros labrados, solo roca negra que parece respirar. Seis cadenas colosales se "
                    + "pierden tensas en la oscuridad; la séptima cuelga rota. Al fondo, una voz reza una "
                    + "plegaria que conoces, pero con las palabras al revés.",
            List.of(
                    "La roca late bajo tus botas, lenta, como un corazón dormido.",
                    "Tu antorcha alumbra menos aquí. La oscuridad no se aparta: espera.",
                    "Un eslabón roto del tamaño de una carreta yace en el suelo. La fractura es limpia, hecha desde dentro.",
                    "Escuchas tu propio nombre susurrado desde abajo. No respondes."
            ),
            "Morvane, Señor del Abismo",
            "«Bajé a cerrar la Grieta y la Grieta me abrió a mí. No vengas a salvarme, forastero. Ven a sustituirme.»",
            "«¡El Abismo no se cierra! ¡Solo cambia de guardián!»",
            "«La séptima cadena... nunca fue de hierro. Ciérrala tú. Y que nadie vuelva a bajar.»"
    );

    private final String displayName;
    private final String arrivalText;
    private final List<String> ambientLines;
    private final String bossName;
    private final String bossIntroLine;
    private final String bossEnrageLine;
    private final String bossDeathLine;

    FloorTheme(String displayName, String arrivalText, List<String> ambientLines,
               String bossName, String bossIntroLine, String bossEnrageLine, String bossDeathLine) {
        this.displayName = displayName;
        this.arrivalText = arrivalText;
        this.ambientLines = ambientLines;
        this.bossName = bossName;
        this.bossIntroLine = bossIntroLine;
        this.bossEnrageLine = bossEnrageLine;
        this.bossDeathLine = bossDeathLine;
    }

    /**
     * Tema correspondiente a un número de piso. Los pisos más profundos que el último tema
     * reutilizan el del Abismo.
     */
    public static FloorTheme forFloor(int floorNumber) {
        if (floorNumber < 1) {
            throw new IllegalArgumentException("El número de piso debe ser al menos 1.");
        }
        FloorTheme[] themes = values();
        return themes[Math.min(floorNumber, themes.length) - 1];
    }

    /** Número de piso (1-based) al que pertenece este tema. */
    public int getFloorNumber() {
        return ordinal() + 1;
    }

    public String randomAmbientLine() {
        return ambientLines.get(ThreadLocalRandom.current().nextInt(ambientLines.size()));
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArrivalText() {
        return arrivalText;
    }

    public String getBossName() {
        return bossName;
    }

    public String getBossIntroLine() {
        return bossIntroLine;
    }

    public String getBossEnrageLine() {
        return bossEnrageLine;
    }

    public String getBossDeathLine() {
        return bossDeathLine;
    }
}

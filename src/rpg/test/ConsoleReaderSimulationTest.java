package rpg.test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import rpg.ui.ConsoleReader;

/**
 * Prueba la resistencia de ConsoleReader contra entradas corruptas, cadenas vacías,
 * números fuera de rango y caracteres no numéricos.
 */
public final class ConsoleReaderSimulationTest {

    public static void main(String[] args) {
        System.out.print("Probando robustez de ConsoleReader ante fallos de tipeo... ");

        // Simula:
        // 1. Línea vacía
        // 2. Letras "abc"
        // 3. Número fuera de rango (99)
        // 4. Número válido (2)
        // 5. Confirmación inválida "quizas"
        // 6. Confirmación válida "s"
        String simulatedInput = "\n" +
                "abc\n" +
                "99\n" +
                "2\n" +
                "quizas\n" +
                "s\n";

        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        ConsoleReader reader = new ConsoleReader(in);

        int chosen = reader.readIntInRange("Opción", 1, 3);
        assert chosen == 2 : "No leyó correctamente el valor válido tras los errores.";

        boolean confirmed = reader.readYesNo("Confirmar");
        assert confirmed : "No leyó correctamente la respuesta afirmativa.";

        System.out.println("OK");
    }
}

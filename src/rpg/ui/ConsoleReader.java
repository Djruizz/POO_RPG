package rpg.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Gestor robusto de lectura de consola protegido contra fallos de tipeo,
 * excepciones de tipo (InputMismatchException) y desbordamientos de buffer.
 */
public final class ConsoleReader {
    private final Scanner scanner;

    public ConsoleReader(InputStream inputStream) {
        this.scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Solicita y valida un número entero dentro del rango inclusivo [min, max].
     */
    public int readIntInRange(String prompt, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Configuración de rango inválida: min > max.");
        }
        while (true) {
            System.out.print(AnsiColor.CYAN.boldWrap("➤ " + prompt + " [" + min + "-" + max + "]: "));
            try {
                String rawLine = scanner.nextLine().trim();
                if (rawLine.isEmpty()) {
                    throw new InputMismatchException("Entrada vacía.");
                }
                int value = Integer.parseInt(rawLine);
                if (value < min || value > max) {
                    System.out.println(AnsiColor.YELLOW.wrap(
                            "  [!] Opción fuera de rango. Ingresa un número entre " + min + " y " + max + "."));
                    continue;
                }
                return value;
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println(AnsiColor.RED.wrap(
                        "  [✗] Entrada inválida. Debes escribir un número entero válido."));
            }
        }
    }

    /**
     * Lee una cadena no vacía que cumpla con una longitud máxima.
     */
    public String readNonBlankString(String prompt, int maxLength) {
        while (true) {
            System.out.print(AnsiColor.CYAN.boldWrap("➤ " + prompt + ": "));
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println(AnsiColor.RED.wrap("  [✗] El texto no puede estar vacío."));
                continue;
            }
            if (line.length() > maxLength) {
                System.out.println(AnsiColor.YELLOW.wrap(
                        "  [!] Máximo permitido: " + maxLength + " caracteres."));
                continue;
            }
            return line;
        }
    }

    /**
     * Lee una confirmación afirmativa o negativa (S/N).
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(AnsiColor.CYAN.boldWrap("➤ " + prompt + " (S/N): "));
            String input = scanner.nextLine().trim().toUpperCase();
            if ("S".equals(input) || "SI".equals(input) || "SÍ".equals(input) || "Y".equals(input)) {
                return true;
            }
            if ("N".equals(input) || "NO".equals(input)) {
                return false;
            }
            System.out.println(AnsiColor.YELLOW.wrap("  [!] Responde 'S' para Sí o 'N' para No."));
        }
    }

    /**
     * Pausa la ejecución hasta que el usuario presione la tecla ENTER.
     */
    public void waitForEnter() {
        System.out.print(AnsiColor.BRIGHT_BLACK.wrap("\n  [Presiona ENTER para continuar...]"));
        try {
            scanner.nextLine();
        } catch (Exception ignored) {}
    }
}

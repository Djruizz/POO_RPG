package rpg.exception;

import java.io.Serial;

/**
 * Excepción base checked para todas las reglas de negocio y fallos del dominio RPG.
 */
public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}

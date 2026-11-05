package rpg.exception;

import java.io.Serial;

/**
 * Se lanza cuando ocurre un error en la capa de persistencia (I/O, corrupción de archivo o incompatibilidad).
 */
public class PersistenceException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

package rpg.exception;

import java.io.Serial;

/**
 * Se lanza cuando el jugador o entidad intenta ejecutar una acción ilícita según las reglas del juego
 * (ej. desequipar una ranura vacía, curarse cuando la vida ya está al máximo, o falta de furia/energía).
 */
public class InvalidActionException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

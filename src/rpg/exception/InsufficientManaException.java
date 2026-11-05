package rpg.exception;

import java.io.Serial;

/**
 * Se lanza cuando un conjurador intenta lanzar una habilidad que requiere más maná del disponible.
 */
public class InsufficientManaException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int requiredMana;
    private final int currentMana;

    public InsufficientManaException(int requiredMana, int currentMana) {
        super(String.format("Maná insuficiente para conjurar: se requieren %d PM pero tienes %d PM.", requiredMana, currentMana));
        this.requiredMana = requiredMana;
        this.currentMana = currentMana;
    }

    public int getRequiredMana() {
        return requiredMana;
    }

    public int getCurrentMana() {
        return currentMana;
    }
}

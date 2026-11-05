package rpg.model.combat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import rpg.model.enums.StatusEffectType;

/**
 * Representa un estado alterado temporal con potencia y duración en turnos.
 */
public final class ActiveStatusEffect implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final StatusEffectType type;
    private int remainingTurns;
    private final int potency;

    public ActiveStatusEffect(StatusEffectType type, int remainingTurns, int potency) {
        this.type = Objects.requireNonNull(type, "El tipo de efecto no puede ser nulo.");
        if (remainingTurns <= 0) {
            throw new IllegalArgumentException("La duración del efecto debe ser al menos 1 turno.");
        }
        this.remainingTurns = remainingTurns;
        this.potency = Math.max(0, potency);
    }

    public StatusEffectType getType() {
        return type;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public int getPotency() {
        return potency;
    }

    public void decrementTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActiveStatusEffect that)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.getDisplayName() + " (" + remainingTurns + "t)";
    }
}

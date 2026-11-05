package rpg.model.skill;

import java.io.Serial;
import java.util.Objects;

/**
 * Clase base abstracta con metadatos y validaciones comunes para todas las habilidades.
 */
public abstract class AbstractSkill implements Skill {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final boolean selfTargeted;

    protected AbstractSkill(String name, String description, boolean selfTargeted) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la habilidad no puede estar vacío.");
        }
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.selfTargeted = selfTargeted;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isSelfTargeted() {
        return selfTargeted;
    }

    @Override
    public String toString() {
        return name + " [" + getCostDescription() + "] - " + description;
    }
}

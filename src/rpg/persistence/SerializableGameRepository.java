package rpg.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import rpg.exception.PersistenceException;
import rpg.model.dungeon.GameState;

/**
 * Implementación de persistencia basada en java.io.Serializable que preserva
 * el grafo polimórfico completo del estado de juego en archivos binarios (.dat).
 */
public final class SerializableGameRepository implements GameRepository {
    private static final String DEFAULT_SAVE_DIR = "saves";
    private static final String EXTENSION = ".dat";

    private final Path saveDirectory;

    public SerializableGameRepository() {
        this(Paths.get(DEFAULT_SAVE_DIR));
    }

    public SerializableGameRepository(Path saveDirectory) {
        this.saveDirectory = Objects.requireNonNull(saveDirectory, "El directorio de guardado no puede ser nulo.");
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(saveDirectory)) {
                Files.createDirectories(saveDirectory);
            }
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo crear el directorio de guardado: " + e.getMessage());
        }
    }

    @Override
    public void save(GameState state, String slotName) throws PersistenceException {
        Objects.requireNonNull(state, "El estado a guardar no puede ser nulo.");
        if (slotName == null || slotName.isBlank()) {
            throw new PersistenceException("El nombre de la ranura de guardado no puede estar vacío.");
        }
        ensureDirectoryExists();

        Path filePath = saveDirectory.resolve(sanitizeSlotName(slotName) + EXTENSION);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(state);
            oos.flush();
        } catch (IOException e) {
            throw new PersistenceException("Error al escribir el archivo de guardado en " + filePath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public GameState load(String slotName) throws PersistenceException {
        if (slotName == null || slotName.isBlank()) {
            throw new PersistenceException("El nombre de la ranura de guardado no puede estar vacío.");
        }
        Path filePath = saveDirectory.resolve(sanitizeSlotName(slotName) + EXTENSION);
        if (!Files.exists(filePath)) {
            throw new PersistenceException("No existe ninguna partida guardada en la ranura: " + slotName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof GameState state) {
                return state;
            } else {
                throw new PersistenceException("El archivo de guardado está corrupto o contiene un tipo incompatible.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException("Error al deserializar la partida desde " + filePath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> listSaveSlots() {
        List<String> slots = new ArrayList<>();
        File dir = saveDirectory.toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENSION));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    slots.add(name.substring(0, name.length() - EXTENSION.length()));
                }
            }
        }
        return slots;
    }

    private String sanitizeSlotName(String raw) {
        return raw.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}

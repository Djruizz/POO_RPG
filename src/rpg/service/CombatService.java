package rpg.service;

import java.util.Objects;
import rpg.exception.GameException;
import rpg.exception.InventoryFullException;
import rpg.model.combat.Combatant;
import rpg.model.combat.SkillResult;
import rpg.model.entity.Enemy;
import rpg.model.entity.Player;
import rpg.model.item.Item;
import rpg.model.skill.Skill;

/**
 * Servicio de dominio responsable de la orquestación del combate,
 * resolución de acciones de habilidades y otorgamiento de recompensas de victoria.
 */
public final class CombatService {

    public SkillResult executeSkillAction(Combatant user, Skill skill, Combatant target) throws GameException {
        Objects.requireNonNull(user, "El ejecutor no puede ser nulo.");
        Objects.requireNonNull(skill, "La habilidad no puede ser nula.");
        Objects.requireNonNull(target, "El objetivo no puede ser nulo.");

        return skill.execute(user, target);
    }

    public String grantVictoryRewards(Player player, Enemy defeatedEnemy, Item droppedLoot) {
        Objects.requireNonNull(player, "El jugador no puede ser nulo.");
        Objects.requireNonNull(defeatedEnemy, "El enemigo derrotado no puede ser nulo.");

        int expGained = defeatedEnemy.getExperienceReward();
        int goldGained = defeatedEnemy.getGoldReward();

        player.addGold(goldGained);
        int levelsUp = player.gainExperience(expGained);

        StringBuilder report = new StringBuilder();
        report.append(String.format("¡Victoria sobre %s! Obtienes +%d EXP y +%d💰 de oro.",
                defeatedEnemy.getName(), expGained, goldGained));

        if (levelsUp > 0) {
            report.append(String.format(" ¡SUBISTE %d NIVEL(ES)! Salud restaurada y estadísticas incrementadas.", levelsUp));
        }

        if (droppedLoot != null) {
            try {
                player.getInventory().addItem(droppedLoot);
                report.append(" Botín obtenido: ").append(droppedLoot.getName()).append(" guardado en la mochila.");
            } catch (InventoryFullException e) {
                report.append(" Botín caído: ").append(droppedLoot.getName())
                      .append(" (¡Mochila llena! No se pudo recoger el objeto).");
            }
        }

        return report.toString();
    }
}

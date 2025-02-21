package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

@CommandAlias("jobs")
@CommandPermission("auraskills.command.jobmenu")
public class JobsMenuCommand extends BaseCommand {

    private final AuraSkills plugin;

    public JobsMenuCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onJobsMenu(Player player) {
        // Abrir el menú de trabajos
        plugin.getSlate().openMenu(player, "jobs");
    }
}
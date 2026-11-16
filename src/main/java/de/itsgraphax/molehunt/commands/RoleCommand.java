package de.itsgraphax.molehunt.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.Executor;
import org.bukkit.entity.Player;

import static de.itsgraphax.molehunt.Molehunt.mhp;

@Command("role")
public class RoleCommand {
    @Executes
    void getRole(@Executor Player player) {
        if (DebugCommand.checkRunningAndMsg(player)) return;

        var role = mhp.settings.getRole(player);
        player.sendMessage(Component.empty()
                .append(Component.text("Your role is:"))
                .append(role.getName().decorate(TextDecoration.BOLD))
                .append(Component.text(".")));

        player.sendMessage(Component.empty()
                .append(role.description));
    }
}

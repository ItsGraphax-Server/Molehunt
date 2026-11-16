package de.itsgraphax.molehunt.commands;

import de.itsgraphax.molehunt.data.presets.Presets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.strokkur.commands.Aliases;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.Subcommand;
import net.strokkur.commands.paper.RequiresOP;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.itsgraphax.molehunt.Molehunt.mhp;

@Command("settings")
@Aliases("mhs")
@RequiresOP
public class SettingsCommand {
    @Executes("show")
    void show(CommandSender s) {
        s.sendMessage(mhp.settings.show());
    }

    @Executes("validate")
    void validated(CommandSender s) {
        mhp.settings.updatedSinceLastRun = true;

        s.sendMessage(Component.text("Marked your settings as updated", NamedTextColor.GREEN));
    }

    @Executes("preset")
    void preset(CommandSender s, Presets preset) {
        preset.use();

        mhp.settings.updatedSinceLastRun = true;

        s.sendMessage(Component.text("Loaded up the preset", NamedTextColor.GREEN));
    }

    @Subcommand("distribution")
    static class DistributionSub {
        @Executes("default")
        void defaultRole(CommandSender s, String role) {
            mhp.settings.getDistribution().defaultRole = role;

            s.sendMessage(
                    Component.text(String.format("The default role is now %s", role), NamedTextColor.GREEN)
            );
        }

        @Executes("amount")
        void distribution(CommandSender s, String role, int amount) {
            mhp.settings.getDistribution().amount.put(role, amount);

            s.sendMessage(
                    Component.text(String.format("There will now be %s %s(s)", amount, role), NamedTextColor.GREEN)
            );
        }

        @Executes("force")
        void force(CommandSender s, Player player, String role) {
            mhp.settings.getDistribution().forced.put(player.getUniqueId(), role);

            s.sendMessage(
                    Component.text(String.format("Forced the role of %s to %s", player.getName(), role))
            );
        }

        @Executes("unforce")
        void unforce(CommandSender s, Player player) {
            mhp.settings.getDistribution().forced.remove(player.getUniqueId());

            s.sendMessage(
                    Component.text(String.format("Unforced any role %s had", player.getName()))
            );
        }
    }

    @Subcommand("values")
    static class ValuesSub {

    }
}

package de.itsgraphax.molehunt.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.strokkur.commands.Aliases;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.Subcommand;
import net.strokkur.commands.paper.RequiresOP;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.itsgraphax.molehunt.Molehunt.mhp;

@Command("molehunt")
@Aliases("mh")
@RequiresOP
public class DebugCommand {
    public static boolean checkRunningAndMsg(CommandSender s) {
        if (mhp.data == null) {
            s.sendMessage(Component.text("There is no currently running game", NamedTextColor.RED));
            return true;
        }
        return false;
    }

    @Subcommand("role")
    static class RolesSub {
        @Executes("get")
        void get(CommandSender s, Player[] players) {
            if (checkRunningAndMsg(s)) return;

            for (var pI : players) {
                s.sendMessage(
                        Component.text(pI.getName())
                                .append(Component.text(": "))
                                .append(mhp.settings.getRole(pI).getName())
                );
            }
        }

        @Executes("set")
        void set(CommandSender s, Player player, String role) {
            if (checkRunningAndMsg(s)) return;

            mhp.data.setRoleId(player, role);

            s.sendMessage(Component.text(String.format("Set the role of %s", player.getName()), NamedTextColor.GREEN));
        }
    }

    @Executes("stopgame")
    void stopGame(CommandSender s) {
        Bukkit.getServer().sendMessage(Component.text(String.format("The currently running game was stopped by %s", s.getName()), NamedTextColor.GOLD));

        mhp.data = null;
        mhp.wm.deleteAllWorlds();
    }
}

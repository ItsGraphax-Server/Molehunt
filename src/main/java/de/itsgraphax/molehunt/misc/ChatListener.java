package de.itsgraphax.molehunt.misc;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class ChatListener implements Listener {
    @EventHandler
    void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        var mimic = Component.object().contents(ObjectContents.playerHead(player.getUniqueId()))
                        .append(Component.text(String.format(" %s: ", player.getName())))
                        .append(event.message()).build();

        event.setCancelled(true);

        if (mhp.data == null) {
            Bukkit.broadcast(mimic);
            return;
        }


        var role = mhp.settings.getRole(player);
        for (var pI : Bukkit.getOnlinePlayers()) {
            var pIRole = mhp.data.getRoleId(pI);
            var contains = role.chatBlacklist.contains(pIRole);

            if (contains && !role.invertBlacklist ||
            !contains && role.invertBlacklist) continue;

            pI.sendMessage(mimic);
        }
    }
}

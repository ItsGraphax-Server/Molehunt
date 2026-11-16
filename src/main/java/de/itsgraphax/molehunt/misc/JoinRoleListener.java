package de.itsgraphax.molehunt.misc;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class JoinRoleListener implements Listener {
    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (mhp.data == null) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
        } else {
            mhp.settings.getRole(player).onGet(player);
        }
    }
}

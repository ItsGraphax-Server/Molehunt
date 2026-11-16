package de.itsgraphax.molehunt.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class DeathListener implements Listener {
    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        if (mhp.data == null) return;

        if (mhp.settings.getRole(event.getPlayer()).hardcore)
            mhp.data.setRoleId(event.getPlayer(), "spectator");
    }
}

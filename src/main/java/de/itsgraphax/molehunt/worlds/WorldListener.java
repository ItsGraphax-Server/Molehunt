package de.itsgraphax.molehunt.worlds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class WorldListener implements Listener {
    @EventHandler
    void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player &&
                event.getEntity().getWorld() == Bukkit.getServer().getRespawnWorld()) event.setCancelled(true);
    }

    private World getPortalTarget(World from, World portalWorld) {
        if (from == mhp.wm.overworld) return portalWorld;
        if (from == portalWorld) return mhp.wm.overworld;
        return null;
    }

    @EventHandler
    void onEntityPortal(EntityPortalEvent event) {
        var to = event.getTo();
        if (to == null) return;

        var portalWorld = switch (event.getPortalType()) {
            case NETHER -> mhp.wm.nether;
            case ENDER -> mhp.wm.end;
            default -> null;
        };

        if (portalWorld == null) return;

        var target = getPortalTarget(event.getFrom().getWorld(), portalWorld);
        if (target != null) to.setWorld(target);
    }

    @EventHandler
    void onPlayerPortal(PlayerPortalEvent event) {
        var to = event.getTo();

        var portalWorld = switch (event.getCause()) {
            case NETHER_PORTAL -> mhp.wm.nether;
            case END_PORTAL -> mhp.wm.end;
            default -> null;
        };

        if (portalWorld == null) return;

        var target = getPortalTarget(event.getFrom().getWorld(), portalWorld);
        if (target != null) to.setWorld(target);
    }
}

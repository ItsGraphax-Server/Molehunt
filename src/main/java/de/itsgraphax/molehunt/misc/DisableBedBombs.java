package de.itsgraphax.molehunt.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

public class DisableBedBombs implements Listener {
    @EventHandler
    void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().getType().toString().contains("BED")) event.setCancelled(true);
    }
}

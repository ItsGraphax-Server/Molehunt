package de.itsgraphax.molehunt.misc;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;


public class LocatorUnhider {
    public static void tick() {
        for (var pI : Bukkit.getOnlinePlayers()) removeLocatorHidingModifiers(pI);
    }

    private static void removeLocatorHidingModifiers(Player player) {
        var attribute = player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
        if (attribute == null) return;

        Set<NamespacedKey> toRemove = Set.of(
                NamespacedKey.minecraft("waypoint_transmit_range_crouch"),
                NamespacedKey.minecraft("effect.waypoint_transmit_range_hide")
        );

        for (var mod : List.copyOf(attribute.getModifiers())) {
            if (toRemove.contains(mod.getKey())) {
                attribute.removeModifier(mod);
            }
        }
    }
}

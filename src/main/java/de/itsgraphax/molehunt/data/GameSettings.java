package de.itsgraphax.molehunt.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public final class GameSettings {
    public boolean updatedSinceLastRun = false;
    private final Map<String, RoleConfiguration> roles = new HashMap<>();
    private final Distribution distribution = new Distribution();

    public int spawnRadius;
    public int worldBorder;
    public boolean advancements;
    public boolean deathMessages;
    public Difficulty difficulty;


    public GameSettings() {
        clear();
    }


    public void clear() {
        updatedSinceLastRun = false;
        roles.clear();

        spawnRadius = 250;
        worldBorder = 3000;
        advancements = true;
        deathMessages = true;
        difficulty = Difficulty.NORMAL;

        var spectator = new RoleConfiguration();
        spectator.id = "spectator";
        spectator.name = Component.text("Spectator");
        spectator.description = Component.text("Enjoy the chaos!");
        spectator.gameMode = GameMode.SPECTATOR;
        spectator.invertBlacklist = true;
        spectator.chatBlacklist.add("spectator");
        addRole(spectator);
    }


    public static class Distribution {
        public final Map<String, Integer> amount = new HashMap<>();
        public final Map<UUID, String> forced = new HashMap<>();
        public String defaultRole;
    }

    public Distribution getDistribution() {
        return distribution;
    }


    public void addRole(RoleConfiguration role) {
        roles.put(role.id, role);
    }

    public RoleConfiguration getRole(String role) {
        return roles.get(role);
    }

    public RoleConfiguration getRole(Player player) {
        return getRole(mhp.data.getRoleId(player));
    }

    public Collection<RoleConfiguration> allRoles() {
        return roles.values();
    }


    private static Component text(Object obj, TextColor color) {
        return Component.text(String.valueOf(obj), color);
    }

    private static Component settingPrefix(String name) {
        return text(" • " + name + ": ", NamedTextColor.GRAY);
    }
    private static Component setting(String name, boolean bool) {
        return Component.empty()
                .append(settingPrefix(name))
                .append((bool ?
                                        text("Yes", NamedTextColor.GREEN) :
                                        text("No", NamedTextColor.RED)
                        ).decorate(TextDecoration.BOLD))
                .appendNewline();
    }
    private static Component setting(String name, String val) {
        return Component.empty()
                .append(settingPrefix(name))
                .append(Component.text(val).decorate(TextDecoration.BOLD))
                .appendNewline();
    }

    public Component show() {
        var result = Component.text();

        if (!updatedSinceLastRun) {
            result.append(text(
                    "The settings haven't been updated since the last game!\n\n",
                    NamedTextColor.RED
            ));
        }


        result.append(text("General Settings:\n", NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
        result.append(setting("Spawn Radius", String.valueOf(spawnRadius)));
        result.append(setting("World Border", String.valueOf(worldBorder)));
        result.append(setting("Advancements", advancements));
        result.append(setting("Death Messages", deathMessages));
        result.append(setting("Difficulty", difficulty.toString()));


        result.append(text("\nRoles:\n\n", NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
        for (var role : roles.values()) {
            result.append(role.asComponent());
            if (Objects.equals(role.id, distribution.defaultRole)) {
                result
                        .append(Component.text("Default Role").decorate(TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline();
            }
            result
                    .append(Component.text("Amount", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text(distribution.amount.getOrDefault(role.id, 0)))
                    .appendNewline()
                    .appendNewline()
                    .appendNewline();
        }

        return result.build();
    }
}

package de.itsgraphax.molehunt.data;

import de.itsgraphax.molehunt.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

import static de.itsgraphax.molehunt.Molehunt.mhp;

// class is initialized per game
public final class GameData {
    private final Map<UUID, String> roles = new HashMap<>();

    public GameData() {
        distributeRoles();
    }

    private void distributeRoles() {
        var rand = new Random();

        var online = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(online, rand);

        Map<String, Integer> forcedAmount = new HashMap<>();

        // forced roles
        for (var entry : mhp.settings.getDistribution().forced.entrySet()) {
            var pI = Bukkit.getPlayer(entry.getKey());
            online.remove(pI);
            if (pI == null) continue;

            setRoleId(pI, entry.getValue());
            forcedAmount.compute(entry.getValue(), (_, v) -> (v == null) ? 1 : v + 1);
        }

        // role distribution
        for (var entry : mhp.settings.getDistribution().amount.entrySet()) {
            var fA = forcedAmount.getOrDefault(entry.getKey(), 0);
            for (int unusedI = 0; unusedI < entry.getValue() - fA; unusedI++) {
                var pI = online.removeFirst();
                setRoleId(pI, entry.getKey());
            }
        }

        // default role
        for (var pI : online) {
            setRoleId(pI, mhp.settings.getDistribution().defaultRole);
        }
    }

    public void announceRole(Player player) {
        var role = mhp.settings.getRole(player);
        player.showTitle(Title.title(
                role.getName(),
                Component.text("The game has started!"),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1)))
        );
        Bukkit.getScheduler().runTaskLater(mhp, _ -> {
            player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());
            player.performCommand("molehunt:role");
        }, 80);
    }


    public void setRoleId(Player player, String role) {
        var old = getRoleId(player);

        player.removeScoreboardTag(old);
        player.addScoreboardTag(role);

        roles.put(player.getUniqueId(), role);

        mhp.settings.getRole(role).onGet(player);
    }

    public String getRoleId(Player player) {
        return roles.getOrDefault(player.getUniqueId(), "spectator");
    }


    public void checkGameEnd() {
        for (var role : mhp.settings.allRoles()) {
            if (role.goal.reachedGoal()) {
                mhp.wm.deleteAllWorlds();
                Utils.playGlobalSound(Key.key("entity.wither.death"), 1, 1);
                Utils.onlineAudience().showTitle(Title.title(
                        role.getName().append(Component.text("s")),
                        Component.text("win the game!"),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
                ));

                mhp.data = null;
                return;
            }
        }
    }
}

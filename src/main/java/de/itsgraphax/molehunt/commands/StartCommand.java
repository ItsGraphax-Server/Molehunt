
package de.itsgraphax.molehunt.commands;

import de.itsgraphax.molehunt.Utils;
import de.itsgraphax.molehunt.data.GameData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.RequiresOP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static de.itsgraphax.molehunt.Molehunt.mhp;

@Command("start")
@RequiresOP
public class StartCommand {
    private static final int COUNTDOWN_SECONDS = 5;

    @Executes
    void start(CommandSender sender) {
        if (mhp.data != null) {
            error(sender, "A game is already running! Stop it using /mh stopgame");
            return;
        }

        if (!mhp.settings.updatedSinceLastRun) {
            error(sender, "Please check your settings before starting a game!");
            return;
        }

        var players = new ArrayList<>(Bukkit.getOnlinePlayers());

        int assignedRoles = mhp.settings.getDistribution().amount.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (assignedRoles > players.size()) {
            error(sender, "There are more assigned roles than online players!");
            return;
        }

        Bukkit.broadcast(Component.text(
                "Generating worlds and finding spawnpoints. The server might lag briefly.",
                NamedTextColor.GRAY
        ));

        mhp.wm.makeNewWorlds(() -> preparePlayers(players, 0));
    }

    private void setupWorld(World world) {
        world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, mhp.settings.advancements);
        world.setGameRule(GameRules.SHOW_DEATH_MESSAGES, mhp.settings.deathMessages);
        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        world.setDifficulty(mhp.settings.difficulty);
        world.getWorldBorder().setSize(mhp.settings.worldBorder);
    }

    private void buildEndPortal() {
        var world = mhp.wm.overworld;

        world.getChunkAtAsync(0, 0, true, _ -> {
            int y = world.getHighestBlockYAt(0, 0, HeightMap.MOTION_BLOCKING) + 1;

            for (int i = -1; i <= 1; i++) {
                setPortalFrame(i, y, -2, BlockFace.SOUTH);
                setPortalFrame(i, y, 2, BlockFace.NORTH);
                setPortalFrame(-2, y, i, BlockFace.EAST);
                setPortalFrame(2, y, i, BlockFace.WEST);
            }
        });
    }

    private void setPortalFrame(int x, int y, int z, BlockFace facing) {
        Block block = mhp.wm.overworld.getBlockAt(x, y, z);
        block.setType(Material.END_PORTAL_FRAME);

        EndPortalFrame data = (EndPortalFrame) block.getBlockData();
        data.setFacing(facing);

        block.setBlockData(data);
    }

    private void prepareWorlds() {
        setupWorld(mhp.wm.overworld);
        setupWorld(mhp.wm.nether);
        setupWorld(mhp.wm.end);

        buildEndPortal();

        startCountdown(COUNTDOWN_SECONDS);
    }

    private void preparePlayers(List<? extends Player> players, int i) {
        if (i >= players.size()) {
            prepareWorlds();
            return;
        }

        var pI = players.get(i);

        pI.setHealth(20);
        pI.setFoodLevel(20);
        pI.setSaturation(6);
        pI.setExp(0);
        pI.getInventory().clear();

        // revoke advancements
        var it = Bukkit.advancementIterator();
        while (it.hasNext()) {
            var advancement = it.next();
            var prog = pI.getAdvancementProgress(advancement);

            for (var crit : prog.getAwardedCriteria()) {
                prog.revokeCriteria(crit);
            }
        }

        // teleport
        var world = mhp.wm.overworld;

        double angle = 2 * Math.PI * i / players.size();

        int x = (int) Math.round(mhp.settings.spawnRadius * Math.cos(angle));
        int z = (int) Math.round(mhp.settings.spawnRadius * Math.sin(angle));

        world.getChunkAtAsync(x >> 4, z >> 4, true, _ -> {
            int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);

            var spawn = new Location(world, x + 0.5, y, z + 0.5);
            spawn.getBlock().setType(Material.BEDROCK);
            spawn.add(0, 1, 0);

            pI.setRespawnLocation(spawn);

            pI.teleportAsync(spawn).thenAccept(success ->
                    Bukkit.getScheduler().runTask(mhp, task -> {
                        if (!success) mhp.getComponentLogger().warn("Failed to tp {}", pI.getName());

                        preparePlayers(players, i + 1);
                    })
            );
        });
    }

    private void startCountdown(int remaining) {
        if (remaining <= 0) {
            beginGame();
            return;
        }

        Utils.onlineAudience().showTitle(Title.title(
                Component.text(remaining, NamedTextColor.RED),
                Component.text("Game starting in"),
                Title.Times.times(
                        Duration.ZERO,
                        Duration.ofSeconds(2),
                        Duration.ZERO
                )
        ));

        Utils.playGlobalSound(
                Key.key("entity.wither.shoot"),
                0.5f,
                1.4f - remaining * 0.1f
        );

        Bukkit.getScheduler().runTaskLater(
                mhp,
                task -> startCountdown(remaining - 1),
                20L
        );
    }

    private void beginGame() {
        Utils.playGlobalSound(Key.key("entity.wither.spawn"), 1f, 1f);

        Bukkit.getServerTickManager().setFrozen(false);

        mhp.data = new GameData();

        for (var player : Bukkit.getOnlinePlayers()) {
            mhp.data.announceRole(player);
        }
    }

    private void error(CommandSender s, String msg) {
        s.sendMessage(Component.text(msg, NamedTextColor.RED));
    }
}

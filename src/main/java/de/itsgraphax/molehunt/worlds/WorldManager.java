package de.itsgraphax.molehunt.worlds;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class WorldManager {
    public World overworld;
    public World nether;
    public World end;

    public void makeNewWorlds(Runnable onCreate) {
        deleteAllWorlds();

        if (Bukkit.isTickingWorlds()) {
            Bukkit.getScheduler().runTaskLater(mhp, () -> makeNewWorlds(onCreate), 1);
            return;
        }

        String id = String.format("molehunt_%s", UUID.randomUUID());

        overworld = Bukkit.createWorld(new WorldCreator(id + "_overworld"));

        nether = Bukkit.createWorld(new WorldCreator(id + "_nether")
                .environment(World.Environment.NETHER));

        end = Bukkit.createWorld(new WorldCreator(id + "_end")
                .environment(World.Environment.THE_END));

        onCreate.run();
    }

    private void deleteWorld(World world) {
        if (world == null) return;

        if (Bukkit.isTickingWorlds()) {
            Bukkit.getScheduler().runTaskLater(mhp, () -> deleteWorld(world), 1);
            return;
        }

        var spawn = Bukkit.getServer().getRespawnWorld().getSpawnLocation();
        world.getPlayers().forEach(pI -> {
            pI.teleport(spawn);

            Bukkit.getScheduler().runTaskLater(mhp, () -> {
                pI.teleport(spawn);
                pI.setHealth(20);
                pI.setGameMode(GameMode.ADVENTURE);
                pI.getInventory().clear();
            }, 5);
        });

        var folder = world.getWorldFolder().toPath();

        if (!Bukkit.unloadWorld(world, false)) {
            mhp.getComponentLogger().error("Failed to unload world");
            return;
        }

        // thanks stackoverflow
        Bukkit.getScheduler().runTaskAsynchronously(mhp, () -> {
            try {
                Files.walkFileTree(folder,
                        new SimpleFileVisitor<>() {
                            @Override
                            public @NonNull FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public @NonNull FileVisitResult postVisitDirectory(@NonNull Path dir, IOException exc) throws IOException {
                                if (exc != null) throw exc;
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });

                mhp.getComponentLogger().info("Deleted world");
            } catch (IOException e) {
                mhp.getComponentLogger().error("Could not delete world: {}", String.valueOf(e));
            }
        });
    }

    public void deleteAllWorlds() {
        deleteWorld(overworld);
        deleteWorld(nether);
        deleteWorld(end);

        overworld = null;
        nether = null;
        end = null;
    }
}

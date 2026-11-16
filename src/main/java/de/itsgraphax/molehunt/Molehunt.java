package de.itsgraphax.molehunt;

import de.itsgraphax.grphxLib.shorthands.OnEnable;
import de.itsgraphax.molehunt.commands.DebugCommandBrigadier;
import de.itsgraphax.molehunt.commands.RoleCommandBrigadier;
import de.itsgraphax.molehunt.commands.SettingsCommandBrigadier;
import de.itsgraphax.molehunt.commands.StartCommandBrigadier;
import de.itsgraphax.molehunt.data.GameData;
import de.itsgraphax.molehunt.data.GameSettings;
import de.itsgraphax.molehunt.misc.*;
import de.itsgraphax.molehunt.worlds.WorldListener;
import de.itsgraphax.molehunt.worlds.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Molehunt extends JavaPlugin {
    public static Molehunt mhp;

    public GameData data = null;
    public final GameSettings settings;
    public final WorldManager wm;

    public Molehunt() {
        mhp = this;

        settings = new GameSettings();
        wm = new WorldManager();
    }

    @Override
    public void onEnable() {
        OnEnable.registerCommands(this,
                DebugCommandBrigadier::register,
                SettingsCommandBrigadier::register,
                RoleCommandBrigadier::register,
                StartCommandBrigadier::register
        );

        OnEnable.registerEvents(this,
                new JoinRoleListener(),
                new DeathListener(),
                new ChatListener(),
                new WorldListener(),
                new DisableBedBombs()
        );

        World rw = getServer().getRespawnWorld();
        rw.setDifficulty(Difficulty.PEACEFUL);
        rw.setGameRule(GameRules.ADVANCE_TIME, false);
        rw.setGameRule(GameRules.ADVANCE_WEATHER, false);

        Bukkit.getServerTickManager().setFrozen(true);

        Bukkit.getScheduler().runTaskTimer(mhp, () -> {
            if (data != null) data.checkGameEnd();
        }, 1, 1);
        Bukkit.getScheduler().runTaskTimer(mhp, LocatorUnhider::tick, 1, 1);
    }

    @Override
    public void onDisable() {
        wm.deleteAllWorlds();
    }
}

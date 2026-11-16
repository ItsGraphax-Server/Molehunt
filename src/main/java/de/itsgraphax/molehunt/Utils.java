package de.itsgraphax.molehunt;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;

public interface Utils {
    static void playGlobalSound(Key key, float volume, float pitch) {
        onlineAudience().playSound(Sound.sound(key, Sound.Source.MASTER, volume, pitch), Sound.Emitter.self());
    }

    static Audience onlineAudience() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }
}

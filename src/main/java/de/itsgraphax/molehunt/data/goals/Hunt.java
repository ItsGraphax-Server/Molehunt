package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.Objects;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public final class Hunt extends Goal {
    private final String target;

    public Hunt(String target) {
        this.target = target;
    }


    @Override
    public boolean reachedGoal() {
        for (var pI : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(mhp.data.getRoleId(pI), target)) return false;
        }
        return true;
    }

    @Override
    public Component asComponent() {
        return Component.text(String.format("Kill every %s", target));
    }
}

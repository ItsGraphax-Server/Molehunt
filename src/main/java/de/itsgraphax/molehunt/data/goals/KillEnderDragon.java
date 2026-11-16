package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public final class KillEnderDragon extends Goal {
    @Override
    public boolean reachedGoal() {
        var dragonBattle = mhp.wm.end.getEnderDragonBattle();
        return dragonBattle != null && dragonBattle.hasBeenPreviouslyKilled();
    }

    @Override
    public Component asComponent() {
        return Component.text("Kill the Ender Dragon");
    }
}

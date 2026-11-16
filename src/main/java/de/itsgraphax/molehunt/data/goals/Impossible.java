package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;

public final class Impossible extends Goal {
    @Override
    public boolean reachedGoal() {
        return false;
    }

    @Override
    public Component asComponent() {
        return Component.text("Impossible");
    }
}

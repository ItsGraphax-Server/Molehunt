package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;

public class All extends Goal {
    @Override
    public boolean reachedGoal() {
        return sub.stream().allMatch(Goal::reachedGoal);
    }

    @Override
    public Component asComponent() {
        return Component.text("All of these:");
    }
}

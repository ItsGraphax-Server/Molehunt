package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;

public class Any extends Goal {
    @Override
    public boolean reachedGoal() {
        return sub.stream().anyMatch(Goal::reachedGoal);
    }

    @Override
    public Component asComponent() {
        return Component.text("Any of these:");
    }


}

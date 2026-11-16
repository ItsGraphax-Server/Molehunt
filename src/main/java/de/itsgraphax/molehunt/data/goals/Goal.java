package de.itsgraphax.molehunt.data.goals;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Goal {
    protected final List<Goal> sub = new ArrayList<>();

    public void addSubGoal(Goal goal) {
        sub.add(goal);
    }

    public abstract boolean reachedGoal();

    public abstract Component asComponent();

    public Collection<Goal> getSubGoals() {
        return sub;
    }
}

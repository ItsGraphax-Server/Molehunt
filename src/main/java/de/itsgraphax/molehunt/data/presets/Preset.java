package de.itsgraphax.molehunt.data.presets;

import de.itsgraphax.molehunt.data.RoleConfiguration;
import de.itsgraphax.molehunt.data.goals.Any;
import de.itsgraphax.molehunt.data.goals.Hunt;
import de.itsgraphax.molehunt.data.goals.KillEnderDragon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Preset {
    void apply();

    static RoleConfiguration defaultRunner(String hunter) {
        var runner = new RoleConfiguration();
        runner.id = "runner";
        runner.goal = new Any();
        runner.goal.addSubGoal(new KillEnderDragon());
        runner.goal.addSubGoal(new Hunt(hunter));
        runner.name = Component.text("Runner");
        runner.description = Component.text("Defeat the Ender Dragon!");
        runner.color = NamedTextColor.GREEN;

        runner.hardcore = true;

        return runner;
    }

    static RoleConfiguration defaultHunter(String runner) {
        var hunter = new RoleConfiguration();
        hunter.id = "hunter";
        hunter.goal = new Hunt("runner");
        hunter.name = Component.text("Hunter");
        hunter.description = Component.text("Kill all the ")
                .append(Component.text("Runners", NamedTextColor.GREEN))
                .append(Component.text("!"));
        hunter.color = NamedTextColor.RED;
        return hunter;
    }
}

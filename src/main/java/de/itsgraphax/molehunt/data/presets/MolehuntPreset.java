package de.itsgraphax.molehunt.data.presets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class MolehuntPreset implements Preset {
    @Override
    public void apply() {
        mhp.settings.clear();

        mhp.settings.advancements = false;
        mhp.settings.deathMessages = false;

        var runner = Preset.defaultRunner("mole");
        runner.description = Component.text("Defeat the Ender Dragon or eliminate all the ")
                .append(Component.text("Moles", NamedTextColor.RED))
                .append(Component.text("!"));
        runner.locatorBarEnabled = false;
        runner.invertBlacklist = true;

        var mole = Preset.defaultHunter("runner");
        mole.id = "mole";
        mole.name = Component.text("Mole");
        mole.hardcore = true;
        mole.chatBlacklist.add("runner");

        mhp.settings.addRole(runner);
        mhp.settings.addRole(mole);

        mhp.settings.getDistribution().defaultRole = "runner";
        mhp.settings.getDistribution().amount.put("mole", (int) Math.ceil(Bukkit.getOnlinePlayers().size() / 5f));
    }
}

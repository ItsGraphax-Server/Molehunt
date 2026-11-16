package de.itsgraphax.molehunt.data.presets;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class ManhuntPreset implements Preset {
    @Override
    public void apply() {
        mhp.settings.clear();

        var runner = Preset.defaultRunner("hunter");
        var hunter = Preset.defaultHunter("runner");

        mhp.settings.addRole(runner);
        mhp.settings.addRole(hunter);

        mhp.settings.getDistribution().defaultRole = "hunter";
        mhp.settings.getDistribution().amount.put("runner", 1);
    }
}

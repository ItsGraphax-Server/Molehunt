package de.itsgraphax.molehunt.data.presets;

public enum Presets {
    MANHUNT(new ManhuntPreset()),
    MOLEHUNT(new MolehuntPreset());

    private final Preset preset;

    Presets(Preset preset) {
        this.preset = preset;
    }

    public void use() {
        preset.apply();
    }
}

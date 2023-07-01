package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class TraitConfig extends OptionProvider {

    public TraitConfig(Map<String, Object> optionMap) {
        super(optionMap);
    }

    public boolean enabled() {
        return getBoolean("enabled");
    }

}

package io.github.adamraichu.suppressopengl1280;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.adamraichu.suppressopengl1280.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.gui.registry.DefaultGuiRegistryAccess;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            @SuppressWarnings("unchecked")
            ConfigManager<ConfigOptions> manager = (ConfigManager<ConfigOptions>) AutoConfig.getConfigHolder(ConfigOptions.class);
            return new ConfigScreenProvider<>(manager, new DefaultGuiRegistryAccess(), parent).get();
        };
    }
}

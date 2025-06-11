package io.github.skippyall.technology_in_the_sea_companion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TechnologyInTheSeaCompanion implements ModInitializer {
    public static final String MOD_ID = "technologyinthesea";

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(StartManager::onServerStart);
    }
}

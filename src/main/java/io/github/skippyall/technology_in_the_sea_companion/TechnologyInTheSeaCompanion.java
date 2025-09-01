package io.github.skippyall.technology_in_the_sea_companion;

import io.github.skippyall.technology_in_the_sea_companion.groups.GroupCommand;
import io.github.skippyall.technology_in_the_sea_companion.sun.SunManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnologyInTheSeaCompanion implements ModInitializer {
    public static final String MOD_ID = "technologyinthesea";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        WorldAttachment.register();
        SunManager.register();
        ServerLifecycleEvents.SERVER_STARTED.register(StartManager::onServerStart);
        CommandRegistrationCallback.EVENT.register(GroupCommand::groupCommand);
    }
}

package io.github.skippyall.technology_in_the_sea_companion;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StartManager {
    public static final RegistryKey<World> EARTH_ORBIT = RegistryKey.of(RegistryKeys.WORLD, new Identifier("ad_astra", "earth_orbit"));
    public static final Identifier START_STRUCTURE = new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "space_station");

    public static void onServerStart(MinecraftServer server) {
        ServerWorld world = server.getWorld(EARTH_ORBIT);
        if(world == null) {
            return;
        }

        StructureTemplate template = server.getStructureTemplateManager().getTemplateOrBlank(START_STRUCTURE);
        template.place(
                world,
                new BlockPos(-34, -1, -34),
                new BlockPos(-34,-1,-34),
                new StructurePlacementData(),
                world.random,
                Block.NOTIFY_ALL
        );
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {

    }
}

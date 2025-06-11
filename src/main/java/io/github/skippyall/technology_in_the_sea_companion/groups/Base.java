package io.github.skippyall.technology_in_the_sea_companion.groups;

import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Set;

public class Base {
    public static final Identifier BASE_STRUCTURE = new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "base");

    BlockPos pos;
    boolean exist;

    public Base(BlockPos pos, boolean exist) {
        this.pos = pos;
        this.exist = exist;
    }

    public void createIfNotExist(MinecraftServer server) {
        if (!exist) {
            ServerWorld world = server.getWorld(World.OVERWORLD);
            if(world == null) {
                return;
            }

            int y = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
            pos = new BlockPos(pos.getX(), y, pos.getZ());

            StructureTemplate template = server.getStructureTemplateManager().getTemplateOrBlank(BASE_STRUCTURE);
            template.place(
                    world,
                    pos.add(-6, -1, -6),
                    new BlockPos(0, 0, 0),
                    new StructurePlacementData(),
                    world.random,
                    Block.NOTIFY_ALL
            );

            exist = true;
        }
    }

    public void teleport(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        ServerWorld world = server.getWorld(World.OVERWORLD);
        world.getSpawnPos()
        if(world == null) {
            player.sendMessage(Text.literal("Error while teleporting: Can't find the overworld").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return;
        }

        createIfNotExist(server);
        player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), 0, 0);
    }
}

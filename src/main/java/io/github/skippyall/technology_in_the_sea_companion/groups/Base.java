package io.github.skippyall.technology_in_the_sea_companion.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Set;

public class Base {
    public static final Identifier BASE_STRUCTURE = new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "base");
    public static final Codec<Base> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(Base::getPos),
                    Codec.BOOL.fieldOf("exist").forGetter(Base::exits)
            ).apply(instance, Base::new)
    );

    BlockPos pos;
    boolean exist;

    public Base(BlockPos pos, boolean exist) {
        this.pos = pos;
        this.exist = exist;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean exits() {
        return exist;
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
        ServerWorld world = server.getOverworld();

        createIfNotExist(server);
        player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), 0, 0);
    }
}

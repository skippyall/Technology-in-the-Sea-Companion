package io.github.skippyall.technology_in_the_sea_companion;

import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import io.github.skippyall.technology_in_the_sea_companion.groups.GroupManager;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class StartManager {
    public static final Identifier START_STRUCTURE = new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "space_station");

    public static final BlockPos PLAYER_START_POS = new BlockPos(0, 65,0);
    public static final BlockPos STRUCTURE_POS = new BlockPos(0, 60, 0);
    
    public static final BlockPos SIGN_POS = new BlockPos(0, 65, 2);
    public static final Box GROUP_ROOM = new Box(-7, 63, 8, 7, 65, 0);

    public static void onServerStart(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        if(world == null) {
            return;
        }

        StructureTemplate template = server.getStructureTemplateManager().getTemplateOrBlank(START_STRUCTURE);
        template.place(
                world,
                STRUCTURE_POS,
                PLAYER_START_POS,
                new StructurePlacementData(),
                world.random,
                Block.NOTIFY_ALL
        );
    }

    public static void formGroup(ServerWorld world) {
        BlockEntity sign = world.getBlockEntity(SIGN_POS);
        if(!(sign instanceof SignBlockEntity signBe)) {
            world.getServer().sendMessage(Text.literal("404 Sign not found"));
            return;
        }
        SignText text = signBe.getText(true);
        Text message = text.getMessage(1, false);
        String string = message.getString();

        if(string.isBlank()) {
            world.getServer().sendMessage(Text.literal("Please write your group's name on the sign."));
            return;
        }

        Group group = GroupManager.getGroup(world.getServer(), string).orElseGet(() -> GroupManager.createGroup(world.getServer(), string));
        group.getBase().createIfNotExist(world.getServer());

        List<Entity> players = world.getOtherEntities(null, GROUP_ROOM, entity -> entity instanceof ServerPlayerEntity);

        for(Entity e : players) {
            ServerPlayerEntity player = (ServerPlayerEntity) e;
            group.join(player);
            group.getBase().teleport(player);
        }
    }
}

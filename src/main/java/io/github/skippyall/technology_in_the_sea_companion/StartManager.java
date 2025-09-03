package io.github.skippyall.technology_in_the_sea_companion;

import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.claims.admin.flags.BooleanFlag;
import earth.terrarium.cadmus.api.claims.admin.flags.ComponentFlag;
import earth.terrarium.cadmus.common.claims.AdminClaimHandler;
import earth.terrarium.cadmus.common.claims.ClaimHandler;
import earth.terrarium.cadmus.common.claims.admin.ModFlags;
import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import io.github.skippyall.technology_in_the_sea_companion.groups.GroupManager;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.List;

public class StartManager {
    public static final Identifier START_STRUCTURE = new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "ship");

    public static final String CLAIM_ID = "spawn";
    public static final ChunkPos[] SPAWN_CLAIM_POS = {new ChunkPos(0,0), new ChunkPos(-1,0), new ChunkPos(0,-1), new ChunkPos(-1,-1)};

    public static final BlockPos PLAYER_START_POS = new BlockPos(0, 66,0);
    public static final BlockPos STRUCTURE_POS = new BlockPos(-7, 60, -16);
    
    public static final BlockPos SIGN_POS = new BlockPos(-2, 63, -14);
    public static final Box GROUP_ROOM = new Box(-5, 62, -15, 5, 65, -9);

    public static void onServerStart(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        if(world == null) {
            return;
        }

        WorldAttachment attachment = WorldAttachment.getInstance(server);
        if(attachment.isShipPlaced()) {
            return;
        }

        StructureTemplate template = server.getStructureTemplateManager().getTemplateOrBlank(START_STRUCTURE);
        template.place(
                world,
                STRUCTURE_POS,
                PLAYER_START_POS,
                new StructurePlacementData().setPlaceFluids(false),
                world.random,
                Block.NOTIFY_ALL
        );

        attachment.setShipPlaced();

        AdminClaimHandler.create(server, CLAIM_ID, new HashMap<>());
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.DISPLAY_NAME, new ComponentFlag(Text.literal("Spawn")));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.BLOCK_BREAK, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.BLOCK_EXPLOSIONS, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.BLOCK_PLACE, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.ENTITY_DAMAGE, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.ENTITY_INTERACTIONS, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.USE_VEHICLES, new BooleanFlag(false));
        AdminClaimHandler.setFlag(server, CLAIM_ID, ModFlags.CREATURE_SPAWNING, new BooleanFlag(false));

        for(ChunkPos chunkPos : SPAWN_CLAIM_POS) {
            ClaimApi.API.claim(world, chunkPos, ClaimHandler.ADMIN_PREFIX + CLAIM_ID, false);
        }
    }

    public static void formGroup(ServerWorld world) {
        PlayerManager playerManager = world.getServer().getPlayerManager();
        try {
            List<ServerPlayerEntity> players = world.getEntitiesByClass(ServerPlayerEntity.class, GROUP_ROOM, player -> true);
            for(ServerPlayerEntity player : players) {
                playerManager.broadcast(player.getName(), false);
            }
            if(players.isEmpty()) {
                playerManager.broadcast(Text.literal("There is nobody in this room"), false);
            }

            playerManager.broadcast(Text.literal("Forming Group"), false);

            BlockEntity sign = world.getBlockEntity(SIGN_POS);
            if (!(sign instanceof SignBlockEntity signBe)) {
                playerManager.broadcast(Text.literal("404 Sign not found"), false);
                return;
            }
            SignText text = signBe.getText(true);
            Text message = text.getMessage(0, false);
            String string = message.getString();

            if (string.isBlank()) {
                playerManager.broadcast(Text.literal("Write your group's name on the sign."), false);
                return;
            }

            Group group = GroupManager.getGroup(world.getServer(), string).orElseGet(() -> GroupManager.createGroup(world.getServer(), string));
            group.getBase().createIfNotExist(world.getServer());

            group.join(players);

            playerManager.broadcast(Text.literal("Group formed"), false);
        } catch (Exception e) {
            TechnologyInTheSeaCompanion.LOGGER.error("Exception while forming group:", e);
            playerManager.broadcast(Text.literal(e.toString()), false);
        }
    }
}

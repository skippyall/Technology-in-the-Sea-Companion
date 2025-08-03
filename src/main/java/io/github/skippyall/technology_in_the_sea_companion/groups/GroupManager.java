package io.github.skippyall.technology_in_the_sea_companion.groups;

import io.github.skippyall.technology_in_the_sea_companion.WorldAttachment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class GroupManager {
    public static Optional<Group> getGroup(ServerPlayerEntity player) {
        for (Group group : getGroups(player.getServer())) {
            if (group.contains(player)) {
                return Optional.of(group);
            }
        }

        return Optional.empty();
    }

    public static Optional<Group> getGroup(MinecraftServer server, String name) {
        for (Group group : getGroups(server)) {
            if (group.getName().equals(name)) {
                return Optional.of(group);
            }
        }

        return Optional.empty();
    }

    public static List<Group> getGroups(MinecraftServer server) {
        return WorldAttachment.getInstance(server).getGroups();
    }

    public static Group createGroup(MinecraftServer server, String name) {
        BlockPos pos = getNextStep(server);

        Group group = new Group(name, new Base(pos, false));
        getGroups(server).add(group);
        return group;
    }

    public static BlockPos getNextStep(MinecraftServer server) {
        WorldAttachment attachment = WorldAttachment.getInstance(server);
        int stepX = attachment.getStepX();
        int stepY = attachment.getStepY();

        BlockPos pos = new BlockPos(stepX, 0, stepY);

        if (stepX >= 0 && stepY < 0) {
            stepX++;
            stepY++;
        }else if (stepX > 0 && stepY >= 0) {
            stepX--;
            stepY++;
        }else if (stepX <= 0 && stepY > 0) {
            stepX--;
            stepY--;
        }else if (stepX < 0 && stepY <= 0) {
            stepX++;
            stepY--;

            if (stepX == 0) {
                stepY--;
            }
        }

        attachment.setStepX(stepX);
        attachment.setStepY(stepY);

        return pos;
    }
}

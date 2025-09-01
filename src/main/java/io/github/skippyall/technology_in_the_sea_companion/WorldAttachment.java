package io.github.skippyall.technology_in_the_sea_companion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class WorldAttachment {
    public static final Codec<WorldAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Group.CODEC.listOf().fieldOf("groups").forGetter(WorldAttachment::getGroups),
                Codec.INT.optionalFieldOf("stepX", 0).forGetter(WorldAttachment::getStepX),
                Codec.INT.optionalFieldOf("stepY", -1).forGetter(WorldAttachment::getStepY),
                Codec.BOOL.optionalFieldOf("shipPlaced", false).forGetter(WorldAttachment::isShipPlaced)
            ).apply(instance, WorldAttachment::new)
    );

    public static final AttachmentType<WorldAttachment> ATTACHMENT_TYPE = AttachmentRegistry.<WorldAttachment>builder()
            .initializer(WorldAttachment::new)
            .persistent(CODEC)
            .buildAndRegister(Identifier.of(TechnologyInTheSeaCompanion.MOD_ID, "world_attachment"));

    private final List<Group> groups;
    private int stepX, stepY;
    private boolean shipPlaced = false;

    public WorldAttachment() {
        groups = new ArrayList<>();
        this.stepX = 0;
        this.stepY = -1;
    }

    public WorldAttachment(List<Group> groups, int stepX, int stepY, boolean shipPlaced) {
        this.groups = new ArrayList<>(groups);
        this.stepX = stepX;
        this.stepY = stepY;
        this.shipPlaced = shipPlaced;
    }

    public static void register() {}

    public static WorldAttachment getInstance(MinecraftServer server) {
        return server.getOverworld().getAttachedOrCreate(ATTACHMENT_TYPE);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public int getStepX() {
        return stepX;
    }

    public int getStepY() {
        return stepY;
    }

    public void setStepX(int stepX) {
        this.stepX = stepX;
    }

    public void setStepY(int stepY) {
        this.stepY = stepY;
    }

    public boolean isShipPlaced() {
        return shipPlaced;
    }

    public void setShipPlaced() {
        shipPlaced = true;
    }
}

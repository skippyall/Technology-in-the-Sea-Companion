package io.github.skippyall.technology_in_the_sea_companion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class WorldAttachment {
    public static final Codec<WorldAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(

            )
    )

    public static final AttachmentType<WorldAttachment> ATTACHMENT_TYPE = AttachmentRegistry.<WorldAttachment>builder()
            .initializer(WorldAttachment::new)
            .buildAndRegister(Identifier.of(TechnologyInTheSeaCompanion.MOD_ID, "world_attachment"));

    private final List<Group> groups;

    public WorldAttachment() {
        groups = new ArrayList<>();
    }
}

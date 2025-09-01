package io.github.skippyall.technology_in_the_sea_companion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
public class PlayerAttachment {
    public static final Codec<PlayerAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.fieldOf("teleportedToBase").forGetter(a -> a.teleportedToBase)
            ).apply(instance, PlayerAttachment::new)
    );

    public static final AttachmentType<PlayerAttachment> ATTACHMENT_TYPE = AttachmentRegistry.<PlayerAttachment>builder()
            .copyOnDeath()
            .persistent(CODEC)
            .initializer(PlayerAttachment::new)
            .buildAndRegister(new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "player_attachment"));

    public boolean teleportedToBase = false;

    public PlayerAttachment() {

    }

    public PlayerAttachment(boolean teleportedToBase) {
        this.teleportedToBase = teleportedToBase;
    }

    public static PlayerAttachment get(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ATTACHMENT_TYPE);
    }
}

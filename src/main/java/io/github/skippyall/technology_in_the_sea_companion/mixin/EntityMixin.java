package io.github.skippyall.technology_in_the_sea_companion.mixin;

import io.github.skippyall.technology_in_the_sea_companion.PlayerAttachment;
import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import io.github.skippyall.technology_in_the_sea_companion.groups.GroupManager;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setPose", at = @At("HEAD"))
    private void teleportToBase(EntityPose pose, CallbackInfo ci) {
        if(pose == EntityPose.SWIMMING && ((Object)this) instanceof ServerPlayerEntity player) {
            PlayerAttachment attachment = PlayerAttachment.get(player);
            if(!attachment.teleportedToBase) {
                Optional<Group> group = GroupManager.getGroup(player);
                if (group.isPresent()) {
                    player.server.send(new ServerTask(player.server.getTicks(), () -> {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100));
                        group.get().getBase().teleport(player);

                    }));
                }
                attachment.teleportedToBase = true;
            }
        }
    }
}

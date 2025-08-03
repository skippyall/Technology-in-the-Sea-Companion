package io.github.skippyall.technology_in_the_sea_companion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.technology_in_the_sea_companion.StartManager;
import io.github.skippyall.technology_in_the_sea_companion.groups.Group;
import io.github.skippyall.technology_in_the_sea_companion.groups.GroupManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @ModifyExpressionValue(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/DimensionType;hasSkyLight()Z"))
    private boolean alwaysRespawnExact(boolean original) {
        return false;
    }

    @WrapOperation(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(Lnet/minecraft/util/math/BlockPos;FF)V"))
    private void moveToModifiedSpawn(ServerPlayerEntity instance, BlockPos blockPos, float yaw, float pitch, Operation<Void> original) {
        Optional<Group> optionalGroup = GroupManager.getGroup(instance);
        if(optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            original.call(instance, group.getBase().getPos(), yaw, pitch);
        } else {
            original.call(instance, StartManager.PLAYER_START_POS, yaw, pitch);
        }
    }
}

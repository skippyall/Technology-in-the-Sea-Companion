package io.github.skippyall.technology_in_the_sea_companion.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    private void moveToModifiedSpawn(ServerWorld world, CallbackInfo ci) {
        
    }
}

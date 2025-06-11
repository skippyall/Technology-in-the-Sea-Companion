package io.github.skippyall.technology_in_the_sea_companion.mixin;

import io.github.skippyall.technology_in_the_sea_companion.StartManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        StartManager.onPlayerJoin(player);
    }
}

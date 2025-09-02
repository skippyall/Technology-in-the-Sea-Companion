package io.github.skippyall.technology_in_the_sea_companion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow
    @Deprecated
    public abstract RegistryEntry.Reference<Block> getRegistryEntry();

    @ModifyExpressionValue(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean disableOreExperienceDrop(boolean original) {
        if(getRegistryEntry().isIn(TechnologyInTheSeaCompanion.DISABLE_EXPERIENCE_DROP)) {
            return false;
        }
        return original;
    }
}

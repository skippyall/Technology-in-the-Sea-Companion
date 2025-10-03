package io.github.skippyall.technology_in_the_sea_companion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.botania.common.block.FakeAirBlock;

@Mixin(FakeAirBlock.class)
public class FakeAirBlockMixin {
    @WrapOperation(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int noWater(Random instance, int i, Operation<Integer> original) {
        return 1;
    }
}

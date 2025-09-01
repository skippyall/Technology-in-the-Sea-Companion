package io.github.skippyall.technology_in_the_sea_companion.sun;

import com.google.common.base.Suppliers;
import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class SunscreenItem extends Item {
    public static final Supplier<Fluid> SUNSCREEN = Suppliers.memoize(() -> Registries.FLUID.get(Identifier.of(TechnologyInTheSeaCompanion.MOD_ID, "sunscreen")));
    public static final long SUNSCREEN_USE_PER_TICK = FluidConstants.BUCKET / 200;

    public SunscreenItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(canUse(user, hand)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(user.getStackInHand(hand));
        } else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    public boolean canUse(LivingEntity user, Hand hand) {
        if(user instanceof PlayerEntity player) {
            StatusEffectInstance effect = user.getStatusEffect(SunManager.SUN_PROTECTION_EFFECT);
            if(effect == null || effect.isDurationBelow(5 * 60 * 20)) {
                ContainerItemContext context = ContainerItemContext.forPlayerInteraction(player, hand);
                Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);

                if(storage != null) {
                    try (Transaction t = Transaction.openOuter()) {
                        long extracted = storage.extract(FluidVariant.of(SUNSCREEN.get()), SUNSCREEN_USE_PER_TICK, t);

                        if (extracted == SUNSCREEN_USE_PER_TICK) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if(user instanceof PlayerEntity player) {
            StatusEffectInstance effect = user.getStatusEffect(SunManager.SUN_PROTECTION_EFFECT);
            if (effect == null || effect.isDurationBelow(5 * 60 * 20)) {
                ContainerItemContext context = ContainerItemContext.forPlayerInteraction(player, user.getActiveHand());
                Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);

                try(Transaction t = Transaction.openOuter()) {
                    long extracted = storage.extract(FluidVariant.of(SUNSCREEN.get()), SUNSCREEN_USE_PER_TICK, t);

                    if(extracted == SUNSCREEN_USE_PER_TICK) {
                        t.commit();
                        user.addStatusEffect(new StatusEffectInstance(SunManager.SUN_PROTECTION_EFFECT, (effect != null ? effect.getDuration() : 0) + 3 * 20));
                        return;
                    }
                }
            }
        }
        user.stopUsingItem();
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public static void registerStorage() {
        FluidStorage.ITEM.registerForItems(SunscreenStorage::new, SunManager.SUNSCREEN);
    }

    public static class SunscreenStorage extends SingleFluidStorage {
        private ContainerItemContext context;
        private ItemVariant itemVariant;

        public SunscreenStorage(ItemStack stack, ContainerItemContext context) {
            this.context = context;
            this.itemVariant = context.getItemVariant();
            NbtCompound subNbt = stack.getSubNbt("sunscreenStorage");
            if(subNbt != null) {
                readNbt(subNbt);
            }
        }

        public void exchangeItem(TransactionContext transaction) {
            NbtCompound nbt = itemVariant.copyOrCreateNbt();
            NbtCompound sunscreenStorage = new NbtCompound();
            writeNbt(sunscreenStorage);
            nbt.put("sunscreenStorage", sunscreenStorage);
            context.exchange(ItemVariant.of(itemVariant.getItem(), nbt), 1, transaction);
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            long inserted = super.insert(insertedVariant, maxAmount, transaction);
            if(inserted > 0) {
                exchangeItem(transaction);
            }
            return inserted;
        }

        @Override
        public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
            long extracted = super.extract(extractedVariant, maxAmount, transaction);
            if(extracted > 0) {
                exchangeItem(transaction);
            }
            return extracted;
        }

        @Override
        protected boolean canInsert(FluidVariant variant) {
            return variant.isOf(SUNSCREEN.get());
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET;
        }
    }
}

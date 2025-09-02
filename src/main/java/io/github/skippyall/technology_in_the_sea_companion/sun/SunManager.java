package io.github.skippyall.technology_in_the_sea_companion.sun;

import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.MapColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.LightType;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;

import java.util.Optional;

public class SunManager {
    public static final StatusEffect SUN_PROTECTION_EFFECT = Registry.register(Registries.STATUS_EFFECT, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "sun_protection"), new StatusEffect(StatusEffectCategory.BENEFICIAL, MapColor.YELLOW.color){});
    public static final Enchantment SUN_PROTECTION_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "sun_protection"), new SunProtectionEnchantment());

    public static final Item SUNSCREEN = Registry.register(Registries.ITEM, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "sunscreen"), new SunscreenItem());

    public static final TagKey<Item> SUN_PROTECTION_ARMOR = TagKey.of(RegistryKeys.ITEM, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "sun_protection_armor"));
    public static final RegistryKey<DamageType> SUN_DAMAGE_TYPE_KEY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, "sun"));

    private static int ticks = 0;

    public static void tick(ServerWorld world) {
        if(ticks % 20 == 0 && world.getRegistryKey().equals(World.OVERWORLD)) {
            Optional<RegistryEntry.Reference<DamageType>> SUN_DAMAGE_TYPE = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(SUN_DAMAGE_TYPE_KEY);
            if(SUN_DAMAGE_TYPE.isPresent()) {
                for (PlayerEntity player : world.getPlayers()) {
                    if (world.getLightLevel(LightType.SKY, player.getBlockPos()) - world.getAmbientDarkness() > 10 && !isProtected(player)) {
                        player.damage(new DamageSource(SUN_DAMAGE_TYPE.get()), 1);
                    }
                }
            }
        }
        ticks++;
    }

    public static boolean isProtected(PlayerEntity player) {
        if(player.hasStatusEffect(SUN_PROTECTION_EFFECT)) {
            return true;
        }

        for (ItemStack armorItem : player.getArmorItems()) {
            if(!(armorItem.isIn(SUN_PROTECTION_ARMOR) || EnchantmentHelper.getLevel(SUN_PROTECTION_ENCHANTMENT, armorItem) > 0)) {
                return false;
            }
        }
        return true;
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(SunManager::tick);
        SunscreenItem.registerStorage();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((entries) -> {
            entries.add(new ItemStack(SUNSCREEN));
        });
    }
}

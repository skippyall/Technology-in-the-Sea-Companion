package io.github.skippyall.technology_in_the_sea_companion.veins;

import com.tom.createores.OreDataCapability;
import io.github.skippyall.technology_in_the_sea_companion.TechnologyInTheSeaCompanion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.function.Predicate;

public class VeinCreatorItem extends Item {
    public static final VeinCreatorItem ZINC_VEIN_CREATOR = register("zinc_vein_creator", new Identifier("createoreexcavation", "ore_vein_type/zinc"));
    public static final VeinCreatorItem REDSTONE_VEIN_CREATOR = register("redstone_vein_creator", new Identifier("createoreexcavation", "ore_vein_type/redstone"));
    public static final VeinCreatorItem NETHERITE_VEIN_CREATOR = register("netherite_vein_creator", new Identifier("createoreexcavation", "ore_vein_type/netherite"), user -> user.getWorld().getDimensionKey().getValue().equals(DimensionTypes.THE_NETHER_ID));
    public static final VeinCreatorItem OIL_VEIN_CREATOR = register("oil_vein_creator", new Identifier("technologyinthesea", "oil_vein"));
    public static final VeinCreatorItem DESH_VEIN_CREATOR = register("desh_vein_creator", new Identifier("technologyinthesea", "desh_vein"), user -> user.getWorld().getDimensionKey().getValue().equals(new Identifier("ad_astra", "moon")));
    public static final VeinCreatorItem OSTRUM_VEIN_CREATOR = register("ostrum_vein_creator", new Identifier("technologyinthesea", "ostrum_vein"), user -> user.getWorld().getDimensionKey().getValue().equals(new Identifier("ad_astra", "mars")));
    public static final VeinCreatorItem CALORITE_VEIN_CREATOR = register("calorite_vein_creator", new Identifier("technologyinthesea", "calorite_vein"), user -> {
        Identifier id = user.getWorld().getDimensionKey().getValue();
        return id.equals(new Identifier("ad_astra", "mercury")) || id.equals(new Identifier("ad_astra", "venus"));
    });

    private final Identifier recipe;
    private final Predicate<PlayerEntity> predicate;

    public VeinCreatorItem(Identifier recipe, Predicate<PlayerEntity> predicate) {
        super(new Settings());
        this.recipe = recipe;
        this.predicate = predicate;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) {
            if (predicate.test(user)) {
                ItemStack stack = user.getStackInHand(hand);
                stack.decrement(1);

                OreDataCapability.OreData data = OreDataCapability.getData(world.getWorldChunk(user.getBlockPos()));
                data.setRecipe(recipe);
                data.setLoaded(true);
                data.setRandomMul(0.8F);
                data.setExtractedAmount(0);
                return TypedActionResult.success(stack);
            } else {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }
        } else {
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
    }

    public static VeinCreatorItem register(String id, Identifier oreVein) {
        return Registry.register(Registries.ITEM, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, id), new VeinCreatorItem(oreVein, player -> true));
    }

    public static VeinCreatorItem register(String id, Identifier oreVein, Predicate<PlayerEntity> predicate) {
        return Registry.register(Registries.ITEM, new Identifier(TechnologyInTheSeaCompanion.MOD_ID, id), new VeinCreatorItem(oreVein, predicate));
    }

    public static void register() {

    }
}

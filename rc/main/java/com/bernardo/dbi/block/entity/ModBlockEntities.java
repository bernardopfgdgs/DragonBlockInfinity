ackage com.bernardo.dbi.block.entity;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.block.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registra todas as entidades de bloco (BlockEntities) do mod.
 */
public class ModBlockEntities {



    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
        ForgeRegistries.BLOCK_ENTITY_TYPES,
        Dbi.MOD_ID
    );

    public static final RegistryObject<BlockEntityType<DragonBlockEntity>> DRAGON_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "dragon_block",
        () -> BlockEntityType.Builder.of(
            DragonBlockEntity::new,
            // Registra a entidade para todos os dragon blocks
            BlockRegistry.DRAGON_BLOCK_1_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_2_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_3_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_4_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_5_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_6_EARTH.get(),
            BlockRegistry.DRAGON_BLOCK_7_EARTH.get(),
            BlockRegistry.NAMEK_GRASS.get(),
            BlockRegistry.NAMEK_DIRTY.get(),
            BlockRegistry.DIRTY_STONE_COBBLESTONE.get()
        ).build(null)
    );
}

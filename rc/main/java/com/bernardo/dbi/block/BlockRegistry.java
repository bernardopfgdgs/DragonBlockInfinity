ackage com.bernardo.dbi.block;

import com.bernardo.dbi.Dbi;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registra todos os blocos Dragon Block do mod.
 */
public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
        ForgeRegistries.BLOCKS,
        Dbi.MOD_ID
    );

    // ===== Dragon Blocks =====
    public static final RegistryObject<Block> DRAGON_BLOCK_1_EARTH = BLOCKS.register(
        "dragon_block_1_earth",
        DragonBlock1Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_2_EARTH = BLOCKS.register(
        "dragon_block_2_earth",
        DragonBlock2Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_3_EARTH = BLOCKS.register(
        "dragon_block_3_earth",
        DragonBlock3Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_4_EARTH = BLOCKS.register(
        "dragon_block_4_earth",
        DragonBlock4Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_5_EARTH = BLOCKS.register(
        "dragon_block_5_earth",
        DragonBlock5Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_6_EARTH = BLOCKS.register(
        "dragon_block_6_earth",
        DragonBlock6Earth::new
    );

    public static final RegistryObject<Block> DRAGON_BLOCK_7_EARTH = BLOCKS.register(
        "dragon_block_7_earth",
        DragonBlock7Earth::new
    );

    // ===== Outros Blocos =====
    public static final RegistryObject<Block> NAMEK_GRASS = BLOCKS.register(
        "namek_grass",
        NamekGrass::new
    );

    public static final RegistryObject<Block> NAMEK_DIRTY = BLOCKS.register(
        "namek_dirty",
        NamekDirty::new
    );

    public static final RegistryObject<Block> DIRTY_STONE_COBBLESTONE = BLOCKS.register(
        "dirty_stone_cobblestone",
        DirtyStoneCobblestone::new
    );
}

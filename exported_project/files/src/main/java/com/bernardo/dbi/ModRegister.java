package com.bernardo.dbi;

import com.bernardo.dbi.block.BlockRegistry;
import com.bernardo.dbi.block.entity.ModBlockEntities;
import com.bernardo.dbi.race.RaceRegistry;
import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.capability.PlayerRaceCap;
import com.bernardo.dbi.capability.PlayerStatusCap;
import com.bernardo.dbi.style.StyleRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModRegister {

    public static void registerAll() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registrar blocos
        BlockRegistry.BLOCKS.register(modEventBus);
        
        // Registrar entidades de bloco
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // Registrar sistemas existentes
        RaceRegistry.registerAll();
        StyleRegistry.registerAll();

        ModNetwork.register(modEventBus);
        PlayerRaceCap.register(modEventBus);
        PlayerStatusCap.register(modEventBus);
    }
}

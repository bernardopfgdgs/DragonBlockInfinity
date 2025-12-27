package com.bernardo.dbi;

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

        // Registrar raças
        RaceRegistry.registerAll();

        // Registrar estilos de luta
        StyleRegistry.registerAll();

        // Registrar rede
        ModNetwork.register(modEventBus);

        // Registrar capabilities
        PlayerRaceCap.register(modEventBus);
        PlayerStatusCap.register(modEventBus);

        // Adicionar outros registros aqui conforme necessário
    }
}
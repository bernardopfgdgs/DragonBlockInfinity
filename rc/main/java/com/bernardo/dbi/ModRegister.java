package com.bernardo.dbi;

import com.bernardo.dbi.capability.PlayerRaceCap;
import com.bernardo.dbi.network.ModNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

public class ModRegister {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModRegister::commonSetup);
        modEventBus.addListener(ModRegister::registerCapabilities);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.register();
        });
    }

    private static void registerCapabilities(RegisterEvent event) {
        // Capabilities são registradas via RegisterCapabilitiesEvent no Forge 1.20.1
        // Isso é feito automaticamente pelo CapabilityToken no PlayerRaceCap
    }
}

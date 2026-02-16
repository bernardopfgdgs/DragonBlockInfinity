package com.bernardo.dbi.network;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";
    private static int id = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Dbi.MOD_ID, "main"),
        () -> PROTOCOL,
        PROTOCOL::equals,
        PROTOCOL::equals
    );

    public static void register(IEventBus modEventBus) {
        CHANNEL.registerMessage(
            id++,
            RaceSelectionPacket.class,
            RaceSelectionPacket::encode,
            RaceSelectionPacket::decode,
            RaceSelectionPacket::handle
        );
    }

    /** Envia um pacote do cliente para o servidor. */
    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
}

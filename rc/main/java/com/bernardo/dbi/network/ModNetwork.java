ackage com.bernardo.dbi.network;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import com.bernardo.dbi.network.packet.SyncRaceCapPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
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

    public static void register() {
        CHANNEL.registerMessage(id++,
            RaceSelectionPacket.class,
            RaceSelectionPacket::encode,
            RaceSelectionPacket::decode,
            RaceSelectionPacket::handle
        );
        CHANNEL.registerMessage(id++,
            SyncRaceCapPacket.class,
            SyncRaceCapPacket::encode,
            SyncRaceCapPacket::decode,
            SyncRaceCapPacket::handle
        );
    }

    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
}

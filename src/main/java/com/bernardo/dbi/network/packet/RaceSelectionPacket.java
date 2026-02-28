package com.bernardo.dbi.network.packet;

import com.bernardo.dbi.capability.PlayerRaceCap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RaceSelectionPacket {

    private final String race;
    private final int formIdx, hairStyle, ageIdx, bodyTypeIdx;
    private final int noseIdx, mouthIdx, eyeIdx;
    private final int bodyColor, hairColor, eyeColor;

    public RaceSelectionPacket(String race, int formIdx, int hairStyle, int ageIdx,
                                int bodyTypeIdx, int noseIdx, int mouthIdx, int eyeIdx,
                                int bodyColor, int hairColor, int eyeColor) {
        this.race = race;
        this.formIdx = formIdx;
        this.hairStyle = hairStyle;
        this.ageIdx = ageIdx;
        this.bodyTypeIdx = bodyTypeIdx;
        this.noseIdx = noseIdx;
        this.mouthIdx = mouthIdx;
        this.eyeIdx = eyeIdx;
        this.bodyColor = bodyColor;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
    }

    public static void encode(RaceSelectionPacket p, FriendlyByteBuf buf) {
        buf.writeUtf(p.race);
        buf.writeInt(p.formIdx);
        buf.writeInt(p.hairStyle);
        buf.writeInt(p.ageIdx);
        buf.writeInt(p.bodyTypeIdx);
        buf.writeInt(p.noseIdx);
        buf.writeInt(p.mouthIdx);
        buf.writeInt(p.eyeIdx);
        buf.writeInt(p.bodyColor);
        buf.writeInt(p.hairColor);
        buf.writeInt(p.eyeColor);
    }

    public static RaceSelectionPacket decode(FriendlyByteBuf buf) {
        return new RaceSelectionPacket(
            buf.readUtf(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt()
        );
    }

    public static void handle(RaceSelectionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            
            // Gera raceDisplay a partir do race ID
            String raceDisplay = getRaceDisplay(pkt.race);
            
            PlayerRaceCap.applyAndSync(player,
                pkt.race, raceDisplay,
                pkt.formIdx, pkt.hairStyle, pkt.ageIdx, pkt.bodyTypeIdx,
                pkt.noseIdx, pkt.mouthIdx, pkt.eyeIdx,
                pkt.bodyColor, pkt.hairColor, pkt.eyeColor);
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static String getRaceDisplay(String raceId) {
        switch (raceId) {
            case "sayajin": return "Saiyan";
            case "namekian": return "Namekian";
            case "arconsian": return "Arcosian";
            case "humano": return "Human";
            case "halfsayajin": return "Half-Saiyan";
            default: return raceId;
        }
    }
}

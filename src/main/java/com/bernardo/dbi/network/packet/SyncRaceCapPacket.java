package com.bernardo.dbi.network.packet;

import com.bernardo.dbi.capability.PlayerRaceCap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncRaceCapPacket {

    private final String race, raceDisplay;
    private final int formIdx, hairStyle, ageIdx, bodyTypeIdx;
    private final int noseIdx, mouthIdx, eyeIdx;
    private final int bodyColor, hairColor, eyeColor;

    public SyncRaceCapPacket(PlayerRaceCap cap) {
        this.race = cap.getRace();
        this.raceDisplay = cap.getRaceDisplay();
        this.formIdx = cap.getFormIdx();
        this.hairStyle = cap.getHairStyle();
        this.ageIdx = cap.getAgeIdx();
        this.bodyTypeIdx = cap.getBodyTypeIdx();
        this.noseIdx = cap.getNoseIdx();
        this.mouthIdx = cap.getMouthIdx();
        this.eyeIdx = cap.getEyeIdx();
        this.bodyColor = cap.getBodyColor();
        this.hairColor = cap.getHairColor();
        this.eyeColor = cap.getEyeColor();
    }

    private SyncRaceCapPacket(String race, String raceDisplay,
                               int formIdx, int hairStyle, int ageIdx, int bodyTypeIdx,
                               int noseIdx, int mouthIdx, int eyeIdx,
                               int bodyColor, int hairColor, int eyeColor) {
        this.race = race;
        this.raceDisplay = raceDisplay;
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

    public static void encode(SyncRaceCapPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.race);
        buf.writeUtf(pkt.raceDisplay);
        buf.writeInt(pkt.formIdx);
        buf.writeInt(pkt.hairStyle);
        buf.writeInt(pkt.ageIdx);
        buf.writeInt(pkt.bodyTypeIdx);
        buf.writeInt(pkt.noseIdx);
        buf.writeInt(pkt.mouthIdx);
        buf.writeInt(pkt.eyeIdx);
        buf.writeInt(pkt.bodyColor);
        buf.writeInt(pkt.hairColor);
        buf.writeInt(pkt.eyeColor);
    }

    public static SyncRaceCapPacket decode(FriendlyByteBuf buf) {
        return new SyncRaceCapPacket(
            buf.readUtf(), buf.readUtf(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt()
        );
    }

    public static void handle(SyncRaceCapPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            mc.player.getCapability(PlayerRaceCap.RACE_CAP).ifPresent(cap ->
                cap.setAll(pkt.race, pkt.raceDisplay,
                    pkt.formIdx, pkt.hairStyle, pkt.ageIdx, pkt.bodyTypeIdx,
                    pkt.noseIdx, pkt.mouthIdx, pkt.eyeIdx,
                    pkt.bodyColor, pkt.hairColor, pkt.eyeColor)
            );

            var nbt = mc.player.getPersistentData();
            nbt.putString("dbi:race", pkt.race);
            nbt.putInt("dbi:bodyColor", pkt.bodyColor);
            nbt.putInt("dbi:hairColor", pkt.hairColor);
            nbt.putInt("dbi:eyeColor", pkt.eyeColor);
        });
        ctx.get().setPacketHandled(true);
    }
}

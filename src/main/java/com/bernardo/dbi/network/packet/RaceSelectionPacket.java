package com.bernardo.dbi.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Pacote enviado do cliente → servidor quando o player confirma a seleção de aparência.
 * O servidor salva tudo no PersistentData do player.
 */
public class RaceSelectionPacket {

    private final String race;
    private final int formIdx, hairStyle, ageIdx, bodyTypeIdx;
    private final int noseIdx, mouthIdx, eyeIdx;
    private final int bodyColor, hairColor, eyeColor;

    public RaceSelectionPacket(String race, int formIdx, int hairStyle, int ageIdx,
                               int bodyTypeIdx, int noseIdx, int mouthIdx, int eyeIdx,
                               int bodyColor, int hairColor, int eyeColor) {
        this.race        = race;
        this.formIdx     = formIdx;
        this.hairStyle   = hairStyle;
        this.ageIdx      = ageIdx;
        this.bodyTypeIdx = bodyTypeIdx;
        this.noseIdx     = noseIdx;
        this.mouthIdx    = mouthIdx;
        this.eyeIdx      = eyeIdx;
        this.bodyColor   = bodyColor;
        this.hairColor   = hairColor;
        this.eyeColor    = eyeColor;
    }

    // ── Encode / Decode ──────────────────────────────────────

    public static void encode(RaceSelectionPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.race);
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

    public static RaceSelectionPacket decode(FriendlyByteBuf buf) {
        return new RaceSelectionPacket(
            buf.readUtf(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt()
        );
    }

    // ── Handler (executa no lado do servidor) ─────────────────

    public static void handle(RaceSelectionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            var nbt = player.getPersistentData();
            nbt.putString("dbi:race",        pkt.race);
            nbt.putInt   ("dbi:formIdx",      pkt.formIdx);
            nbt.putInt   ("dbi:hairStyle",    pkt.hairStyle);
            nbt.putInt   ("dbi:ageIdx",       pkt.ageIdx);
            nbt.putInt   ("dbi:bodyTypeIdx",  pkt.bodyTypeIdx);
            nbt.putInt   ("dbi:noseIdx",      pkt.noseIdx);
            nbt.putInt   ("dbi:mouthIdx",     pkt.mouthIdx);
            nbt.putInt   ("dbi:eyeIdx",       pkt.eyeIdx);
            nbt.putInt   ("dbi:bodyColor",    pkt.bodyColor);
            nbt.putInt   ("dbi:hairColor",    pkt.hairColor);
            nbt.putInt   ("dbi:eyeColor",     pkt.eyeColor);
            // Sincroniza de volta ao cliente
            player.refreshDisplayName();
        });
        ctx.get().setPacketHandled(true);
    }
}

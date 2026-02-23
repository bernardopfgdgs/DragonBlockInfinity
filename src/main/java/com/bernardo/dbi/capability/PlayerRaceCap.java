package com.bernardo.dbi.capability;

import net.minecraftforge.eventbus.api.IEventBus;

public class PlayerRaceCap {
    // raça + mastery

    public static void register(IEventBus modEventBus) {
        // Registrar capability aqui
    }
        public static void applyAndSync(ServerPlayer player,
                                     String race, String raceDisplay,
                                     int formIdx, int hairStyle, int ageIdx,
                                     int bodyTypeIdx, int noseIdx, int mouthIdx,
                                     int eyeIdx, int bodyColor, int hairColor,
                                     int eyeColor) {
        player.getCapability(RACE_CAP).ifPresent(cap -> {
            cap.setAll(race, raceDisplay, formIdx, hairStyle, ageIdx, bodyTypeIdx,
                noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor);

            var nbt = player.getPersistentData();
            nbt.putString("dbi:race",       race);
            nbt.putInt   ("dbi:bodyColor",  bodyColor);
            nbt.putInt   ("dbi:hairColor",  hairColor);
            nbt.putInt   ("dbi:eyeColor",   eyeColor);

            ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncRaceCapPacket(cap)
            );

            ModNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new SyncRaceCapPacket(cap)
            );
        });
    }

}
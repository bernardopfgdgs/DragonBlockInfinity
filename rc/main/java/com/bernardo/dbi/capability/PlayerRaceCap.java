ackage com.bernardo.dbi.capability;

import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.network.packet.SyncRaceCapPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class PlayerRaceCap implements INBTSerializable<CompoundTag> {

    public static final Capability<PlayerRaceCap> RACE_CAP =
        CapabilityManager.get(new CapabilityToken<>() {});

    private String race = "";
    private String raceDisplay = "";
    private int formIdx = 0, hairStyle = 0, ageIdx = 0;
    private int bodyTypeIdx = 0, noseIdx = 0, mouthIdx = 0, eyeIdx = 0;
    private int bodyColor = 0xFFD2956E;
    private int hairColor = 0xFF1A1A1A;
    private int eyeColor = 0xFF1A1A1A;

    public String getRace() { return race; }
    public String getRaceDisplay() { return raceDisplay; }
    public int getFormIdx() { return formIdx; }
    public int getHairStyle() { return hairStyle; }
    public int getAgeIdx() { return ageIdx; }
    public int getBodyTypeIdx() { return bodyTypeIdx; }
    public int getNoseIdx() { return noseIdx; }
    public int getMouthIdx() { return mouthIdx; }
    public int getEyeIdx() { return eyeIdx; }
    public int getBodyColor() { return bodyColor; }
    public int getHairColor() { return hairColor; }
    public int getEyeColor() { return eyeColor; }

    public void setAll(String race, String raceDisplay,
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("race", race);
        tag.putString("raceDisplay", raceDisplay);
        tag.putInt("formIdx", formIdx);
        tag.putInt("hairStyle", hairStyle);
        tag.putInt("ageIdx", ageIdx);
        tag.putInt("bodyTypeIdx", bodyTypeIdx);
        tag.putInt("noseIdx", noseIdx);
        tag.putInt("mouthIdx", mouthIdx);
        tag.putInt("eyeIdx", eyeIdx);
        tag.putInt("bodyColor", bodyColor);
        tag.putInt("hairColor", hairColor);
        tag.putInt("eyeColor", eyeColor);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        race = tag.getString("race");
        raceDisplay = tag.getString("raceDisplay");
        formIdx = tag.getInt("formIdx");
        hairStyle = tag.getInt("hairStyle");
        ageIdx = tag.getInt("ageIdx");
        bodyTypeIdx = tag.getInt("bodyTypeIdx");
        noseIdx = tag.getInt("noseIdx");
        mouthIdx = tag.getInt("mouthIdx");
        eyeIdx = tag.getInt("eyeIdx");
        bodyColor = tag.getInt("bodyColor");
        hairColor = tag.getInt("hairColor");
        eyeColor = tag.getInt("eyeColor");
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
            nbt.putString("dbi:race", race);
            nbt.putInt("dbi:bodyColor", bodyColor);
            nbt.putInt("dbi:hairColor", hairColor);
            nbt.putInt("dbi:eyeColor", eyeColor);

            ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncRaceCapPacket(cap)
            );
        });
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final PlayerRaceCap cap = new PlayerRaceCap();
        private final LazyOptional<PlayerRaceCap> opt = LazyOptional.of(() -> cap);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> c, Direction side) {
            return RACE_CAP.orEmpty(c, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            cap.deserializeNBT(tag);
        }
    }
}

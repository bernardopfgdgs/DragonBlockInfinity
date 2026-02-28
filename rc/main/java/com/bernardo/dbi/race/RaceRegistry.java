ackage com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusBaseMultiversal;
import com.bernardo.dbi.util.IdentifierUtil;
import java.util.HashMap;
import java.util.Map;

public class RaceRegistry {

    private static final Map<String, RaceData> RACES = new HashMap<>();

    public static void registerAll() {

        register(new RaceData(IdentifierUtil.id("sayajin"), Race.RaceType.Sayajin,
                new StatusBaseMultiversal(6, 4, 5, 3, 5, 4)));

        register(new RaceData(IdentifierUtil.id("namekian"), Race.RaceType.Namekian,
                new StatusBaseMultiversal(4, 5, 4, 6, 4, 5)));

        register(new RaceData(IdentifierUtil.id("half_sayajin"), Race.RaceType.Half_Sayajin,
                new StatusBaseMultiversal(5, 4, 4, 4, 4, 3)));

        register(new RaceData(IdentifierUtil.id("arconsian"), Race.RaceType.Arconsian,
                new StatusBaseMultiversal(7, 6, 6, 7, 6, 6)));

        register(new RaceData(IdentifierUtil.id("humano"), Race.RaceType.Humano,
                new StatusBaseMultiversal(5, 5, 5, 5, 5, 5)));

    }

    private static void register(RaceData data) {
        RACES.put(data.getId(), data);
    }

    public static RaceData getById(String id) {
        return RACES.get(id);
    }

}
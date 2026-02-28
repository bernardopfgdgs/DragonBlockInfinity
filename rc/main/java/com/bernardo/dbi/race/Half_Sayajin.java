ackage com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusMultiplier;

public class Half_Sayajin {

    public final String name = "Half_Sayajin";
    public final StatusMultiplier warrior;
    public final StatusMultiplier spiritualist;
    public final StatusMultiplier martialArts;

    public Half_Sayajin() {
        warrior = new StatusMultiplier(1.35f, 1.4f, 1.25f, 1.2f, 1.0f, 1.3f);
        spiritualist = new StatusMultiplier(1.15f, 1.3f, 1.25f, 1.4f, 1.0f, 1.45f);
        martialArts = new StatusMultiplier(1.3f, 1.3f, 1.25f, 1.3f, 1.0f, 1.3f);
    }

}

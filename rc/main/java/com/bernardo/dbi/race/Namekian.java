ackage com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusMultiplier;

public class Namekian {

    public final String name = "Namekian";
    public final StatusMultiplier warrior;
    public final StatusMultiplier spiritualist;
    public final StatusMultiplier martialArts;

    public Namekian() {
        warrior = new StatusMultiplier(1.2f, 1.3f, 1.5f, 1.1f, 1.0f, 1.3f);
        spiritualist = new StatusMultiplier(1.0f, 1.2f, 1.4f, 1.4f, 1.0f, 1.5f);
        martialArts = new StatusMultiplier(1.15f, 1.25f, 1.4f, 1.2f, 1.0f, 1.3f);
    }

}

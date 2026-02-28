ackage com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusMultiplier;

public class Humano {

    public final String name = "Humano";
    public final StatusMultiplier warrior;
    public final StatusMultiplier spiritualist;
    public final StatusMultiplier martialArts;

    public Humano() {
        warrior = new StatusMultiplier(1.3f, 1.3f, 1.2f, 1.1f, 1.0f, 1.1f);
        spiritualist = new StatusMultiplier(1.1f, 1.2f, 1.2f, 1.3f, 1.0f, 1.3f);
        martialArts = new StatusMultiplier(1.2f, 1.3f, 1.2f, 1.2f, 1.0f, 1.2f);
    }

}

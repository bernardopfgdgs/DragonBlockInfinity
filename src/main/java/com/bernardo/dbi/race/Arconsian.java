package com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusMultiplier;

public class Arconsian {

    public final String name = "Arconsian";
    public final StatusMultiplier warrior;
    public final StatusMultiplier spiritualist;
    public final StatusMultiplier martialArts;

    public Arconsian() {
        warrior = new StatusMultiplier(1.5f, 1.2f, 1.4f, 1.1f, 1.0f, 1.1f);
        spiritualist = new StatusMultiplier(1.3f, 1.1f, 1.3f, 1.2f, 1.0f, 1.2f);
        martialArts = new StatusMultiplier(1.4f, 1.2f, 1.3f, 1.15f, 1.0f, 1.15f);
    }

}

package com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusBaseMultiversal;

public class RaceRegistry {

    public static void registerAll() {

        register(new RaceData(Race.RaceType.Sayajin,
                new StatusBaseMultiversal(6, 4, 5, 3, 5, 4)));

        register(new RaceData(Race.RaceType.Namekian,
                new StatusBaseMultiversal(4, 5, 4, 6, 4, 5)));

        register(new RaceData(Race.RaceType.Half_Sayajin,
                new StatusBaseMultiversal(5, 4, 4, 4, 4, 3)));

        register(new RaceData(Race.RaceType.Arconsian,
                new StatusBaseMultiversal(7, 6, 6, 7, 6, 6)));

        register(new RaceData(Race.RaceType.Humano,
                new StatusBaseMultiversal(5, 5, 5, 5, 5, 5)));

    }

    private static void register(RaceData data) {
        // depois você liga isso no mapa/registry
    }

}
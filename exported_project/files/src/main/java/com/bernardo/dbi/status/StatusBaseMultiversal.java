package com.bernardo.dbi.status;

import java.util.EnumMap;
import java.util.Map;

public class StatusBaseMultiversal {

    public int str;
    public int dex;
    public int con;
    public int will;
    public int mnd;
    public int spi;

    public StatusBaseMultiversal(int str, int dex, int con, int will, int mnd, int spi) {
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.will = will;
        this.mnd = mnd;
        this.spi = spi;
    }

    public static final Map<Status.StatusType, Integer> BASE =
    new EnumMap<>(Status.StatusType.class);

    static {
        BASE.put(Status.StatusType.STR, 10);
        BASE.put(Status.StatusType.DEX, 10);
        BASE.put(Status.StatusType.CON, 10);
        BASE.put(Status.StatusType.WILL, 10);
        BASE.put(Status.StatusType.MND, 10);
        BASE.put(Status.StatusType.SPI, 10);
    }
}
package com.bernardo.dbi.status;

import java.util.Map;
import java.util.EnumMap;

public class StatusNames {

    public static final Map<Status.StatusType, String> NAMES = new EnumMap<>(Status.StatusType.class);

    static {
    NAMES.put(Status.StatusType.STR, "Força");
    NAMES.put(Status.StatusType.DEX, "Destreza");
    NAMES.put(Status.StatusType.CON, "Constituição");
    NAMES.put(Status.StatusType.WILL, "Vontade");
    NAMES.put(Status.StatusType.MND, "Mente");
    NAMES.put(Status.StatusType.SPI, "Espírito");
    }
}
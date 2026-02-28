package com.bernardo.dbi.util;

public final class IdentifierUtil {

    private static final String MODID = "dragonblockinfinity";

    private IdentifierUtil() {}

    public static String id(String name) {
        if (name == null) return MODID + ":";
        String n = name.toLowerCase().replace(' ', '_');
        return MODID + ":" + n;
    }
}

package com.bernardo.dbi.style;

import com.bernardo.dbi.util.IdentifierUtil;

public enum FightStyle {
    Warrior("warrior"),
    Spiritualist("spiritualist"),
    MartialArts("martial_arts");

    private final String id;

    FightStyle(String id) {
        this.id = IdentifierUtil.id(id);
    }

    public String getId() {
        return id;
    }
}
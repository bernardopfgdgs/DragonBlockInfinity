package com.bernardo.dbi.style;

import com.bernardo.dbi.style.data.MartialArts;
import com.bernardo.dbi.style.data.Spiritualist;
import com.bernardo.dbi.style.data.Warrior;
import java.util.HashMap;
import java.util.Map;

public class StyleRegistry {
    private static final Map<FightStyle, IFightStyle> styleMap = new HashMap<>();

    public static void registerAll() {
        register(FightStyle.Warrior, new Warrior());
        register(FightStyle.Spiritualist, new Spiritualist());
        register(FightStyle.MartialArts, new MartialArts());
    }

    private static void register(FightStyle style, IFightStyle implementation) {
        styleMap.put(style, implementation);
    }

    public static StyleData getStyleData(FightStyle style) {
        IFightStyle impl = styleMap.get(style);
        return impl != null ? impl.getStyleData() : new StyleData();
    }
}
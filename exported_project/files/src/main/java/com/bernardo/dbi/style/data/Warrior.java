package com.bernardo.dbi.style.data;

import com.bernardo.dbi.style.IFightStyle;
import com.bernardo.dbi.style.StyleData;

public class Warrior implements IFightStyle {
    @Override
    public StyleData getStyleData() {
        StyleData data = new StyleData();
        // configurar status base do guerreiro
        data.str += 3;
        data.con += 2;
        data.dex -= 1;
        return data;
    }
}

package com.bernardo.dbi.style.data;

import com.bernardo.dbi.style.IFightStyle;
import com.bernardo.dbi.style.StyleData;

public class Spiritualist implements IFightStyle {
    @Override
    public StyleData getStyleData() {
        StyleData data = new StyleData();
        // configurar status base do espiritualista
        data.spi += 3;
        data.mnd += 2;
        data.wil += 1;
        data.str -= 2;
        return data;
    }
}

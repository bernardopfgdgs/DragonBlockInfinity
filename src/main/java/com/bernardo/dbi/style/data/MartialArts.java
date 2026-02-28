package com.bernardo.dbi.style.data;

import com.bernardo.dbi.style.IFightStyle;
import com.bernardo.dbi.style.StyleData;

public class MartialArts implements IFightStyle {
    @Override
    public StyleData getStyleData() {
        StyleData data = new StyleData();
        // configurar status base das artes marciais
        data.dex += 3;
        data.con += 1;
        data.str += 1;
        data.wil -= 1;
        return data;
    }
}

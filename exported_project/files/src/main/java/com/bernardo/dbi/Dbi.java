package com.bernardo.dbi;

import net.minecraftforge.fml.common.Mod;

@Mod(Dbi.MOD_ID)
public class Dbi {

    public static final String MOD_ID = "dragonblockinfinity";

    public Dbi() {
        // registra tudo que é base do mod
        ModRegister.registerAll();
    }
}
package com.bernardo.dbi.client;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.screen.CharacterScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = Dbi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientEvents.CHARACTER_SCREEN_KEY.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new CharacterScreen());
            }
        }
    }
}

package com.bernardo.dbi.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.bernardo.dbi.Dbi;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DbiRaceRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    // cache for race textures to avoid recreation every frame
    private static final Map<String, ResourceLocation> RACE_TEXTURES = new ConcurrentHashMap<>();

    public DbiRaceRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buf, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        // don't draw if invisible or has no race defined
        if (player.isInvisible()) return;
        String race = player.getPersistentData().getString("dbi:race");
        if (race == null || race.isEmpty()) return;

        ResourceLocation texture = RACE_TEXTURES.computeIfAbsent(race, r ->
            new ResourceLocation(Dbi.MOD_ID, "textures/entity/race/" + r + ".png")
        );

        ps.pushPose();
        // slight scale-up to prevent z-fighting with base skin
        ps.scale(1.001f, 1.001f, 1.001f);

        // ensure model properties (pose, arm swing, etc.) are applied
        getParentModel().copyPropertiesTo(getParentModel());

        getParentModel().renderToBuffer(
            ps,
            buf.getBuffer(RenderType.entityTranslucent(texture)),
            light,
            OverlayTexture.NO_OVERLAY,
            1f, 1f, 1f, 1f
        );
        ps.popPose();
    }
}

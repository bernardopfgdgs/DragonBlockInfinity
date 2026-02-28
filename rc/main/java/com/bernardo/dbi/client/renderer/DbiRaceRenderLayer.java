package com.bernardo.dbi.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DbiRaceRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public DbiRaceRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buf, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        
        String race = player.getPersistentData().getString("dbi:race");
        if (race.isEmpty()) return;
        
        ResourceLocation texture = new ResourceLocation("dragonblockinfinity",
            "textures/entity/race/" + race + ".png");
        
        ps.pushPose();
        ps.scale(1.001f, 1.001f, 1.001f);
        
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

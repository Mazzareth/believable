package app.masterwork.believable.classes.prospector.client;

import app.masterwork.believable.classes.prospector.ProspectorService;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Renders ore outlines for Prospector Vision.
 */
public final class ProspectorVisionRenderer {
    private static final float OUTLINE_R = 0.2f;
    private static final float OUTLINE_G = 0.9f;
    private static final float OUTLINE_B = 0.4f;
    private static final float OUTLINE_A = 0.6f;

    private ProspectorVisionRenderer() {
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || !ProspectorClientState.isEnabled() || !ProspectorService.canUse(player)) {
            return;
        }
        if (ProspectorClientState.getCachedOres().isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for (BlockPos pos : ProspectorClientState.getCachedOres()) {
            AABB box = new AABB(pos).inflate(0.002);
            LevelRenderer.renderLineBox(poseStack, consumer, box, OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A);
        }
        poseStack.popPose();
        buffer.endBatch(RenderType.lines());
    }
}

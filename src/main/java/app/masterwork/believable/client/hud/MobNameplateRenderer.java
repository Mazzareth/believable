package app.masterwork.believable.client.hud;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.mob.MobStatStorage;
import app.masterwork.believable.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import org.joml.Matrix4f;

public final class MobNameplateRenderer {
    private static final int BAR_BACKGROUND = 0xAA202020;
    private static final int BAR_FILL = 0xFFC04040;
    private static final int BAR_BORDER = 0xFF000000;
    private static final float BAR_WIDTH = 60.0F;
    private static final float BAR_HEIGHT = 6.0F;
    private static final float BAR_Y_OFFSET = 10.0F;

    private MobNameplateRenderer() {
    }

    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player player) {
            applySubjugationNameTag(event, player);
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }
        LocalPlayer viewer = Minecraft.getInstance().player;
        if (viewer == null || !viewer.hasLineOfSight(living)) {
            return;
        }
        MobStatStorage storage = living.getData(ModAttachments.MOB_STATS);
        if (storage == null || !storage.isInitialized()) {
            return;
        }
        Component baseName = event.getOriginalContent();
        String label = storage.getHighestStatLabel();
        Component name = Component.literal("Lvl " + storage.getLevel() + " " + label + " ").append(baseName);
        event.setContent(name);
        event.setCanRender(TriState.TRUE);
    }

    private static void applySubjugationNameTag(RenderNameTagEvent event, Player player) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return;
        }
        if (storage.getSubjugatorId() == null) {
            return;
        }
        String subjugatorName = storage.getSubjugatorName();
        if (subjugatorName == null || subjugatorName.isBlank()) {
            return;
        }
        String displayName = storage.getSubjugatedName();
        if (displayName == null || displayName.isBlank()) {
            displayName = event.getOriginalContent().getString();
        }
        Component nameTag = Component.literal(displayName + " [Subjugated by " + subjugatorName + "]");
        event.setContent(nameTag);
        event.setCanRender(TriState.TRUE);
    }

    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }
        if (living instanceof Player) {
            return;
        }
        LocalPlayer viewer = Minecraft.getInstance().player;
        if (viewer == null || !viewer.hasLineOfSight(living)) {
            return;
        }
        MobStatStorage storage = living.getData(ModAttachments.MOB_STATS);
        if (storage == null || !storage.isInitialized()) {
            return;
        }
        double distance = Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(living);
        if (!ClientHooks.isNameplateInRenderDistance(living, distance)) {
            return;
        }

        float maxHealth = living.getMaxHealth();
        if (maxHealth <= 0.0F) {
            return;
        }
        float health = Mth.clamp(living.getHealth(), 0.0F, maxHealth);
        float ratio = health / maxHealth;
        String hpText = String.format("%d / %d", Mth.ceil(health), Mth.ceil(maxHealth));

        Vec3 attachment = living.getAttachments().getNullable(
            EntityAttachment.NAME_TAG,
            0,
            living.getViewYRot(event.getPartialTick())
        );
        if (attachment == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(attachment.x, attachment.y + 0.5, attachment.z);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);

        float left = -BAR_WIDTH / 2.0F;
        float right = left + BAR_WIDTH;
        float top = BAR_Y_OFFSET;
        float bottom = top + BAR_HEIGHT;
        float filledRight = left + BAR_WIDTH * ratio;

        Matrix4f matrix = poseStack.last().pose();
        MultiBufferSource buffers = event.getMultiBufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderType.gui());
        int light = event.getPackedLight();

        fill(matrix, buffer, left - 1, top - 1, right + 1, bottom + 1, BAR_BORDER, light);
        fill(matrix, buffer, left, top, right, bottom, BAR_BACKGROUND, light);
        if (filledRight > left) {
            fill(matrix, buffer, left, top, filledRight, bottom, BAR_FILL, light);
        }
        renderHpText(matrix, buffers, hpText, top, light);

        poseStack.popPose();
    }

    private static void renderHpText(Matrix4f matrix, MultiBufferSource buffers, String text, float top, int light) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        float x = -font.width(text) / 2.0F;
        float y = top + (BAR_HEIGHT - font.lineHeight) / 2.0F;
        float opacity = minecraft.options.getBackgroundOpacity(0.25F);
        int background = (int) (opacity * 255.0F) << 24;
        font.drawInBatch(
            text,
            x,
            y,
            0xFFE6E6E6,
            false,
            matrix,
            buffers,
            Font.DisplayMode.SEE_THROUGH,
            background,
            light
        );
    }

    private static void fill(
        Matrix4f matrix,
        VertexConsumer buffer,
        float minX,
        float minY,
        float maxX,
        float maxY,
        int color,
        int light
    ) {
        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minY > maxY) {
            float tmp = minY;
            minY = maxY;
            maxY = tmp;
        }

        buffer.addVertex(matrix, minX, minY, 0.0F).setColor(color).setLight(light);
        buffer.addVertex(matrix, minX, maxY, 0.0F).setColor(color).setLight(light);
        buffer.addVertex(matrix, maxX, maxY, 0.0F).setColor(color).setLight(light);
        buffer.addVertex(matrix, maxX, minY, 0.0F).setColor(color).setLight(light);
    }
}

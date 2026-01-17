package app.masterwork.believable.race;

import app.masterwork.believable.Believable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class GoatStepUpModifier implements RaceModifier {
    public static final String ID = "step_up";
    public static final GoatStepUpModifier INSTANCE = new GoatStepUpModifier();
    private static final float STEP_HEIGHT = 1.0F;
    private static final ResourceLocation STEP_UP_MODIFIER_ID =
        ResourceLocation.parse(Believable.MODID + ":step_up");

    private GoatStepUpModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Step Up";
    }

    @Override
    public String getDescription() {
        return "Steps up full blocks without jumping.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.STEP_HEIGHT);
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(STEP_UP_MODIFIER_ID);
        if (attribute.getValue() >= STEP_HEIGHT) {
            return;
        }
        double amount = STEP_HEIGHT - attribute.getValue();
        if (amount <= 0.0D) {
            return;
        }
        attribute.addTransientModifier(new AttributeModifier(
            STEP_UP_MODIFIER_ID,
            amount,
            AttributeModifier.Operation.ADD_VALUE
        ));
    }
}

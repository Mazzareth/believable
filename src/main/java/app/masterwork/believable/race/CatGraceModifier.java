package app.masterwork.believable.race;

import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public final class CatGraceModifier implements RaceModifier {
    public static final String ID = "grace";
    public static final CatGraceModifier INSTANCE = new CatGraceModifier();

    private CatGraceModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Grace";
    }

    @Override
    public String getDescription() {
        return "Negates fall damage.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onLivingFall(LivingFallEvent event) {
        event.setDistance(0.0F);
        event.setDamageMultiplier(0.0F);
    }
}

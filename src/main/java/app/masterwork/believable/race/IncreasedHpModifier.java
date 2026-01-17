package app.masterwork.believable.race;

public final class IncreasedHpModifier implements RaceModifier {
    public static final String ID = "increased_hp";
    public static final IncreasedHpModifier INSTANCE = new IncreasedHpModifier(4);

    private final int hpBonus;

    private IncreasedHpModifier(int hpBonus) {
        this.hpBonus = hpBonus;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Increased HP";
    }

    @Override
    public String getDescription() {
        return "Increases base HP.";
    }

    @Override
    public int getHpBonus() {
        return hpBonus;
    }
}

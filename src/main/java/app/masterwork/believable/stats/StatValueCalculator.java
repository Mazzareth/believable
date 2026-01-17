package app.masterwork.believable.stats;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.race.BasicRace;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class StatValueCalculator {
    private StatValueCalculator() {
    }

    public static double getCharacterStatLevel(CharacterStat stat, PlayerStatStorage storage, LivingEntity entity) {
        if (storage == null) {
            return stat.defaultValue();
        }
        return storage.getValue(stat.id(), stat.defaultValue());
    }

    public static double getStatBonus(Stat stat, PlayerStatStorage storage, LivingEntity entity) {
        if (storage == null) {
            return 0.0D;
        }
        double bonus = 0.0D;
        for (CharacterStat characterStat : CharacterStatRegistry.getAll()) {
            double level = getCharacterStatLevel(characterStat, storage, entity);
            double baseline = characterStat.defaultValue();
            double effectiveLevels = Math.max(0.0D, level - baseline);
            if (effectiveLevels <= 0.0D) {
                continue;
            }
            for (CharacterStatEffect effect : characterStat.effects()) {
                if (effect.statId().equals(stat.getId())) {
                    bonus += effect.perLevel() * effectiveLevels;
                }
            }
        }
        return bonus;
    }

    public static double getEffectiveStatValue(Stat stat, PlayerStatStorage storage, LivingEntity entity) {
        double baseValue = storage != null ? storage.getValue(stat, entity) : stat.getDefaultValue(entity);
        double bonus = getStatBonus(stat, storage, entity);
        double raceClassBonus = getRaceClassStatBonus(stat, entity);
        return stat.clamp(baseValue + bonus + raceClassBonus);
    }

    private static double getRaceClassStatBonus(Stat stat, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return 0.0D;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return 0.0D;
        }
        double bonus = 0.0D;
        BasicRace race = storage.getRace();
        if (race != null) {
            bonus += getStatModifierTotal(stat, race.statModifiers());
        }
        BasicClass playerClass = storage.getPlayerClass();
        if (playerClass != null) {
            bonus += getStatModifierTotal(stat, playerClass.statModifiers());
        }
        return bonus;
    }

    private static double getStatModifierTotal(Stat stat, Iterable<StatModifier> modifiers) {
        double bonus = 0.0D;
        for (StatModifier modifier : modifiers) {
            if (modifier.statId().equals(stat.getId())) {
                bonus += modifier.amount();
            }
        }
        return bonus;
    }
}

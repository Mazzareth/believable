package app.masterwork.believable.event;

import app.masterwork.believable.stats.AttributeStat;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

public final class ModAttributeEvents {
    private ModAttributeEvents() {
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        StatDefinitions.init();
        for (var stat : StatRegistry.getAll()) {
            if (!(stat instanceof AttributeStat attributeStat)) {
                continue;
            }
            Holder<Attribute> attribute = attributeStat.getAttribute();
            if (attribute == null) {
                continue;
            }
            event.add(EntityType.PLAYER, attribute);
        }
    }
}

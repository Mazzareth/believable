package app.masterwork.believable.stats;

import app.masterwork.believable.Believable;
import net.minecraft.resources.ResourceLocation;

public final class XpSources {
    public static final ResourceLocation VANILLA_ID = ResourceLocation.parse(Believable.MODID + ":vanilla");
    public static final ResourceLocation ORE_MINING_ID = ResourceLocation.parse(Believable.MODID + ":ore_mining");
    public static final ResourceLocation BIOME_DISCOVERY_ID = ResourceLocation.parse(Believable.MODID + ":biome_discovery");
    public static final ResourceLocation MOB_KILL_ID = ResourceLocation.parse(Believable.MODID + ":mob_kill");

    public static final XpSource VANILLA = () -> VANILLA_ID;
    public static final XpSource ORE_MINING = () -> ORE_MINING_ID;
    public static final XpSource BIOME_DISCOVERY = () -> BIOME_DISCOVERY_ID;
    public static final XpSource MOB_KILL = () -> MOB_KILL_ID;

    private XpSources() {
    }
}

package app.masterwork.believable.event;

import app.masterwork.believable.Believable;
import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.registry.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ModCommands {
    private ModCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(Believable.MODID)
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> resetCharacter(context.getSource(), EntityArgument.getPlayer(context, "player"))))));
    }

    private static int resetCharacter(CommandSourceStack source, ServerPlayer target) {
        PlayerAttachmentStorage storage = target.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            source.sendFailure(Component.literal("No character data is available for that player."));
            return 0;
        }
        boolean hadData = storage.getRace() != null || storage.getPlayerClass() != null;
        storage.setRace(null);
        storage.setPlayerClass(null);
        storage.setPartnerId(null);
        storage.setSubjugatorId(null);
        storage.setSubjugatorName(null);
        storage.setSubjugatedName(null);
        target.syncData(ModAttachments.PLAYER_STORAGE);
        if (hadData) {
            source.sendSuccess(() -> Component.literal("Reset character (race and class) for " + target.getGameProfile().getName() + "."), true);
            target.sendSystemMessage(Component.literal("Your race and class have been reset. Open the Unbelievable menu to choose again."));
        } else {
            source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + " already has no race or class selected."), false);
        }
        return 1;
    }
}

package app.masterwork.believable.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModPayloads {
    public static final String NETWORK_VERSION = "1";

    private ModPayloads() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
            .playToClient(
                BiomeDiscoveredPayload.TYPE,
                BiomeDiscoveredPayload.STREAM_CODEC,
                BiomeDiscoveredPayload::handle
            )
            .playToServer(
                OpenUnbelievableMenuPayload.TYPE,
                OpenUnbelievableMenuPayload.STREAM_CODEC,
                OpenUnbelievableMenuPayload::handle
            )
            .playToServer(
                OpenSalvageMenuPayload.TYPE,
                OpenSalvageMenuPayload.STREAM_CODEC,
                OpenSalvageMenuPayload::handle
            )
            .playToServer(
                ToggleVeinMinerPayload.TYPE,
                ToggleVeinMinerPayload.STREAM_CODEC,
                ToggleVeinMinerPayload::handle
            )
            .playToServer(
                ToggleProspectorVisionPayload.TYPE,
                ToggleProspectorVisionPayload.STREAM_CODEC,
                ToggleProspectorVisionPayload::handle
            )
            .playToServer(
                ToggleSmithingSalvagePayload.TYPE,
                ToggleSmithingSalvagePayload.STREAM_CODEC,
                ToggleSmithingSalvagePayload::handle
            )
            .playToServer(
                RenameWaypointPayload.TYPE,
                RenameWaypointPayload.STREAM_CODEC,
                RenameWaypointPayload::handle
            );
    }
}

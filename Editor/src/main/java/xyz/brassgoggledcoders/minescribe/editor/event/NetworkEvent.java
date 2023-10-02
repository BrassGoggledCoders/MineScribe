package xyz.brassgoggledcoders.minescribe.editor.event;

import javafx.event.Event;
import javafx.event.EventType;

public class NetworkEvent extends Event {
    public static final EventType<NetworkEvent> NETWORK_EVENT_TYPE = new EventType<>("network");

    public NetworkEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public static class ClientConnectedNetworkEvent extends NetworkEvent {
        public static final EventType<ClientConnectedNetworkEvent> CLIENT_CONNECTED_EVENT_TYPE = new EventType<>(
                NETWORK_EVENT_TYPE,
                "client_connected"
        );

        public ClientConnectedNetworkEvent() {
            super(CLIENT_CONNECTED_EVENT_TYPE);
        }
    }
}

package xyz.brassgoggledcoders.minescribe.editor.event;

import javafx.event.Event;
import javafx.event.EventType;

public class NetworkEvent extends Event {
    public static final EventType<NetworkEvent> NETWORK_EVENT_TYPE = new EventType<>("network");

    public NetworkEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public static class ClientConnectionNetworkEvent extends NetworkEvent {
        public static final EventType<ClientConnectionNetworkEvent> CLIENT_CONNECTED_EVENT_TYPE = new EventType<>(
                NETWORK_EVENT_TYPE,
                "client_connected"
        );

        private final ConnectionStatus status;

        public ClientConnectionNetworkEvent(ConnectionStatus status) {
            super(CLIENT_CONNECTED_EVENT_TYPE);
            this.status = status;
        }

        public ConnectionStatus getStatus() {
            return status;
        }
    }

    public enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED
    }
}

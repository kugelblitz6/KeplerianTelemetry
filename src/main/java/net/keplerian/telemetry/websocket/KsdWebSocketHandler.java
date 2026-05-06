package net.keplerian.telemetry.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.keplerian.telemetry.model.ObjectInfoMessage;
import net.keplerian.telemetry.model.SpaceObjectInput;
import net.keplerian.telemetry.model.TelemetryMessage;
import net.keplerian.telemetry.store.TelemetryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Component
public class KsdWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(KsdWebSocketHandler.class);

    private static final String QUERY_OBJECTS = "{\"type\":\"queryObjects\"}";

    private final ObjectMapper objectMapper;
    private final TelemetryStore store;

    public KsdWebSocketHandler(ObjectMapper objectMapper, TelemetryStore store) {
        this.objectMapper = objectMapper;
        this.store = store;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("KSD connected: {}", session.getId());
        session.sendMessage(new TextMessage(QUERY_OBJECTS));
        log.debug("Sent queryObjects to {}", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        JsonNode root = objectMapper.readTree(message.getPayload());

        String messageType = root.path("messageType").asText("");
        switch (messageType) {
            case "Telemetry"   -> handleTelemetry(objectMapper.treeToValue(root, TelemetryMessage.class));
            case "ObjectList"  -> handleObjectInfo(objectMapper.treeToValue(root, ObjectInfoMessage.class));
            default            -> log.warn("Unknown messageType '{}' from {}", messageType, session.getId());
        }
    }

    private void handleTelemetry(TelemetryMessage msg) {
        Instant time = Instant.ofEpochSecond(msg.currentTime());
        for (SpaceObjectInput o : msg.spaceObjects()) {
            store.putTelemetry(o.id(), o.position(), o.velocity(), time);
        }
        log.debug("Updated {} objects at t={}", msg.spaceObjects().size(), msg.currentTime());
    }

    private void handleObjectInfo(ObjectInfoMessage msg) {
        for (var info : msg.spaceObjects()) {
            store.putInfo(info);
        }
        log.info("Stored info for {} objects", msg.spaceObjects().size());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        log.info("KSD disconnected: {} ({})", session.getId(), status);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        log.error("Transport error [{}]: {}", session.getId(), exception.getMessage());
    }
}

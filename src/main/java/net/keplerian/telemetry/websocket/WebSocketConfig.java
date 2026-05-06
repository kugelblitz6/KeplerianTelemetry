package net.keplerian.telemetry.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final KsdWebSocketHandler ksdWebSocketHandler;

    public WebSocketConfig(KsdWebSocketHandler ksdWebSocketHandler) {
        this.ksdWebSocketHandler = ksdWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        if (ksdWebSocketHandler != null) {
            registry.addHandler(ksdWebSocketHandler, "/ksd")
                    .setAllowedOrigins("*");
        }
    }
}

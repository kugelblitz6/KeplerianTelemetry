package net.keplerian.telemetry.api;

import net.keplerian.telemetry.model.SpaceObject;
import net.keplerian.telemetry.model.TelemetryResponse;
import net.keplerian.telemetry.store.TelemetryStore;
import net.keplerian.telemetry.websocket.KsdWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TelemetryRestController {

    private final TelemetryStore store;
    private final KsdWebSocketHandler ksdWebSocketHandler;

    public TelemetryRestController(TelemetryStore store, KsdWebSocketHandler ksdWebSocketHandler) {
        this.store = store;
        this.ksdWebSocketHandler = ksdWebSocketHandler;
    }

    @GetMapping("/objects")
    public TelemetryResponse getAll() {
        ksdWebSocketHandler.requestObjectList();
        return new TelemetryResponse(store.getCurrentTime(), store.getAll());
    }

    @GetMapping("/objects/{id}")
    public ResponseEntity<SpaceObject> getById(@PathVariable long id) {
        return store.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

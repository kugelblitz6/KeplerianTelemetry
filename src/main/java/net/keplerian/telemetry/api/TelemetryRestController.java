package net.keplerian.telemetry.api;

import net.keplerian.telemetry.model.SpaceObject;
import net.keplerian.telemetry.store.TelemetryStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class TelemetryRestController {

    private final TelemetryStore store;

    public TelemetryRestController(TelemetryStore store) {
        this.store = store;
    }

    @GetMapping("/objects")
    public Collection<SpaceObject> getAll() {
        return store.getAll();
    }

    @GetMapping("/objects/{id}")
    public ResponseEntity<SpaceObject> getById(@PathVariable long id) {
        return store.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

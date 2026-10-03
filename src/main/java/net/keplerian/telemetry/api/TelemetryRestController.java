package net.keplerian.telemetry.api;

import net.keplerian.telemetry.model.SpaceObject;
import net.keplerian.telemetry.model.TelemetryResponse;
import net.keplerian.telemetry.store.TelemetryStore;
import net.keplerian.telemetry.websocket.KsdWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class TelemetryRestController {

    private final TelemetryStore store;
    private final KsdWebSocketHandler ksdWebSocketHandler;

    public TelemetryRestController(TelemetryStore store, KsdWebSocketHandler ksdWebSocketHandler) {
        this.store = store;
        this.ksdWebSocketHandler = ksdWebSocketHandler;
    }

    /**
     * @param orbits true のとき軌道線（orbitLegs）も返す。省略時は orbitRev だけを返す
     */
    @GetMapping("/objects")
    public TelemetryResponse getAll(@RequestParam(defaultValue = "false") boolean orbits) {
        ksdWebSocketHandler.requestObjectList();
        Collection<SpaceObject> objects = store.getAll();
        if (!orbits) {
            objects = objects.stream().map(SpaceObject::withoutOrbitLegs).toList();
        }
        return new TelemetryResponse(store.getCurrentTime(), objects);
    }

    /**
     * @param orbits true のとき軌道線（orbitLegs）も返す。省略時は orbitRev だけを返す
     */
    @GetMapping("/objects/{id}")
    public ResponseEntity<SpaceObject> getById(@PathVariable long id,
                                               @RequestParam(defaultValue = "false") boolean orbits) {
        return store.get(id)
                .map(o -> orbits ? o : o.withoutOrbitLegs())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

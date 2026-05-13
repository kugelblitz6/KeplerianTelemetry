package net.keplerian.telemetry.store;

import net.keplerian.telemetry.model.SpaceObject;
import net.keplerian.telemetry.model.SpaceObjectInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelemetryStore {

    private final Map<Long, SpaceObject> objects = new ConcurrentHashMap<>();

    public void putTelemetry(long id,
                             net.keplerian.telemetry.model.CartesianElements cart,
                             net.keplerian.telemetry.model.KeplerianElements kep,
                             java.time.Instant updatedAt) {
        objects.merge(id,
                new SpaceObject(id, null, null, null, cart, kep, updatedAt),
                (existing, incoming) -> existing.withTelemetry(cart, kep, updatedAt));
    }

    public void putInfo(SpaceObjectInfo info) {
        objects.merge(info.id(),
                new SpaceObject(info.id(), info.name(), info.type(), info.parentId(), null, null, null),
                (existing, incoming) -> existing.withInfo(info.name(), info.type(), info.parentId()));
    }

    public Collection<SpaceObject> getAll() {
        return Collections.unmodifiableCollection(objects.values());
    }

    public Optional<SpaceObject> get(long id) {
        return Optional.ofNullable(objects.get(id));
    }
}

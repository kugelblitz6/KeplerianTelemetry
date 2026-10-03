package net.keplerian.telemetry.store;

import net.keplerian.telemetry.model.OrbitLeg;
import net.keplerian.telemetry.model.SpaceObject;
import net.keplerian.telemetry.model.SpaceObjectInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelemetryStore {

    private final Map<Long, SpaceObject> objects = new ConcurrentHashMap<>();
    private volatile Long currentTime = null;

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void putTelemetry(long id,
                             net.keplerian.telemetry.model.CartesianElements cart,
                             net.keplerian.telemetry.model.KeplerianElements kep,
                             Long orbitRev,
                             List<OrbitLeg> orbitLegs) {
        objects.merge(id,
                new SpaceObject(id, null, null, null, null, cart, kep,
                        orbitLegs != null ? orbitRev : null, orbitLegs),
                (existing, incoming) -> existing.withTelemetry(cart, kep, orbitRev, orbitLegs));
    }

    public void putInfo(SpaceObjectInfo info) {
        objects.merge(info.id(),
                new SpaceObject(info.id(), info.name(), info.type(), info.parentId(), info.radius(), null, null, null, null),
                (existing, incoming) -> existing.withInfo(info.name(), info.type(), info.parentId(), info.radius()));
    }

    public Collection<SpaceObject> getAll() {
        return Collections.unmodifiableCollection(objects.values());
    }

    public Optional<SpaceObject> get(long id) {
        return Optional.ofNullable(objects.get(id));
    }
}

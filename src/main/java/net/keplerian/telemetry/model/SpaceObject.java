package net.keplerian.telemetry.model;

import java.time.Instant;

public record SpaceObject(
        long id,
        String name,
        String type,
        Long parentId,
        CartesianElements cart,
        KeplerianElements kep,
        Instant updatedAt
) {
    public SpaceObject withTelemetry(CartesianElements cart, KeplerianElements kep, Instant updatedAt) {
        return new SpaceObject(id, name, type, parentId, cart, kep, updatedAt);
    }

    public SpaceObject withInfo(String name, String type, long parentId) {
        return new SpaceObject(id, name, type, parentId, cart, kep, updatedAt);
    }
}

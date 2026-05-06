package net.keplerian.telemetry.model;

import java.time.Instant;

public record SpaceObject(
        long id,
        String name,
        String type,
        Long parentId,
        Vector3 position,
        Vector3 velocity,
        Instant updatedAt
) {
    public SpaceObject withTelemetry(Vector3 position, Vector3 velocity, Instant updatedAt) {
        return new SpaceObject(id, name, type, parentId, position, velocity, updatedAt);
    }

    public SpaceObject withInfo(String name, String type, long parentId) {
        return new SpaceObject(id, name, type, parentId, position, velocity, updatedAt);
    }
}

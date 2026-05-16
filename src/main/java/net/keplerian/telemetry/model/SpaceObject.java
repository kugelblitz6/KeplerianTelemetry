package net.keplerian.telemetry.model;

public record SpaceObject(
        long id,
        String name,
        String type,
        Long parentId,
        Double radius,
        CartesianElements cart,
        KeplerianElements kep
) {
    public SpaceObject withTelemetry(CartesianElements cart, KeplerianElements kep) {
        return new SpaceObject(id, name, type, parentId, radius, cart, kep);
    }

    public SpaceObject withInfo(String name, String type, long parentId, double radius) {
        return new SpaceObject(id, name, type, parentId, radius, cart, kep);
    }
}

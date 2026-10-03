package net.keplerian.telemetry.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record SpaceObject(
        long id,
        String name,
        String type,
        Long parentId,
        Double radius,
        CartesianElements cart,
        KeplerianElements kep,
        Long orbitRev,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<OrbitLeg> orbitLegs
) {
    /**
     * テレメトリを反映する。軌道線（orbitLegs）が付いていないテレメトリでは、
     * 保持している軌道線とその版（orbitRev）をそのまま残す。
     */
    public SpaceObject withTelemetry(CartesianElements cart, KeplerianElements kep, Long orbitRev, List<OrbitLeg> orbitLegs) {
        if (orbitLegs == null) {
            return new SpaceObject(id, name, type, parentId, radius, cart, kep, this.orbitRev, this.orbitLegs);
        }
        return new SpaceObject(id, name, type, parentId, radius, cart, kep, orbitRev, orbitLegs);
    }

    public SpaceObject withInfo(String name, String type, long parentId, double radius) {
        return new SpaceObject(id, name, type, parentId, radius, cart, kep, orbitRev, orbitLegs);
    }

    /** REST で軌道線を要求されなかったとき用。orbitRev は残す */
    public SpaceObject withoutOrbitLegs() {
        return new SpaceObject(id, name, type, parentId, radius, cart, kep, orbitRev, null);
    }
}

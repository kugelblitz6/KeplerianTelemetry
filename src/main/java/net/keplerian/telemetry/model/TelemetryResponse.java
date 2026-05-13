package net.keplerian.telemetry.model;

import java.util.Collection;

public record TelemetryResponse(Long currentTime, Collection<SpaceObject> objects) {}

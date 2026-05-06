package net.keplerian.telemetry.model;

import java.util.List;

public record TelemetryMessage(long currentTime, List<SpaceObjectInput> spaceObjects) {}

package net.keplerian.telemetry.model;

import java.util.List;

public record ObjectInfoMessage(List<SpaceObjectInfo> spaceObjects) {}

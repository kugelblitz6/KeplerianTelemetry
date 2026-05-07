package net.keplerian.telemetry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ObjectInfoMessage(List<SpaceObjectInfo> spaceObjects) {}

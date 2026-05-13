package net.keplerian.telemetry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartesianElements(Vector3 position, Vector3 velocity) {}

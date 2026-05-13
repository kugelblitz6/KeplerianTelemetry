package net.keplerian.telemetry.model;

public record SpaceObjectInput(long id, CartesianElements cart, KeplerianElements kep) {}

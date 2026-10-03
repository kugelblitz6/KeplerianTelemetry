package net.keplerian.telemetry.model;

import java.util.List;

/**
 * Telemetry の1オブジェクト分。orbitRev / orbitLegs は KSD 側で軌道線が変わったとき
 * （と接続後の最初の Telemetry）だけ付き、それ以外は null。
 */
public record SpaceObjectInput(long id, CartesianElements cart, KeplerianElements kep,
                               Long orbitRev, List<OrbitLeg> orbitLegs) {}

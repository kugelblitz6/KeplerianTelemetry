package net.keplerian.telemetry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeplerianElements(
        long   ep,    // Epoch: Unix time (秒)
        double a,     // 半長軸 (m)
        double e,     // 離心率
        double i,     // 傾斜角 (度)
        double raan,  // 昇交点経度 (度)
        double argp,  // 近点引数 (度)
        double ma     // 平均近点角 (度)
) {}

package net.keplerian.telemetry.model;

import java.util.List;

/**
 * 軌道線の1レッグ（SOI 遷移ごとに分かれる）。
 * 各点は親天体（parentId）の pos からの相対位置（メートル、pos と同じ座標軸）。
 * segments の各要素は途切れずに結ぶ点列 [[x, y, z], ...]。
 */
public record OrbitLeg(long parentId, List<List<double[]>> segments) {}

package wooteco.subway.maps.map.domain;

import java.util.function.Function;

import wooteco.subway.maps.line.domain.LineStation;

public enum PathType {
    DISTANCE(lineStation -> lineStation.getDistance()),

    DURATION(lineStation -> lineStation.getDuration()),

    ARRIVAL_TIME(lineStation -> lineStation.getDuration());

    private Function<LineStation, Integer> expression;

    PathType(Function<LineStation, Integer> expression) {
        this.expression = expression;
    }

    public int findWeightOf(LineStation lineStation) {
        return expression.apply(lineStation);
    }
}

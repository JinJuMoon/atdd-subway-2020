package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.maps.line.domain.LineStation;

class SubwayPathTest {
    private SubwayPath subwayPath;

    @DisplayName("10km 이하 경로의 요금을 계산한다.")
    @Test
    void calculateTotalFare() {
        LineStation lineStation1 = new LineStation(1L, null, 0, 0);
        LineStation lineStation2 = new LineStation(2L, 1L, 10, 4);

        subwayPath = new SubwayPath(Arrays.asList(
            new LineStationEdge(lineStation1,1L),
            new LineStationEdge(lineStation2,1L)
        ));

        assertThat(subwayPath.calculateFare()).isEqualTo(1250);
    }

    @DisplayName("10km 초과 50km 이하 경로의 요금을 계산한다.")
    @Test
    void calculateTotalFareOverTen() {
        LineStation lineStation1 = new LineStation(1L, null, 0, 0);
        LineStation lineStation2 = new LineStation(2L, 1L, 10, 4);
        LineStation lineStation3 = new LineStation(3L, 2L, 10, 5);

        subwayPath = new SubwayPath(Arrays.asList(
            new LineStationEdge(lineStation1,1L),
            new LineStationEdge(lineStation2,1L),
            new LineStationEdge(lineStation3,1L)
        ));

        assertThat(subwayPath.calculateFare()).isEqualTo(1450);
    }

    @DisplayName("50km 초과 경로의 요금을 계산한다.")
    @Test
    void calculateTotalFareOverFifty() {
        LineStation lineStation1 = new LineStation(1L, null, 0, 0);
        LineStation lineStation2 = new LineStation(2L, 1L, 30, 4);
        LineStation lineStation3 = new LineStation(3L, 2L, 40, 5);

        subwayPath = new SubwayPath(Arrays.asList(
            new LineStationEdge(lineStation1,1L),
            new LineStationEdge(lineStation2,1L),
            new LineStationEdge(lineStation3,1L)
        ));

        assertThat(subwayPath.calculateFare()).isEqualTo(2050);
    }
}
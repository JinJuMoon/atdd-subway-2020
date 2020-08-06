package wooteco.subway.maps.map.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static wooteco.subway.common.TestObjectUtils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;
import wooteco.subway.common.TestObjectUtils;
import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.map.domain.LineStationEdge;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.map.dto.MapResponse;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.station.application.StationService;
import wooteco.subway.maps.station.domain.Station;
import wooteco.subway.members.member.domain.LoginMember;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {
    private MapService mapService;
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @Mock
    private PathService pathService;

    private Map<Long, Station> stations;
    private List<Line> lines;

    private SubwayPath subwayPath;

    @BeforeEach
    void setUp() {
        stations = new HashMap<>();
        stations.put(1L, TestObjectUtils.createStation(1L, "교대역"));
        stations.put(2L, TestObjectUtils.createStation(2L, "강남역"));
        stations.put(3L, TestObjectUtils.createStation(3L, "양재역"));
        stations.put(4L, TestObjectUtils.createStation(4L, "남부터미널역"));

        Line line1 = TestObjectUtils.createLine(1L, "2호선", "GREEN");
        line1.addLineStation(new LineStation(1L, null, 0, 0));
        LineStation lineStation2 = new LineStation(2L, 1L, 2, 2);
        line1.addLineStation(new LineStation(2L, 1L, 2, 2));

        Line line2 = TestObjectUtils.createLine(2L, "신분당선", "RED");
        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(3L, 2L, 2, 1));

        Line line3 = TestObjectUtils.createLine(3L, "3호선", "ORANGE");
        line3.addLineStation(new LineStation(1L, null, 0, 0));
        LineStation lineStation6 = new LineStation(4L, 1L, 1, 2);
        LineStation lineStation7 = new LineStation(3L, 4L, 2, 2);
        line3.addLineStation(lineStation6);
        line3.addLineStation(lineStation7);

        lines = Lists.newArrayList(line1, line2, line3);

        List<LineStationEdge> lineStations = Lists.newArrayList(
                new LineStationEdge(lineStation6, line3.getId()),
                new LineStationEdge(lineStation7, line3.getId())
        );
        subwayPath = new SubwayPath(lineStations);

        mapService = new MapService(lineService, stationService, pathService);
    }

    @Test
    void findPath() {
        when(lineService.findLines()).thenReturn(lines);
        when(pathService.findPath(anyList(), anyLong(), anyLong(), any())).thenReturn(subwayPath);
        when(stationService.findStationsByIds(anyList())).thenReturn(stations);

        PathResponse pathResponse = mapService.findPath(1L, 3L, PathType.DISTANCE,  createLoginMember(17));

        assertThat(pathResponse.getStations()).isNotEmpty();
        assertThat(pathResponse.getDuration()).isNotZero();
        assertThat(pathResponse.getDistance()).isNotZero();
    }

    @Test
    void findMap() {
        when(lineService.findLines()).thenReturn(lines);
        when(stationService.findStationsByIds(anyList())).thenReturn(stations);

        MapResponse mapResponse = mapService.findMap();

        assertThat(mapResponse.getLineResponses()).hasSize(3);

    }

    @DisplayName("10km 이하 경로의 요금을 계산한다.")
    @Test
    void calculateTotalFare() {
        LineStation lineStation1 = new LineStation(1L, null, 0, 0);
        LineStation lineStation2 = new LineStation(2L, 1L, 10, 4);

        subwayPath = new SubwayPath(Arrays.asList(
            new LineStationEdge(lineStation1,1L),
            new LineStationEdge(lineStation2,1L)
        ));

        LoginMember loginMember = createLoginMember(25);

        assertThat(mapService.calculateFare(subwayPath, loginMember.getAge())).isEqualTo(1250);
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

        LoginMember loginMember = createLoginMember(25);

        assertThat(mapService.calculateFare(subwayPath, loginMember.getAge())).isEqualTo(1450);
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

        LoginMember loginMember = createLoginMember(25);

        assertThat(mapService.calculateFare(subwayPath, loginMember.getAge())).isEqualTo(2050);
    }
}

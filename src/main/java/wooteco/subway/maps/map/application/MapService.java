package wooteco.subway.maps.map.application;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.line.dto.LineStationResponse;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.map.dto.MapResponse;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.dto.PathResponseAssembler;
import wooteco.subway.maps.station.application.StationService;
import wooteco.subway.maps.station.domain.Station;
import wooteco.subway.maps.station.dto.StationResponse;
import wooteco.subway.members.member.domain.LoginMember;

@Service
@Transactional
public class MapService {
    private LineService lineService;
    private StationService stationService;
    private PathService pathService;

    public MapService(LineService lineService, StationService stationService, PathService pathService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathService = pathService;
    }

    public MapResponse findMap() {
        List<Line> lines = lineService.findLines();
        Map<Long, Station> stations = findStations(lines);

        List<LineResponse> lineResponses = lines.stream()
                .map(it -> LineResponse.of(it, extractLineStationResponses(it, stations)))
                .collect(Collectors.toList());

        return new MapResponse(lineResponses);
    }

    public PathResponse findPath(Long source, Long target, PathType type,
        final LoginMember loginMember) {
        List<Line> allLines = lineService.findLines();
        SubwayPath subwayPath = pathService.findPath(allLines, source, target, type);
        Map<Long, Station> stations = stationService.findStationsByIds(subwayPath.extractStationId());
        int totalFare = calculateFare(subwayPath, loginMember.getAge());

        return PathResponseAssembler.assemble(subwayPath, stations, totalFare);
    }

    public int calculateFare(final SubwayPath subwayPath, final Integer age) {
        int distanceFare = calculateDistanceFare(subwayPath);
        int lineFare = lineService.findMostExpensiveFare(subwayPath.extractLineId());
        int fare = Collections.max(Arrays.asList(distanceFare, lineFare));

        return discountByAge(age, fare);
    }

    private int calculateDistanceFare(final SubwayPath subwayPath) {
        int defaultFare = 1250;
        int overDistance = subwayPath.calculateDistance() - 10;

        if (overDistance > 40) {
            return defaultFare + (int) ((Math.ceil((overDistance - 1) / 8) + 1) * 100);
        }

        if (0 < overDistance) {
            return defaultFare + (int) ((Math.ceil((overDistance - 1) / 5) + 1) * 100);
        }

        return defaultFare;
    }

    private Map<Long, Station> findStations(List<Line> lines) {
        List<Long> stationIds = lines.stream()
                .flatMap(it -> it.getStationInOrder().stream())
                .map(it -> it.getStationId())
                .collect(Collectors.toList());

        return stationService.findStationsByIds(stationIds);
    }

    private int discountByAge(final Integer age, final int fare) {
        if (13 <= age && age < 19) {
            return (int)Math.floor((fare - 350) * 0.8);
        }

        if (6 <= age && age < 13) {
            return (int)Math.floor((fare - 350) * 0.5);
        }

        return fare;
    }

    private List<LineStationResponse> extractLineStationResponses(Line line, Map<Long, Station> stations) {
        return line.getStationInOrder().stream()
                .map(it -> LineStationResponse.of(line.getId(), it, StationResponse.of(stations.get(it.getStationId()))))
                .collect(Collectors.toList());
    }
}

package wooteco.subway.maps.map.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.graph.Multigraph;

import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;

public class SubwayGraphK extends Multigraph<Long, LineStationEdge> {
    public SubwayGraphK(Class edgeClass) {
        super(edgeClass);
    }

    public void addVertexWith(List<Line> lines) {
        // 지하철 역(정점)을 등록
        lines.stream()
            .flatMap(it -> it.getStationInOrder().stream())
            .map(it -> it.getStationId())
            .distinct()
            .collect(Collectors.toList())
            .forEach(it -> addVertex(it));
    }

    public void addEdge(List<Line> lines, PathType type) {
        // 지하철 역의 연결 정보(간선)을 등록
        for (Line line : lines) {
            line.getStationInOrder().stream()
                .filter(it -> it.getPreStationId() != null)
                .forEach(it -> addEdge(type, it, line));
        }
    }

    private void addEdge(PathType type, LineStation lineStation, Line line) {
        LineStationEdge lineStationEdge = new LineStationEdge(lineStation, line.getId());
        addEdge(lineStation.getPreStationId(), lineStation.getStationId(), lineStationEdge);
        setEdgeWeight(lineStationEdge, type.findWeightOf(lineStation));
    }
}

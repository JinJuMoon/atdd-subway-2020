package wooteco.subway.maps.map.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.graph.WeightedMultigraph;

import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;

public class SubwayGraphDijks extends WeightedMultigraph<Long, LineStationEdge> {
    public SubwayGraphDijks(Class edgeClass, List<Line> lines, PathType type) {
        super(edgeClass);
        this.addVertexWith(lines);
        this.addEdge(lines, type);
    }

    private void addVertexWith(List<Line> lines) {
        lines.stream()
                .flatMap(it -> it.getStationInOrder().stream())
                .map(it -> it.getStationId())
                .distinct()
                .collect(Collectors.toList())
                .forEach(it -> addVertex(it));
    }

    private void addEdge(List<Line> lines, PathType type) {
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

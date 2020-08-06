package wooteco.subway.maps.map.application;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.springframework.stereotype.Service;

import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.map.domain.LineStationEdge;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayGraphDijks;
import wooteco.subway.maps.map.domain.SubwayGraphK;
import wooteco.subway.maps.map.domain.SubwayPath;

@Service
public class PathService {
    public SubwayPath findPath(List<Line> lines, Long source, Long target, PathType type) {
        if (type == PathType.ARRIVAL_TIME) {
            return findPathWithTime(lines, source, target, type);
        }

        return findPathWithoutTime(lines, source, target, type);
    }

    private SubwayPath findPathWithTime(List<Line> lines, Long source, Long target, PathType type) {
        SubwayGraphK graph = new SubwayGraphK(LineStationEdge.class, lines, type);
        List<GraphPath> paths = new KShortestPaths(graph, 1000).getPaths(source, target);
        List<SubwayPath> subwayPaths = paths.stream().map(this::convertSubwayPath).collect(Collectors.toList());

        return findBestPath(subwayPaths);
    }

    private SubwayPath findBestPath(final List<SubwayPath> subwayPaths) {
        return subwayPaths.get(0);
    }

    private SubwayPath findPathWithoutTime(final List<Line> lines, final Long source,
        final Long target,
        final PathType type) {
        SubwayGraphDijks graph = new SubwayGraphDijks(LineStationEdge.class, lines, type);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(source, target);

        return convertSubwayPath(path);
    }

    private SubwayPath convertSubwayPath(GraphPath graphPath) {
        return new SubwayPath((List<LineStationEdge>)graphPath.getEdgeList().stream().collect(Collectors.toList()));
    }
}

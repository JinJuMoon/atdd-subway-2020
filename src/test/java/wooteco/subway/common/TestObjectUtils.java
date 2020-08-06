package wooteco.subway.common;

import java.time.LocalTime;

import org.springframework.test.util.ReflectionTestUtils;

import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.station.domain.Station;
import wooteco.subway.members.member.domain.LoginMember;

public class TestObjectUtils {
    public static Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }

    public static Line createLine(Long id, String name, String color) {
        Line line1 = new Line(name, color, LocalTime.of(05, 30), LocalTime.of(23, 30), 10, 0);
        ReflectionTestUtils.setField(line1, "id", id);
        return line1;
    }

    public static Line createLine(Long id, String name, String color, int extraFare) {
        Line line1 = new Line(name, color, LocalTime.of(05, 30), LocalTime.of(23, 30), 10, extraFare);
        ReflectionTestUtils.setField(line1, "id", id);
        return line1;
    }

    public static LoginMember createLoginMember(int age) {
        return new LoginMember(1L,"abc@emial.com", "1234", age);
    }
}

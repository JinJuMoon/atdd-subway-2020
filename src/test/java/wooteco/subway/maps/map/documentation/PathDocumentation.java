package wooteco.subway.maps.map.documentation;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;

import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;
import wooteco.subway.members.member.domain.LoginMember;

@WebMvcTest(controllers = {MapController.class})
public class PathDocumentation extends Documentation {
    @Autowired
    private MapController mapController;
    @MockBean
    private MapService mapService;

    protected TokenResponse tokenResponse;

    @BeforeEach
    public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        super.setUp(context, restDocumentation);
        tokenResponse = new TokenResponse("token");
    }

    @Test
    void findPath() {
        List<StationResponse> stationResponses = Arrays.asList(
            new StationResponse(1L, "광교중앙역", LocalDateTime.now(), LocalDateTime.now()),
            new StationResponse(2L, "잠실역", LocalDateTime.now(), LocalDateTime.now()),
            new StationResponse(3L, "강남역", LocalDateTime.now(), LocalDateTime.now()),
            new StationResponse(4L, "역삼역", LocalDateTime.now(), LocalDateTime.now())
        );
        PathResponse pathResponse = new PathResponse(stationResponses, 5, 10, 1250);
        when(mapService.findPath(anyLong(), anyLong(), any(PathType.class), any(LoginMember.class))).thenReturn(
            pathResponse);

        given().log().all().
            header("Authorization", "Bearer " + tokenResponse.getAccessToken()).
            accept(MediaType.APPLICATION_JSON_VALUE).
            param("source", 1).
            param("target", 2).
            param("type", "DISTANCE").
            when().
            get("/paths").
            then().
            log().all().
            apply(document("paths/find-path",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer auth credentials")),
                responseFields(
                    fieldWithPath("stations").type(JsonFieldType.ARRAY).description("조회된 경로의 역 목록"),
                    fieldWithPath("stations.[].id").type(JsonFieldType.NUMBER).description("조회된 경로의 역의 id"),
                    fieldWithPath("stations.[].name").type(JsonFieldType.STRING).description("조회된 경로의 역의 이름"),
                    fieldWithPath("duration").type(JsonFieldType.NUMBER).description("경로의 소요 시간"),
                    fieldWithPath("distance").type(JsonFieldType.NUMBER).description("경로의 거리"),
                    fieldWithPath("fare").type(JsonFieldType.NUMBER).description("경로의 요금")
                ))).
            extract();
    }
}
